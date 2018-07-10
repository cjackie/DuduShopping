package com.dudu.chat;

import com.dudu.common.StandardObjectMapper;
import com.dudu.database.DBHelper;
import com.dudu.database.StoredProcedure;
import com.dudu.database.ZetaMap;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * We use SQL Server to store data.
 *
 * We deploy write through policy for managing new messages.
 * New messages are written into both SQL server and redis
 */
public class PersistentRedisChatRoom extends RedisChatRoom {
    private static final Logger logger = LogManager.getLogger(PersistentRedisChatRoom.class);
    private static final ObjectMapper objectMapper = StandardObjectMapper.getInstance();

    private static ChatParticipant NULL_PARTICIPANT = new ChatParticipant() {{
        setParticipantId(-520);
    }};

    private static ChatMessage NULL_MESSAGE = new ChatMessage() {{
        setMessageId(-520);
    }};

    private DataSource source;
    // time to live
    private int ttl = 2*60*60;

    private BlockingQueue<Runnable> workingQueue;
    private ThreadPoolExecutor threadPoolExecutor;
    private MonitorThread monitorThread;

    /**
     * load from an existing room
     *
     * @param roomId
     * @param jedisPool
     * @param source where messages will be stored.
     * @throws Exception
     */
    public PersistentRedisChatRoom(long roomId, JedisPool jedisPool, DataSource source) {
        super(String.valueOf(roomId), jedisPool);
        this.source = source;
        this.workingQueue = new ArrayBlockingQueue<>(100);
        this.threadPoolExecutor = new ThreadPoolExecutor(1, 4, 1, TimeUnit.MINUTES, workingQueue);
        this.monitorThread = new MonitorThread();
        this.monitorThread.start();

        checkCache();
    }

    /**
     * create a new room
     *
     * @param jedisPool
     * @param source
     * @param roomName
     * @throws Exception
     */
    public PersistentRedisChatRoom(JedisPool jedisPool, DataSource source, String roomName) throws Exception {
        super(jedisPool);
        this.source = source;
        this.workingQueue = new ArrayBlockingQueue<>(100);
        this.threadPoolExecutor = new ThreadPoolExecutor(1, 4, 1, TimeUnit.MINUTES, workingQueue);
        this.monitorThread = new MonitorThread();
        this.monitorThread.start();

        try (Connection conn = source.getConnection();
             Jedis jedis = jedisPool.getResource()) {

            // ask for a room id from database
            StoredProcedure sp = new StoredProcedure(conn, "sp_ChatRoomCreate");
            sp.addParameter("ChatRoomName", roomName);
            List<ZetaMap> zmaps = sp.execToZetaMaps();
            long roomId = zmaps.get(0).getLong("RoomId", 0);
            if (roomId == 0) {
                logger.error("Failed to create a new room");
                throw new IllegalArgumentException("");
            }

            // move to the newly created room
            this.setRoomId(String.valueOf(roomId));

            // set up cache of this room
            jedis.hset(redisKeyParticipants(), String.valueOf(NULL_PARTICIPANT.getParticipantId()), objectMapper.writeValueAsString(NULL_PARTICIPANT));
            jedis.expire(redisKeyParticipants(), ttl);

            jedis.lpush(redisKeyMessages(), objectMapper.writeValueAsString(NULL_MESSAGE));
            jedis.expire(redisKeyMessages(), ttl);
        }
    }

    /**
     *
     * @param participant
     */
    @Override
    public void join(ChatParticipant participant) {
        checkCache();
        if (participant.getParticipantId() == 0) {
            // join in database
            try (Connection conn = source.getConnection()) {
                StoredProcedure sp = new StoredProcedure(conn, "sp_ChatRoomUserJoin");
                sp.addParameter("RoomId", participant.getRoomId());
                sp.addParameter("UserId", participant.getUserId());
                List<ZetaMap> zetaMaps = sp.execToZetaMaps();

                int error = zetaMaps.get(0).getInt("Error", -1);
                if (error != 0)
                    throw new RuntimeException("sp_ChatRoomUserJoin: Error=" + error);

                participant = ChatParticipant.from(zetaMaps.get(0));
            } catch (Exception e) {
                logger.error("Failed to join the user: UserId=" + participant.getUserId() + ", RoomId=" + participant.getRoomId());
                return;
            }
        }

        super.join(participant);
    }

    @Override
    public void exit(ChatParticipant participant) {
        checkCache();
        if (participant.getParticipantId() != 0) {
            try (Connection conn = source.getConnection()) {
                StoredProcedure sp = new StoredProcedure(conn, "sp_ChatRoomUserExit");
                sp.addParameter("ParticipantId", participant.getParticipantId());
                int error = sp.execToZetaMaps().get(0).getInt("Error", -1);
                if (error != 0)
                    throw new RuntimeException("sp_ChatRoomUserExit: Error=" + error);

            } catch (Exception e) {
                logger.error("Failed to exit the user: ParticipantId="+participant.getParticipantId());
                return;
            }
        }
        super.exit(participant);
    }

    @Override
    public void publish(ChatMessage message) {
        checkCache();
        super.publish(message);

        // asynchronous write to database
        threadPoolExecutor.execute(() -> {
            try (Connection conn = source.getConnection()) {
                StoredProcedure sp = new StoredProcedure(conn, "sp_ChatRoomNewMessage");

                sp.addParameter("PariticpantId", message.getParticipantId());
                sp.addParameter("Message", message.getMessage());
                sp.addParameter("CreatedAt", message.getCreatedAt());
                int error = sp.execToZetaMaps().get(0).getInt("Error", -1);
                if (error != 0)
                    throw new RuntimeException("sp_ChatRoomNewMessage. Error=" + error);

            } catch (Exception e) {
                logger.error("Failed to save a message to sql server: ", e);
            }
        });
    }

    /**
     * MUST invoke this before method calls that use cache.
     *
     * check if room is in the cache. bring to cache if it is a missing hit.
     */
    private void checkCache() {
        try (Connection conn =  source.getConnection();
             Jedis jedis = jedisPool.getResource()) {

            if (!jedis.exists(redisKeyParticipants())) {
                jedis.hset(redisKeyParticipants(), String.valueOf(NULL_PARTICIPANT.getParticipantId()), objectMapper.writeValueAsString(NULL_PARTICIPANT));
                jedis.expire(redisKeyParticipants(), ttl);

                // participants from database
                String select = "SELECT * FROM ChatRoomParticipants WHERE RoomId = ? AND Exited = 0";
                List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, select, roomId);

                for (ZetaMap zetaMap : zetaMaps) {
                    ChatParticipant participant = ChatParticipant.from(zetaMap);
                    jedis.hset(redisKeyParticipants(), String.valueOf(participant.getParticipantId()), objectMapper.writeValueAsString(participant));
                }
            }

            if (!jedis.exists(redisKeyMessages())) {
                jedis.lpush(redisKeyMessages(), objectMapper.writeValueAsString(NULL_MESSAGE));
                jedis.expire(redisKeyMessages(), ttl);

                // messages from database
                String select =
                        "SELECT m.* FROM ChatRoomMessages m " +
                        "  INNER JOIN ChatRoomParticipants p ON p.ParticipantId = m.ParticipantId " +
                        "  INNER JOIN ChatRooms r ON r.RoomId = p.RoomId " +
                        "WHERE r.RoomId = ?";
                List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, select, roomId);

                for (ZetaMap zetaMap: zetaMaps) {
                    ChatMessage chatMessage = ChatMessage.from(zetaMap);
                    jedis.lpush(redisKeyMessages(), objectMapper.writeValueAsString(chatMessage));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check on cache.");
        }
    }

    @Override
    public List<ChatMessage> getAllMessages() {
        checkCache();

        List<ChatMessage> messages = super.getAllMessages();
        messages.removeIf((msg) -> msg.getMessageId() == NULL_MESSAGE.getMessageId());
        return messages;
    }

    @Override
    public List<ChatParticipant> getAllParticipants() {
        checkCache();

        List<ChatParticipant> participants = super.getAllParticipants();
        participants.removeIf((p) -> p.getParticipantId() == NULL_PARTICIPANT.getParticipantId());
        return participants;
    }

    public ChatParticipant createNewParticipant(long userId) {
        ChatParticipant chatParticipant = new ChatParticipant();
        chatParticipant.setUserId(userId);
        chatParticipant.setRoomId(Long.parseLong(roomId));
        return chatParticipant;
    }

    public ChatMessage createNewMessage(long participantId, String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setParticipantId(participantId);
        chatMessage.setMessage(message);
        chatMessage.setCreatedAt(new Date());
        return chatMessage;
    }

    public void close() {
        super.close();
        monitorThread.stop = true;
    }

    private class MonitorThread extends Thread {
        private boolean stop = false;

        @Override
        public void run() {
            super.run();

            try {
                while (!stop) {
                    sleep(5000);
                    logger.info("Number of messages queued up: " + workingQueue.size());
                }
            } catch (Exception ignored) { }
        }
    }
}

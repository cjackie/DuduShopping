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
import java.util.List;

/**
 * We use SQL Server to store data.
 *
 * We deploy write through policy for managing new messages.
 * New messages are written into both SQL server and redis
 */
public class PersistentRedisChatRoom extends RedisChatRoom {
    private static final Logger logger = LogManager.getLogger(PersistentRedisChatRoom.class);
    private static final ObjectMapper objectMapper = StandardObjectMapper.getInstance();

    private static RedisChatRoomParticipant NULL_PARTICIPANT = new RedisChatRoomParticipant() {{
        setParticipantId(-520);
    }};

    private static ChatMessage NULL_MESSAGE = new ChatMessage() {{
        setMessageId(-520);
    }};

    private DataSource source;
    // time to live
    private int ttl = 2*60*60;


    /**
     * load from an existing room
     *
     * @param roomId
     * @param jedisPool
     * @param source where messages will be stored.
     * @throws Exception
     */
    public PersistentRedisChatRoom(String roomId, JedisPool jedisPool, DataSource source) throws Exception {
        super(roomId, jedisPool);
        this.source = source;

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
            jedis.hset(redisKeyParticipants(), NULL_PARTICIPANT.getChatParticipantId(), objectMapper.writeValueAsString(NULL_PARTICIPANT));
            jedis.expire(redisKeyParticipants(), ttl);

            jedis.lpush(redisKeyMessages(), objectMapper.writeValueAsString(NULL_MESSAGE));
            jedis.expire(redisKeyMessages(), ttl);
        }
    }


    @Override
    public void join(ChatParticipant participant) {
        checkCache();
        super.join(participant);
    }

    @Override
    public void exit(ChatParticipant participant) {
        checkCache();
        super.exit(participant);
    }

    @Override
    public void publish(ChatMessage message) {
        checkCache();
        super.publish(message);
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
                jedis.hset(redisKeyParticipants(), NULL_PARTICIPANT.getChatParticipantId(), objectMapper.writeValueAsString(NULL_PARTICIPANT));
                jedis.expire(redisKeyParticipants(), ttl);

                // participants from database
                String select = "SELECT * FROM ChatRoomParticipants WHERE RoomId = ? AND Exited = 0";
                List<ZetaMap> zetaMaps = DBHelper.getHelper().execToZetaMaps(conn, select, roomId);

                for (ZetaMap zetaMap : zetaMaps) {
                    RedisChatRoomParticipant participant = RedisChatRoomParticipant.from(zetaMap);
                    jedis.hset(redisKeyParticipants(), participant.getChatParticipantId(), objectMapper.writeValueAsString(participant));
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
    public List<RedisChatRoomParticipant> getAllParticipants() {
        checkCache();

        List<RedisChatRoomParticipant> participants = super.getAllParticipants();
        participants.removeIf((p) -> p.getChatParticipantId().equals(NULL_PARTICIPANT.getChatParticipantId()));
        return participants;
    }
}

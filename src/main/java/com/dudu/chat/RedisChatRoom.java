package com.dudu.chat;

import com.dudu.common.RedisConstants;
import com.dudu.common.StandardObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.*;


public class RedisChatRoom extends JedisPubSub implements ChatRoom, AutoCloseable {
    private static final Logger logger = LogManager.getLogger(RedisChatRoom.class);
    private static final ObjectMapper objectMapper = StandardObjectMapper.getInstance();
    public static boolean DEBUG = false;

    public static final int ACTION_TYPE_NEW_MESSAGE = 1;

    public static final int ACTION_TYPE_PARTICIPANT_EXIT = 2;

    public static final int ACTION_TYPE_PARTICIPANT_JOIN = 3;

    protected static final String REDIS_CHANNEL_PREFIX = RedisConstants.CHANNEL_CHATROOM;

    protected static final String REDIS_CHAT_PARTICIPANTS = RedisConstants.DATA_CHATROOM_PARTICIPANTS;

    protected static final String REDIS_CHAT_MESSAGES = RedisConstants.DATA_CHATROOM_MESSAGES;

    protected JedisPool jedisPool;
    protected String roomId;
    protected ChatEventHandler eventHandler;
    protected PublishingListener listener;

    /**
     *
     * @param roomId a magic number. its uniqueness must be guaranteed.
     * @param jedisPool
     */
    public RedisChatRoom(String roomId, JedisPool jedisPool) throws Exception {
        this.jedisPool = jedisPool;
        this.roomId = roomId;

        listener = new PublishingListener();
        listener.start();
    }

    @Override
    public void join(ChatParticipant participant) {
        if (DEBUG)
            logger.debug("participant [" + participant.getChatParticipantId() + "] joins room [" + roomId + "]");

        try (Jedis jedis = jedisPool.getResource()) {
            if (!(participant instanceof RedisChatRoomParticipant))
                throw new IllegalArgumentException("Expecting RedisChatRoomParticipant.");

            if (!jedis.hexists(redisKeyParticipants(), participant.getChatParticipantId())) {
                String participantJson = objectMapper.writeValueAsString(participant);
                jedis.hset(redisKeyParticipants(), participant.getChatParticipantId(), participantJson);
                jedis.publish(actionTypeParticipantJoin(), participantJson);
            }
        } catch (Exception e) {
            logger.error("Failed to join a participant: ", e);
        }
    }

    @Override
    public void exit(ChatParticipant participant) {
        if (DEBUG)
            logger.debug("participant [" + participant.getChatParticipantId() + "] exits room [" + roomId + "]");

        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.hexists(redisKeyParticipants(), participant.getChatParticipantId())) {
                jedis.hdel(redisKeyParticipants(), participant.getChatParticipantId());
                jedis.publish(actionTypeParticipantExit(), String.valueOf(participant.getChatParticipantId()));
            }
        }
    }

    @Override
    public void publish(ChatMessage message) {
        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.hexists(redisKeyParticipants(), message.getChatParticipantId())) {
                logger.warn("Participant is not this room: participantId="
                        + message.getChatParticipantId() + ", roomId=" + roomId);
                return;
            }

            if (!jedis.hexists(redisKeyParticipants(), message.getChatParticipantId())) {
                logger.warn("Unknown participant: ParticipantId=" + message.getParticipantId()
                        + ", Message=" + message.getMessage());
            }

            String messageJson = objectMapper.writeValueAsString(message);
            jedis.lpush(redisKeyMessages(), messageJson);
            jedis.publish(actionTypeNewMessage(), messageJson);
        } catch (Exception e) {
            logger.error("Failed to publish: ", e);
        }
    }

    @Override
    public void setEventHandler(ChatEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    public String getRoomId() {
        return roomId;
    }

    /**
     * a new message is published. JSON format of ChatMessage
     * @return
     */
    protected String actionTypeNewMessage() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_NEW_MESSAGE;
    }

    /**
     * a new participant joined. JSON format of RedisChatRoomParticipant
     * @return
     */
    protected String actionTypeParticipantJoin() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_PARTICIPANT_JOIN;
    }

    /**
     * a new participant exited. participantId
     * @return
     */
    protected String actionTypeParticipantExit() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_PARTICIPANT_EXIT;
    }

    /**
     * hash map. particpantId -> RedisChatRoomParticipant
     * @return
     */
    protected String redisKeyParticipants() {
        return REDIS_CHAT_PARTICIPANTS + roomId;
    }

    /**
     * list of ChatMessage.
     * @return
     */
    protected String redisKeyMessages() {
        return REDIS_CHAT_MESSAGES + roomId;
    }

    protected List<ChatMessage> getAllMessages() {
        try (Jedis jedis = jedisPool.getResource()) {
            List<ChatMessage> messages = new ArrayList<>();
            for (String msg : jedis.lrange(redisKeyMessages(), 0, -1)) {
                messages.add(objectMapper.readValue(msg, ChatMessage.class));
            }

            return messages;
        } catch (Exception e) {
            logger.error("Failed to getAllMessages:", e);
            return new ArrayList<>();
        }
    }

    protected List<RedisChatRoomParticipant> getAllParticipants() {
        try (Jedis jedis = jedisPool.getResource()) {
            List<RedisChatRoomParticipant> participants = new ArrayList<>();
            for (String participant : jedis.hvals(redisKeyParticipants())) {
                participants.add(objectMapper.readValue(participant, RedisChatRoomParticipant.class));
            }
            return participants;
        } catch (Exception e) {
            logger.error("Failed to getAllParticipants: ", e);
            return new ArrayList<>();
        }
    }

    @Override
    public void close() throws Exception {
        listener.stop = true;
        listener.subscription.unsubscribe();
    }

    private class PublishingListener extends Thread {
        private boolean stop;
        private JedisPubSub subscription;

        @Override
        public void run() {
            subscription = new JedisPubSub() {
                @Override
                public void onMessage(String channel, String data) {
                    try {
                        if (channel.equals(actionTypeNewMessage())) {
                            if (DEBUG)
                                logger.debug("Getting a new message: " + data);

                            ChatMessage chatMessage = objectMapper.readValue(data, ChatMessage.class);
                            eventHandler.receive(chatMessage);
                        } else if (channel.equals(actionTypeParticipantJoin())) {
                            eventHandler.onParticipantJoin(() -> data);
                        } else if (channel.equals(actionTypeParticipantExit())) {
                            eventHandler.onParticipantExit(() -> data);
                        } else {
                            throw new IllegalStateException("Unknown channel: " + channel);
                        }

                    } catch (Exception e) {
                        logger.warn("Failed to process a message from channel: " + channel + ", data: " + data, e);
                    }
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    super.onSubscribe(channel, subscribedChannels);
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    super.onUnsubscribe(channel, subscribedChannels);
                }
            };

            try (Jedis jedis = jedisPool.getResource()) {
                jedis.subscribe(subscription, actionTypeNewMessage(), actionTypeParticipantJoin(), actionTypeParticipantExit());
                jedis.close();
                while (!stop) { }

                if (DEBUG)
                    logger.debug("exiting subscription");
            }
        }
    }
}

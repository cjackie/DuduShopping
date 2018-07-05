package com.dudu.chat;

import com.dudu.common.RedisConstants;
import com.dudu.common.StandardObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RedisChatRoom extends JedisPubSub implements ChatRoom, AutoCloseable {
    private static final Logger logger = LogManager.getLogger(RedisChatRoom.class);
    private static final ObjectMapper objectMapper = StandardObjectMapper.getInstance();
    public static boolean DEBUG = false;

    public static final int ACTION_TYPE_NEW_MESSAGE = 1;

    public static final int ACTION_TYPE_PARTICIPANT_EXIT = 2;

    public static final int ACTION_TYPE_PARTICIPANT_JOIN = 3;

    /**
     * channel name for publishing and subscribing... it is appended by REDIS_CHANNEL_ROOM_ID to generate a
     * unique key for a room and action type...
     *
     * example:
     * REDIS_CHANNEL_PREFIX + "roomId10/" + ACTION_TYPE_PARTICIPANTS_UPDATED
     *
     * data:
     * {
     *     participantId: "",
     *     message: ""
     * }
     *
     **/
    protected static final String REDIS_CHANNEL_PREFIX = RedisConstants.CHANNEL_CHATROOM;

    /**
     * hash set of participant ids
     */
    protected static final String REDIS_CHAT_PARTICIPANTS = RedisConstants.DATA_CHATROOM_PARTICIPANTS;

    protected JedisPool jedisPool;
    protected String roomId;
    protected ChatEventHandler eventHandler;
    protected PublishingListener listener;
    protected Map<String, ChatParticipant> participants;

    /**
     *
     * @param roomId a magic number. its uniqueness must be guaranteed.
     * @param jedisPool
     */
    public RedisChatRoom(String roomId, JedisPool jedisPool) throws Exception {
        this.jedisPool = jedisPool;
        this.roomId = roomId;

        refreshParticipants();

        listener = new PublishingListener();
        listener.start();
    }

    @Override
    public void join(ChatParticipant participant) {
        if (DEBUG)
            logger.debug("participant [" + participant.getChatParticipantId() + "] joins room [" + roomId + "]");

        participants.put(participant.getChatParticipantId(), participant);

        try (Jedis jedis = jedisPool.getResource()) {
            if (!jedis.sismember(redisKeyParticipants(), participant.getChatParticipantId())) {
                jedis.sadd(redisKeyParticipants(), participant.getChatParticipantId());
                jedis.publish(actionTypeParticipantJoin(), String.valueOf(participant.getChatParticipantId()));
            }
        }
    }

    @Override
    public void exit(ChatParticipant participant) {
        if (DEBUG)
            logger.debug("participant [" + participant.getChatParticipantId() + "] exits room [" + roomId + "]");

        participants.remove(participant.getChatParticipantId());

        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.sismember(redisKeyParticipants(), participant.getChatParticipantId())) {
                jedis.srem(redisKeyParticipants(), participant.getChatParticipantId());
                jedis.publish(actionTypeParticipantExit(), String.valueOf(participant.getChatParticipantId()));
            }
        }
    }

    @Override
    public void publish(ChatMessage message) {
        if (participants.get(message.getChatParticipantId()) == null) {
            logger.warn("Participant is not this room: participantId="
                    + message.getChatParticipantId() + ", roomId=" + roomId);
            return;
        }

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(actionTypeNewMessage(), objectMapper.writeValueAsString(message));
        } catch (Exception e) {
            logger.error("Failed to publish: ", e);
        }
    }

    @Override
    public void setEventHandler(ChatEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    private void refreshParticipants() {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> participantIds = jedis.smembers(redisKeyParticipants());

            this.participants = new HashMap<>();
            for (String participantId : participantIds) {
                RedisChatRoomParticipant participant = new RedisChatRoomParticipant(participantId);
                this.participants.put(participantId, participant);
            }
        }
    }

    public String getRoomId() {
        return roomId;
    }

    private String actionTypeNewMessage() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_NEW_MESSAGE;
    }

    private String actionTypeParticipantJoin() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_PARTICIPANT_JOIN;
    }

    private String actionTypeParticipantExit() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_PARTICIPANT_EXIT;
    }

    private String redisKeyParticipants() {
        return REDIS_CHAT_PARTICIPANTS + roomId;
    }

    @Override
    public void close() throws Exception {
        listener.stop = true;
        listener.subscription.unsubscribe();
    }

    private static class RedisChatRoomParticipant implements ChatParticipant {
        String participantId;

        RedisChatRoomParticipant(String participantId) {
            this.participantId = participantId;
        }

        @Override
        public String getChatParticipantId() {
            return participantId;
        }
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
                            if (!participants.containsKey(chatMessage.getChatParticipantId()))
                                throw new IllegalArgumentException("Unknown participant: " + chatMessage.getParticipantId());

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

package com.dudu.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.json.JSONTokener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class RedisChatRoom extends JedisPubSub implements ChatRoom {
    private static final Logger logger = LogManager.getLogger(RedisChatRoom.class);

    public static final int ACTION_TYPE_NEW_MESSAGE = 1;

    public static final int ACTION_TYPE_PARTICIPANTS_UPDATED = 2;

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
    private static final String REDIS_CHANNEL_PREFIX = "/ch/chatRoom/";

    /**
     * hash set of participant ids
     */
    private static final String REDIS_CHAT_PARTICIPANTS = "/data/chatRoom/participants/";

    private Jedis jedis;
    private String roomId;
    private ChatMessageReceiver receiver;
    private Map<String, ChatParticipant> participants;

    /**
     *
     * @param roomId uniqueness must be guaranteed.
     * @param jedis
     */
    public RedisChatRoom(String roomId, Jedis jedis) throws Exception {
        this.jedis = jedis;
        this.roomId = roomId;

        refreshParticipants();
        jedis.subscribe(new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                try {
                    if (channel.equals(getNewMessageChannel())) {
                        JSONObject data = new JSONObject(new JSONTokener(message));
                        String participantId = data.getString("participantId");
                        String participantMessage = data.getString("message");

                        if (!participants.containsKey(participantId))
                            throw new IllegalArgumentException("Unknown participant: " + participantId);

                        ChatMessage chatMessage = new ChatMessage(new RedisChatRoomParticipant(participantId), participantMessage);
                        receiver.receive(chatMessage);
                    } else if (channel.equals(getParticipantsUpdatedChannel())) {
                        refreshParticipants();
                    } else
                        throw new IllegalStateException("Unknown channel: " + channel);
                } catch (Exception e) {
                    logger.warn(e);
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
        }, getNewMessageChannel(), getParticipantsUpdatedChannel());

    }

    @Override
    public void join(ChatParticipant participant) {
        if (!jedis.sismember(getParticipantsKey(), participant.getChatParticipantId())) {
            jedis.sadd(getParticipantsKey(), participant.getChatParticipantId());
            jedis.publish(getParticipantsUpdatedChannel(), "");
        }
    }

    @Override
    public void exit(ChatParticipant participant) {
        if (jedis.sismember(getParticipantsKey(), participant.getChatParticipantId())) {
            jedis.srem(getParticipantsKey(), participant.getChatParticipantId());
            jedis.publish(getParticipantsUpdatedChannel(), "");
        }
    }

    @Override
    public void publish(ChatMessage message) {
        if (participants.get(message.getParticipant().getChatParticipantId()) == null) {
            logger.warn("Participant is not this room: participantId="
                    + message.getParticipant().getChatParticipantId() + ", roomId" + roomId);
            return;
        }

        String participantId = message.getParticipant().getChatParticipantId();
        String msg = message.getMessage();

        JSONObject data = new JSONObject();
        data.put("participantId", message.getParticipant().getChatParticipantId());
        data.put("message", msg);
        jedis.publish(getNewMessageChannel(), data.toString());
    }

    @Override
    public void setReceiver(ChatMessageReceiver receiver) {
        this.receiver = receiver;
    }

    private void refreshParticipants() {
        Set<String> participantIds = jedis.smembers(getParticipantsKey());

        this.participants = new HashMap<>();
        for (String participantId : participantIds) {
            RedisChatRoomParticipant participant = new RedisChatRoomParticipant(participantId);
            this.participants.put(participantId, participant);
        }
    }

    public String getRoomId() {
        return roomId;
    }

    private String getNewMessageChannel() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_NEW_MESSAGE;
    }

    private String getParticipantsUpdatedChannel() {
        return REDIS_CHANNEL_PREFIX + roomId + "/" + ACTION_TYPE_PARTICIPANTS_UPDATED;
    }

    private String getParticipantsKey() {
        return REDIS_CHAT_PARTICIPANTS + roomId;
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
}

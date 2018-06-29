package com.dudu.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract class ChatRoom {
    private static final Logger logger = LogManager.getLogger(ChatRoom.class);
    private String chatId;
    private Set<ChatParticipant> participants;

    public ChatRoom() {
        participants = new HashSet<>();
    }

    /**
     * silently ignore when participant has been join
     * @param participant
     */
    void join(ChatParticipant participant) {
        participants.add(participant);
    }

    /**
     * silently ignore when participant is not in the chat room
     * @param participant
     */
    void exit(ChatParticipant participant) {
        participants.remove(participant);
    }

    abstract void publish(ChatMessage message);

    void onMessage(ChatMessage message) {
        if (!participants.contains(message.getParticipant())) {
            logger.warn("Participate " + message.getParticipant().getChatParticipantId() + " is not in room " + chatId);
            return;
        }

        message.getParticipant().onMessage(this, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom chatRoom = (ChatRoom) o;
        return Objects.equals(chatId, chatRoom.chatId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(chatId);
    }
}

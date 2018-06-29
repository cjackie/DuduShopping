package com.dudu.chat;

import java.util.Objects;

abstract public class ChatParticipant {

    /**
     * unique id for a participant
     * @return
     */
    abstract String getChatParticipantId();

    /**
     *
     * @param message
     */
    abstract void onMessage(ChatRoom room, ChatMessage message);

    void publish(ChatRoom room, ChatMessage message) {
        room.publish(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatParticipant chatParticipant = (ChatParticipant) o;
        return chatParticipant.getChatParticipantId().equals(this.getChatParticipantId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getChatParticipantId());
    }

    public ChatMessage createMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        chatMessage.setParticipant(this);

        return chatMessage;
    }
}

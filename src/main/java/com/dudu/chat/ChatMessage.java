package com.dudu.chat;

public class ChatMessage {
    private ChatParticipant participant;
    private String message;

    public ChatMessage(ChatParticipant participant, String message) {
        this.participant = participant;
        this.message = message;
    }

    public ChatParticipant getParticipant() {
        return participant;
    }

    public String getMessage() {
        return message;
    }
}

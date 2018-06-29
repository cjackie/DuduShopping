package com.dudu.chat;

class ChatMessage {
    private ChatParticipant participant;
    private String message;

    ChatMessage() { }

    public ChatParticipant getParticipant() {
        return participant;
    }

    void setParticipant(ChatParticipant participant) {
        this.participant = participant;
    }

    public String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }
}

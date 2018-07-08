package com.dudu.chat;

import com.dudu.database.ZetaMap;

import java.util.Date;

class ChatMessage {
    private long participantId;
    private long messageId;
    private Date createdAt;
    private String message;

    public static ChatMessage from(ZetaMap zetaMap) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(zetaMap.getLong("MessageId"));
        chatMessage.setParticipantId(zetaMap.getLong("ParticipantId"));
        chatMessage.setMessage(zetaMap.getString("Message"));
        chatMessage.setCreatedAt(zetaMap.getDate("CreatedAt"));

        return chatMessage;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public long getParticipantId() {
        return this.participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }
}

package com.dudu.chat;

import com.dudu.database.ZetaMap;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class ChatMessage implements ChatParticipant {
    private String participantId;
    private long messageId;
    private Date createdAt;
    private String message;

    public static ChatMessage from(ZetaMap zetaMap) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessageId(zetaMap.getLong("MessageId"));
        chatMessage.setParticipantId(String.valueOf(zetaMap.getLong("ParticipantId")));
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

    public String getParticipantId() {
        return this.participantId;
    }

    public void setParticipantId(String participantId) {
        this.participantId = participantId;
    }

    @Override
    @JsonIgnore
    public String getChatParticipantId() {
        return String.valueOf(participantId);
    }
}

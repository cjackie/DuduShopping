package com.dudu.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class RedisChatRoomParticipant implements ChatParticipant {
    private long participantId;
    private long userId;
    private String roomId;
    private Date joinedAt;

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @JsonIgnore
    @Override
    public String getChatParticipantId() {
        return String.valueOf(participantId);
    }
}

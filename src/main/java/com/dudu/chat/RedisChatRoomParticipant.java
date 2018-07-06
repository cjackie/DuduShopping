package com.dudu.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;

public class RedisChatRoomParticipant implements ChatParticipant {
    private long participantId;
    private long userId;
    private long roomId;
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

    public long getRoomId() {
        return roomId;
    }

    public void setRoomId(long roomId) {
        this.roomId = roomId;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    @JsonIgnore
    @Override
    public String getChatParticipantId() {
        return String.valueOf(participantId);
    }
}

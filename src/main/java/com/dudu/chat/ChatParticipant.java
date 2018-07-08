package com.dudu.chat;

import com.dudu.database.ZetaMap;

import java.util.Date;

class ChatParticipant {
    private long participantId;
    private long userId;
    private long roomId;
    private Date joinedAt;

    public static ChatParticipant from(ZetaMap zmap) {
        ChatParticipant participant = new ChatParticipant();
        participant.setParticipantId(zmap.getLong("ParticipantId"));
        participant.setRoomId(zmap.getLong("RoomId"));
        participant.setUserId(zmap.getLong("UserId"));
        participant.setJoinedAt(zmap.getDate("JoinedAt"));

        return participant;
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

    public long getParticipantId() {
        return participantId;
    }

    public void setParticipantId(long participantId) {
        this.participantId = participantId;
    }
}

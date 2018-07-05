package com.dudu.chat;

interface ChatEventHandler {
    void receive(ChatMessage message);
    void onParticipantJoin(ChatParticipant participant);
    void onParticipantExit(ChatParticipant participant);
}

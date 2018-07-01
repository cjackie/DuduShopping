package com.dudu.chat;



interface ChatRoom {

    /**
     * silently ignore when participant has been join
     * @param participant
     */
    void join(ChatParticipant participant);

    /**
     * silently ignore when participant is not in the chat room
     * @param participant
     */
    void exit(ChatParticipant participant);

    void publish(ChatMessage message);

    void setReceiver(ChatMessageReceiver receiver);
}

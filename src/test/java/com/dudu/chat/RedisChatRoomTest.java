package com.dudu.chat;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

public class RedisChatRoomTest extends TestBase implements ChatMessageReceiver {
    private String roomId = "TEST1";

    @Before
    public void setup() {
        super.setup();
        Assume.assumeTrue(ready);
        RedisChatRoom.DEBUG = true;
    }

    @Test
    public void twoFriendsChat() throws Exception {
        try (RedisChatRoom room = new RedisChatRoom(roomId, DBManager.getManager().getChatRoomRedisPool())) {
            ChatParticipantImpl tom = new ChatParticipantImpl();
            tom.id = "tom";
            ChatParticipantImpl jack = new ChatParticipantImpl();
            jack.id = "jack";

            room.setReceiver(this);
            room.join(tom);
            room.join(jack);
            room.publish(new ChatMessage(tom, "hello, my name is tom"));
            room.publish(new ChatMessage(jack, "hello, my name is jack"));

            while (true) { }
        }
    }

    @Override
    public void receive(ChatMessage message) {
        println(message.getParticipant().getChatParticipantId() + " said '" + message.getMessage() + "'");
    }

    private class ChatParticipantImpl implements ChatParticipant {
        private String id;

        @Override
        public String getChatParticipantId() {
            return id;
        }
    }
}

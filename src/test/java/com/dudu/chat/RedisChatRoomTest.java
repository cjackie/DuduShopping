package com.dudu.chat;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

public class RedisChatRoomTest extends TestBase implements ChatEventHandler {
    private String roomId = "TEST3";

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

            room.setEventHandler(this);
            room.join(tom);
            room.join(jack);

            ChatMessage tomSaid = new ChatMessage();
            tomSaid.setMessage("Hello, this is Tom");
            tomSaid.setCreatedAt(new Date());
            tomSaid.setParticipantId(tom.getChatParticipantId());
            room.publish(tomSaid);

            ChatMessage jackSaid = new ChatMessage();
            jackSaid.setMessage("Hello, this is Jack");
            jackSaid.setCreatedAt(new Date());
            jackSaid.setParticipantId(jack.getChatParticipantId());
            room.publish(jackSaid);

            while (true) { }
        }
    }

    @Override
    public void receive(ChatMessage message) {
        println(message.getChatParticipantId() + " said '" + message.getMessage() + "'");
    }

    @Override
    public void onParticipantJoin(ChatParticipant participant) {
        println(participant.getChatParticipantId() + " joins the room");
    }

    @Override
    public void onParticipantExit(ChatParticipant participant) {
        println(participant.getChatParticipantId() + " exits the room");
    }

    private class ChatParticipantImpl implements ChatParticipant {
        private String id;

        @Override
        public String getChatParticipantId() {
            return id;
        }
    }
}

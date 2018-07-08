package com.dudu.chat;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;

public class RedisChatRoomTest extends TestBase implements ChatEventHandler {
    Logger logger = LogManager.getLogger(RedisChatRoomTest.class);
    String roomId = "TEST15";
    RedisChatRoom room = null;

    @Before
    public void setup() {
        super.setup();
        Assume.assumeTrue(ready);

        RedisChatRoom.DEBUG = true;
        try {
            room = new RedisChatRoom(String.valueOf(roomId), DBManager.getManager().getChatRoomRedisPool());
        } catch (Exception e) {
            logger.info(e);
        }
    }

    @Test
    public void twoFriendsChat() {
        ChatParticipant tom = new ChatParticipant();
        tom.setParticipantId(1);
        tom.setUserId(20);
        ChatParticipant jack = new ChatParticipant();
        jack.setParticipantId(2);
        jack.setUserId(10);

        room.setEventHandler(this);
        room.join(tom);
        room.join(jack);

        ChatMessage tomSaid = new ChatMessage();
        tomSaid.setMessage("Hello, this is Tom");
        tomSaid.setCreatedAt(new Date());
        tomSaid.setParticipantId(tom.getParticipantId());
        room.publish(tomSaid);

        ChatMessage jackSaid = new ChatMessage();
        jackSaid.setMessage("Hello, this is Jack");
        jackSaid.setCreatedAt(new Date());
        jackSaid.setParticipantId(jack.getParticipantId());
        room.publish(jackSaid);

        while (true) { }
    }

    @Test
    public void getAllMessages() {
        List<ChatMessage> messages = room.getAllMessages();
        println(messages.size());
    }

    @Test
    public void getAllParticipants() {
        List<ChatParticipant> participants = room.getAllParticipants();
        println(participants.size());
    }

    @After
    public void cleanup() {
        try {
            if (room != null)
                room.close();
        } catch (Exception e) { }
    }

    @Override
    public void receive(ChatMessage message) {
        println(message.getParticipantId() + " said '" + message.getMessage() + "'");
    }

    @Override
    public void onParticipantJoin(ChatParticipant participant) {
        println(participant.getParticipantId() + " joins the room");
    }

    @Override
    public void onParticipantExit(ChatParticipant participant) {
        println(participant.getParticipantId() + " exits the room");
    }
}

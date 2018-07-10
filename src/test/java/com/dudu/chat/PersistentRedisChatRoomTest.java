package com.dudu.chat;

import com.dudu.common.TestBase;
import com.dudu.database.DBManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistentRedisChatRoomTest extends TestBase implements ChatEventHandler {
    private Logger logger = LogManager.getLogger(PersistentRedisChatRoomTest.class);

    @Before
    public void setup() {
        super.setup();
        Assume.assumeTrue(ready);
    }

    @Test
    public void persistentRedisChatRoom() throws Exception {
        DBManager dbManager = DBManager.getManager();
        long userId1 = 1;
        long userId2 = 2;
        try (PersistentRedisChatRoom persistentRedisChatRoom = new PersistentRedisChatRoom(dbManager.getChatRoomRedisPool(), dbManager.getDataSource(DBManager.DATABASE_DUDU_SHOPPING), "")) {
            println("A chat room has been created");
            persistentRedisChatRoom.setEventHandler(this);

            ChatParticipant participant1 = persistentRedisChatRoom.createNewParticipant(userId1);
            persistentRedisChatRoom.join(participant1);

            ChatParticipant participant2 = persistentRedisChatRoom.createNewParticipant(userId2);
            persistentRedisChatRoom.join(participant2);

            List<ChatParticipant> participants = persistentRedisChatRoom.getAllParticipants();
            Map<Long, ChatParticipant> participantMap = new HashMap<>();
            for (ChatParticipant participant : participants) {
                participantMap.put(participant.getUserId(), participant);
            }

            ChatMessage message1 = persistentRedisChatRoom.createNewMessage(participantMap.get(userId1).getParticipantId(), "hello from UserId=1");
            persistentRedisChatRoom.publish(message1);
            ChatMessage message2 = persistentRedisChatRoom.createNewMessage(participantMap.get(userId2).getParticipantId(), "hello from UserId=2");
            persistentRedisChatRoom.publish(message2);

            println("done");
            while (true) {}
        }
    }

    @Override
    public void receive(ChatMessage message) {
        logger.info("Receiving: " + message.getMessage());
    }

    @Override
    public void onParticipantJoin(ChatParticipant participant) {
        logger.info("Joining: " + participant.getUserId());
    }

    @Override
    public void onParticipantExit(ChatParticipant participant) {
        logger.info("Exiting: " + participant.getUserId());
    }
}

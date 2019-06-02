package at.hangman.hangman;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    private List<Room> rooms = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if(ChatMessage.MessageType.CHAT.equals(chatMessage.getType())) {

            String receiver = "";
            for(Room room : rooms) {
                if(chatMessage.getSender().equals(room.getPlayer1Name())) {
                    receiver = room.getPlayer2Name();
                }

                if(chatMessage.getSender().equals(room.getPlayer2Name())) {
                    receiver = room.getPlayer1Name();
                }
            }

            chatMessage.setContent(chatMessage.getContent() + ":" + receiver);
        }

        return chatMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage,
                               SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        String username = chatMessage.getSender();
        boolean userAdded = false;

        for(Room room : rooms) {
            if(StringUtils.isEmpty(room.getPlayer2Name())) {
                room.setPlayer2Name(username);
                room.setGetWordForPlayer1(chatMessage.getContent());
                userAdded = true;
                logger.info(username + " kam ins spiel dazu, er geht in den Raum von " + room.getPlayer1Name());
            }
        }

        if(userAdded == false) {
            Room newRoom = new Room();
            newRoom.setPlayer1Name(username);
            newRoom.setWordForPlayer2(chatMessage.getContent());
            rooms.add(newRoom);
            logger.info(username + " kam ins spiel dazu, er eröffnet einen neuen Raum ");
        }

        return chatMessage;
    }

}
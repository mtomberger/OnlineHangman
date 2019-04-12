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

    private List<String[]> rooms = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if(ChatMessage.MessageType.CHAT.equals(chatMessage.getType())) {

            String receiver = "";
            for(String[] room : rooms) {
                if(chatMessage.getSender().equals(room[0])) {
                    receiver = room[1];
                }

                if(room.length > 0 && chatMessage.getSender().equals(room[1])) {
                    receiver = room[0];
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

        for(String[] playRoom : rooms) {
            if(StringUtils.isEmpty(playRoom[1])) {
                playRoom[1] = username;
                userAdded = true;
                logger.info(username + " kam ins spiel dazu, er geht in den Raum von " + playRoom[0]);
            }
        }

        if(userAdded == false) {
            String[] newRoom = new String[2];
            newRoom[0] = username;
            rooms.add(newRoom);
            logger.info(username + " kam ins spiel dazu, er eröffnet einen neuen Raum ");

        }

        return chatMessage;
    }

}
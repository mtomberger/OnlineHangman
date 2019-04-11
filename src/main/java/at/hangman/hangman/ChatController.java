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

    private List<String[]> raeume = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
        if(ChatMessage.MessageType.CHAT.equals(chatMessage.getType())) {

            String receiver = "";
            for(String[] raum : raeume) {
                if(chatMessage.getSender().equals(raum[0])) {
                    receiver = raum[1];
                }

                if(raum.length > 0 && chatMessage.getSender().equals(raum[1])) {
                    receiver = raum[0];
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

        for(String[] spielraum : raeume) {
            if(StringUtils.isEmpty(spielraum[1])) {
                spielraum[1] = username;
                userAdded = true;
                logger.info(username + " kam ins spiel dazu, er geht in den Raum von " + spielraum[0]);
            }
        }

        if(userAdded == false) {
            String[] neuerRaum = new String[2];
            neuerRaum[0] = username;
            raeume.add(neuerRaum);
            logger.info(username + " kam ins spiel dazu, er eröffnet einen neuen Raum ");

        }

        return chatMessage;
    }

}
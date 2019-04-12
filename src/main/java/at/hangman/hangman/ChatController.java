package at.hangman.hangman;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());

        String username = chatMessage.getSender();
        String word = chatMessage.getContent();

        String checkJson = GetWordCheckJson(word);
        if(!checkForAlloowedWord(checkJson,word)){
            chatMessage.setType(ChatMessage.MessageType.ERROR);
            chatMessage.setContent("We can't find your Word in the dictionary");
            return chatMessage;
        }
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
    private boolean checkForAlloowedWord(String wordsJson,String word){
        word = word.toLowerCase();
        List<Word> words = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            words = objectMapper.readValue(wordsJson, new TypeReference<List<Word>>(){});
        } catch (IOException e) {
            e.printStackTrace();
        }
        String finalWord = word;
        return words.stream().anyMatch(w -> w.isNoun() && w.getWord().toLowerCase().equals(finalWord));
    }
    private String GetWordCheckJson(String word){

        word = word.toLowerCase();
        try {
            word = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
        }
        final String uri = "https://api.datamuse.com/words?sp="+word+"&md=p&v=enwiki";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(uri, String.class);
    }

}
package at.hangman.hangman;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class HangmanController {

    private List<Room> rooms = new ArrayList<>();
    private final String MESSAGE_DELIMITER="#;#";
    private static final Logger logger = LoggerFactory.getLogger(HangmanController.class);

    @MessageMapping("/hangman.addUser")
    @SendTo("/hangman/public")
    public ClientMessage addUser(@Payload ClientMessage clientMessage, SimpMessageHeaderAccessor headerAccessor) {
        HangmanMessage hangmanMessage = clientMessage.getFirstMessage();
        headerAccessor.getSessionAttributes().put("clientid", hangmanMessage.getSenderId());

        String word = hangmanMessage.getContent();
        String username = hangmanMessage.getSender();

        String checkJson = getWordCheckJson(word);
        if(!checkForAllowedWord(checkJson,word)){
            hangmanMessage.setType(HangmanMessage.MessageType.ERROR);
            hangmanMessage.setContent("We can't find your Word in the dictionary");
            return new ClientMessage(hangmanMessage);
        }

        Optional<Room> room = rooms.stream().filter(r -> !r.maxPlayersReached()).findFirst();
        Player player = new Player(username, word);
        ClientMessage returnMessage = new ClientMessage();
        hangmanMessage.setType(HangmanMessage.MessageType.JOIN);
        if(!room.isPresent()) {
            //waiting for second player
           Room newRoom = new Room();
           newRoom.addPlayer(player);
           rooms.add(newRoom);
           logger.info(username + " kam ins spiel dazu, er eröffnet einen neuen Raum ");
           hangmanMessage.setContent("Waiting for another player...");
        } else {
            // start game
           Player otherPlayer = room.get().getPlayers().get(0);
           room.get().addPlayer(player);
           logger.info(username + " kam ins spiel dazu, er geht in den Raum von " + otherPlayer.getName());

           hangmanMessage.setContent(otherPlayer.getName());
           returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.JOIN,player.getName() ,otherPlayer.getId(),otherPlayer.getName()));

           // init game messages
            returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.INIT,otherPlayer.wordToGuessLength()+MESSAGE_DELIMITER+player.wordToGuessLength(),otherPlayer.getId(),otherPlayer.getName()));
            returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.INIT,player.wordToGuessLength()+MESSAGE_DELIMITER+otherPlayer.wordToGuessLength(),hangmanMessage.getSenderId(),player.getName()));
        }
        returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.ID,player.getId(),hangmanMessage.getSenderId(),username));
        returnMessage.addMessage(hangmanMessage);
       return returnMessage;
    }



    @MessageMapping("/hangman.guessLetter")
    @SendTo("/hangman/public")
    public ClientMessage guessLetter(@Payload ClientMessage clientMessage, SimpMessageHeaderAccessor headerAccessor){
        HangmanMessage hangmanMessage = clientMessage.getFirstMessage();
        ClientMessage returnMessages = new ClientMessage();
        String sender = hangmanMessage.getSenderId();
        Optional<Room>  searchedRoom = rooms.stream().filter(r -> r.hasPlayer(sender)).findFirst();
        if(!searchedRoom.isPresent()){
            return returnMessages;
        }
        Room playedRoom = searchedRoom.get();
        Player currentPlayer = playedRoom.getPlayer(sender);
        StringBuilder message = new StringBuilder();
        message.append(hangmanMessage.getContent().charAt(0));
        message.append(MESSAGE_DELIMITER);

        try {
           String indexes = currentPlayer.guessLetter(hangmanMessage.getContent().charAt(0));

            message.append(currentPlayer.getGuessed());
            message.append(MESSAGE_DELIMITER);
            message.append(indexes);
            message.append(MESSAGE_DELIMITER);
            message.append( currentPlayer.getId());

            for(Player p : playedRoom.getPlayers()){
                returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.PLAY,message.toString(),p.getId(),p.getName()));
            }
        } catch (GuessingException e) {

            message.append(currentPlayer.getGuessed());
            message.append(MESSAGE_DELIMITER);
            message.append(MESSAGE_DELIMITER);
            message.append( currentPlayer.getId());

            returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.PLAY,message.toString(),currentPlayer.getId(),currentPlayer.getName()));
            return returnMessages;
        }

        return returnMessages;


    }
    private boolean checkForAllowedWord(String wordsJson,String word){
        word = word.toLowerCase();
        List<Word> words = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            words = objectMapper.readValue(wordsJson, new TypeReference<List<Word>>(){});
        } catch (IOException e) {
        }
        String finalWord = word;
        return words.stream().anyMatch(w -> w.isNoun() && w.getWord().toLowerCase().equals(finalWord));
    }
    private String getWordCheckJson(String word){

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
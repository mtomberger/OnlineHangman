package at.hangman.hangman;

import at.hangman.exception.GuessingException;
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
        String word = hangmanMessage.getContent();
        String username = hangmanMessage.getSender();

        String checkJson = getWordCheckJson(word);
        if(!checkForAllowedWord(checkJson,word)){
            logger.info("'"+word+"' by "+username+" is not a valid word");
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
           logger.info(username + " joined the game and opened a new room");
           hangmanMessage.setContent("Waiting for another player...");
        } else {
            // start game
           Player otherPlayer = room.get().getPlayers().get(0);
           room.get().addPlayer(player);
           logger.info(username + " joined the game and was added to the room from " + otherPlayer.getName());
           hangmanMessage.setContent(otherPlayer.getName());
           returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.JOIN,player.getName() ,otherPlayer.getId(),otherPlayer.getName()));

           // init game messages
            returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.INIT,otherPlayer.wordToGuessLength()+MESSAGE_DELIMITER+player.wordToGuessLength(),otherPlayer.getId(),otherPlayer.getName()));
            returnMessage.addMessage(new HangmanMessage(HangmanMessage.MessageType.INIT,player.wordToGuessLength()+MESSAGE_DELIMITER+otherPlayer.wordToGuessLength(),hangmanMessage.getSenderId(),player.getName()));
        }
        logger.debug("Currently "+rooms.size()+" Room(s) with totally "+rooms.stream().mapToLong(Room::countPlayers).sum() + " Player(s)");
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
            logger.warn("Room for Player with Id "+sender+" not found");
            return returnMessages;
        }
        Room playedRoom = searchedRoom.get();
        Player currentPlayer = playedRoom.getPlayer(sender);
        StringBuilder message = new StringBuilder();
        message.append(hangmanMessage.getContent().charAt(0));
        message.append(MESSAGE_DELIMITER);
        String indexes;
        try {
            indexes = currentPlayer.guessLetter(hangmanMessage.getContent().charAt(0));

            message.append(currentPlayer.getGuessed());
            message.append(MESSAGE_DELIMITER);
            message.append(indexes);
            message.append(MESSAGE_DELIMITER);
            message.append( currentPlayer.getId());

            for(Player p : playedRoom.getPlayers()){
                returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.PLAY,message.toString(),p.getId(),p.getName()));
            }
            List<Player> finished = playedRoom.getFinishedPlayers();
            if(finished.size()>0){
                logger.debug(finished.size()+" Player(s) finished in Room "+playedRoom.getId());
                for(Player p : finished){
                    returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.FINISH,
                            p.getMistakes()+MESSAGE_DELIMITER+Player.MAX_MISTAKES+MESSAGE_DELIMITER+p.getWordToGuess(),
                            p.getId(),
                            p.getName()
                    ));
                }
                if(finished.size()== playedRoom.getPlayerCount()){
                    String scores = playedRoom.getScores(MESSAGE_DELIMITER);
                    for(Player p : playedRoom.getPlayers()){
                        returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.SCORE,scores,p.getId(),p.getName()));
                    }
                }
            }
        } catch (GuessingException e) {
            logger.warn("GuessingException when guessing "+hangmanMessage.getContent().charAt(0) + " by "+currentPlayer.getName());
            message.append(currentPlayer.getGuessed());
            message.append(MESSAGE_DELIMITER);
            message.append(MESSAGE_DELIMITER);
            message.append( currentPlayer.getId());

            returnMessages.addMessage(new HangmanMessage(HangmanMessage.MessageType.PLAY,message.toString(),currentPlayer.getId(),currentPlayer.getName()));
            return returnMessages;
        }
        logger.debug(currentPlayer.getName()+ " guessed '"+hangmanMessage.getContent().charAt(0) + "' "+
                (indexes==null || indexes.equals("")?"WRONG ":"RIGHT ") +
                currentPlayer.getMistakes() +"/"+Player.MAX_MISTAKES+ " Mistakes made");
        return returnMessages;


    }
    private boolean checkForAllowedWord(String wordsJson,String word){
        if(wordsJson==null){
           return false;
        }
        word = word.toLowerCase();
        List<Word> words = new ArrayList<>();
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            logger.debug("Parsing JSON '"+wordsJson+"' to Object");
            words = objectMapper.readValue(wordsJson, new TypeReference<List<Word>>(){});
        } catch (IOException e) {
            logger.error("JSON Word information to Objet parsing failed", e);
        }
        String finalWord = word;
        return words.stream().anyMatch(w -> w.isNoun() && w.getWord().toLowerCase().equals(finalWord));
    }
    private String getWordCheckJson(String word){
        word = word.toLowerCase();
        if(word.length() < 3){
            logger.info("word too small");
            return null;
        }
        try {
            word = URLEncoder.encode(word, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("URLEncoding of word failed", e);
        }
        final String uri = "https://api.datamuse.com/words?sp="+word+"&md=p&v=enwiki";
        RestTemplate restTemplate = new RestTemplate();
        logger.debug("Request word information about '"+word+"' from URI '"+uri+"'");
        return restTemplate.getForObject(uri, String.class);
    }

}
package at.hangman.hangman;

import at.hangman.exception.GuessingException;
import at.hangman.exception.ScoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

public class Player {
    public static final int MAX_MISTAKES = 10;
    private static final Logger logger = LoggerFactory.getLogger(HangmanController.class);

    private String id;
    private Date started;
    private Date finished;
    private String wordToGuess;
    private String choosenWord;
    private int mistakes = 0;
    private String name;
    private ArrayList<Character> guessedChars = new ArrayList<>();
    private boolean isFinished = false;

    public Player(String name, String choosenWord) {
        this.name = name;
        this.choosenWord = choosenWord;
        id = UUID.randomUUID().toString().replace("-", "");
    }

    public String getId() {
        return id;
    }

    public int getMistakes() {
        return mistakes;
    }

    public String getName() {
        return name;
    }

    public void setWordToGuess(String wordToGuess) {
        this.wordToGuess = wordToGuess;
    }

    public String getChoosenWord() {
        return choosenWord;
    }

    public boolean isFinished() {
        return isFinished;
    }
    private void setFinished(){
        finished = new Date();
        isFinished = true;
    }

    public int wordToGuessLength(){
        if(wordToGuess == null){
            return 0;
        }
        return wordToGuess.length();
    }
    public String getGuessed(){
        return guessedChars.stream().map(c -> String.valueOf(c).toUpperCase()).collect(Collectors.joining(","));
    }

    public String getWordToGuess() {
        if(isFinished){
            return wordToGuess;
        }
        return "";
    }

    private void checkFinished(){
        String str = "";
        for (Character c : guessedChars){
            str += c.toString()+",";
        }
          str+=" ; ";

        logger.info("Check for finished "+mistakes+"/"+MAX_MISTAKES + " "+str);
        if(mistakes > MAX_MISTAKES){
            logger.debug("Player "+getName()+" ("+getId()+") exceeded "+MAX_MISTAKES);
            setFinished();
        }
        ArrayList<Character> wordChars = new ArrayList<>();
        for(char c : wordToGuess.toLowerCase().toCharArray()){
            wordChars.add(c);
        }
        for (Character c : wordChars){
            str += c.toString()+",";
        }
        logger.info("Guessed and word Chars: "+str);
        if(guessedChars.containsAll(wordChars)){
            logger.debug("Player "+getName()+" ("+getId()+") guessed right ("+getMistakes()+" mistakes)");
            setFinished();
        }
    }

    public String guessLetter(char letter) throws GuessingException {
        if(guessedChars.size()==0){
            started = new Date();
        }
        letter = Character.toLowerCase(letter);
        ArrayList<Integer> letterIndexes = new ArrayList<>();
        if(isFinished){
            return "";
        }
        if(wordToGuess==null) {
            throw new GuessingException();
        }
        int index = wordToGuess.toLowerCase().indexOf(letter);
        while (index >= 0) {
            System.out.println(index);
            letterIndexes.add(index);
            index = wordToGuess.toLowerCase().indexOf(letter, index + 1);
        }
        if(!guessedChars.contains(letter)){
            guessedChars.add(letter);
            if(letterIndexes.size()<=0){
                mistakes++;
            }
        }else{
            letterIndexes.add(-1);
        }
        checkFinished();
        return letterIndexes.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
    public String getScore(){
        if(started==null||finished==null){
            throw new ScoreException();
        }
        long timeTaken = (finished.getTime()-started.getTime())/1000;
        int scoreNumber = ScoreCalculator.getInstance().calculateScore(getWordToGuess(),getMistakes(),timeTaken);
        scoreNumber = getMistakes()>Player.MAX_MISTAKES?0:scoreNumber;
        String score = getId()+","+getName()+","+getMistakes()+","+Player.MAX_MISTAKES+","+timeTaken+","+getWordToGuess()+","+scoreNumber;
        logger.debug("PLAYER SCORE: "+score);
        return score;
    }

    public Score getScoreObject(){
        if(started==null||finished==null){
            throw new ScoreException();
        }
        long timeTaken = (finished.getTime()-started.getTime())/1000;
        int scoreNumber = ScoreCalculator.getInstance().calculateScore(getWordToGuess(),getMistakes(),timeTaken);
        scoreNumber = getMistakes()>Player.MAX_MISTAKES?0:scoreNumber;
        String score = getId()+","+getName()+","+getMistakes()+","+Player.MAX_MISTAKES+","+timeTaken+","+getWordToGuess()+","+scoreNumber;
        logger.debug("PLAYER SCORE: "+score);
        return new Score(getName(), getWordToGuess().toUpperCase(), getMistakes(), (int) timeTaken, scoreNumber, LocalDate.now());
    }
}
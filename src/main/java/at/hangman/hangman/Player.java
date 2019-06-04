package at.hangman.hangman;

import at.hangman.exception.GuessingException;

import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Player {
    public static final int MAX_MISTAKES = 10;

    private String id;
    private String wordToGuess;
    private String choosenWord;
    private int mistakes = 0;
    private String name;
    private ArrayList<Character> guessedChars = new ArrayList<>();

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

    public int wordToGuessLength(){
        if(wordToGuess == null){
            return 0;
        }
        return wordToGuess.length();
    }
    public String getGuessed(){
        return guessedChars.stream().map(c -> String.valueOf(c).toUpperCase()).collect(Collectors.joining(","));
    }
    public FinishState getFinishState(){
        if(mistakes > MAX_MISTAKES){
            return FinishState.LOST;
        }
        if(false){
            return FinishState.WON;
        }
        return FinishState.NOTFINISHED;
    }

    public String guessLetter(char letter) throws GuessingException {
        letter = Character.toLowerCase(letter);
        ArrayList<Integer> letterIndexes = new ArrayList<>();
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
        }
        return letterIndexes.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

}
enum FinishState{
    NOTFINISHED,
    WON,
    LOST,

}

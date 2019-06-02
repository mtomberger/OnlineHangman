package at.hangman.hangman;

import java.util.ArrayList;
import java.util.UUID;

public class Player {
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

    public void setId(String id) {
        this.id = id;
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

    public boolean guessLetter(char letter){
        letter = Character.toLowerCase(letter);
        if(wordToGuess==null) {
            return false;
        }
        if(guessedChars.contains(letter)){
            return true;
        }
        guessedChars.add(letter);
        if(wordToGuess.toLowerCase().indexOf(letter)>-1){
            return true;
        }
        mistakes++;
        return false;
    }

}

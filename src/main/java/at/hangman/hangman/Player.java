package at.hangman.hangman;

import java.util.UUID;

public class Player {
    private final String id;
    private String wordToGuess;
    private String choosenWord;
    private int mistakes = 0;
    private String name;

    public Player(String name, String choosenWord) {
        this.name = name;
        this.choosenWord = choosenWord;
        id = UUID.randomUUID().toString().replace("-", "");
    }

    public String getId() {
        return id;
    }

    public String getWordToGuess() {
        return wordToGuess;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void addMistake() {
        this.mistakes++;
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

    public void setChoosenWord(String choosenWord) {
        this.choosenWord = choosenWord;
    }
}

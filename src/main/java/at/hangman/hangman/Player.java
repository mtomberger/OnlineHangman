package at.hangman.hangman;

import java.util.UUID;

public class Player {
    private final String id;
    private String wordToGuess;
    private int mistakes = 0;
    private String name;

    public Player(String wordToGuess, String name) {
        this.wordToGuess = wordToGuess;
        this.name = name;
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
}

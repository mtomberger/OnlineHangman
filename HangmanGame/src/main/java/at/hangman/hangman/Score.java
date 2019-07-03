package at.hangman.hangman;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Score {

    private long id;
    private String username;
    private String word;
    private int mistakes;
    private int timeNeeded;
    private int score;
    private LocalDate timestamp;

    public Score(long id, String username, String word, int mistakes, int timeNeeded, int score, LocalDate timestamp) {
        this.id = id;
        this.username = username;
        this.word = word;
        this.mistakes = mistakes;
        this.timeNeeded = timeNeeded;
        this.score = score;
        this.timestamp = timestamp;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getMistakes() {
        return mistakes;
    }

    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public int getTimeNeeded() {
        return timeNeeded;
    }

    public void setTimeNeeded(int timeNeeded) {
        this.timeNeeded = timeNeeded;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDate getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDate timestamp) {
        this.timestamp = timestamp;
    }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }
}

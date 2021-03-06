package entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity(name = "game")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;

    private String word;

    private int mistakes;

    private int timeNeeded;

    private int score;

    private LocalDateTime timestamp;

    public Score() {

    }

    public Score(long id, String username, String word, int mistakes, int timeNeeded, int score, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.word = word;
        this.mistakes = mistakes;
        this.timeNeeded = timeNeeded;
        this.score = score;
        this.timestamp = timestamp;
    }

    public Score(String username, String word, int mistakes, int timeNeeded, int score, LocalDateTime timestamp) {
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getWord() { return word; }

    public void setWord(String word) { this.word = word; }
}

package at.hangman.hangman;

import java.util.ArrayList;
import java.util.List;

public class ScoreWrapper {
    private List<Score> scores;

    public ScoreWrapper() {
        scores = new ArrayList<>();
    }

    public List<Score> getScores() {
        return scores;
    }

    public void setScores(List<Score> scores) {
        this.scores = scores;
    }
}


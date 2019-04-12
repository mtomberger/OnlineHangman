package at.hangman.hangman;

import java.util.ArrayList;
import java.util.List;

public class Word {
    private String word;
    private int score;
    private List<String> tags = new ArrayList<>();
    private boolean isNoun = false;

    public Word(String word, int score, List<String> tags) {
        this.word = word;
        this.score = score;
        this.tags = tags;
        checkForNoun();
    }

    private void checkForNoun(){
        if(this.tags==null)
        {
            this.isNoun = false;
            return;
        }
        this.isNoun = this.tags.stream().anyMatch(m -> m.toLowerCase().equals("n"));
    }

    public Word() {
        isNoun=false;
        checkForNoun();
    }

    public boolean isNoun() {
        return isNoun;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
        checkForNoun();
    }
}

package at.hangman.hangman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class HangmanDBService {

    public static final String URI = "http://localhost:8090/score";

    @Autowired
    private RestTemplate restTemplate;

    public void addScore(Score score) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject(URI, score, Score.class);
    }

    public List<Score> getScores(int scores) {
        if(scores < 1) {
            throw new IllegalArgumentException("invalid score size");
        }

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(URI+"?size="+scores, Object[].class);
        Object[] objects = responseEntity.getBody();
        return convertArrayToList(objects);
    }


    private List<Score> convertArrayToList(Object[] objects) {
        ArrayList<Score> scores = new ArrayList<>();


        for (Object o: objects) {
            LinkedHashMap e = (LinkedHashMap) o;
            int id = (int) e.get("id");
            String username = (String) e.get("username");
            int mistakes = (int) e.get("mistakes");
            int timeNeeded = (int) e.get("timeNeeded");
            int score  = (int) e.get("timeNeeded");
            int timestamp  = (int) e.get("timeNeeded");

            Score newScore = new Score(id, username, mistakes, timeNeeded, score, LocalDateTime.now());
            scores.add(newScore);
        }

        return scores;
    }
}

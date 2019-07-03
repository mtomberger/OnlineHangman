package at.hangman.hangman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
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
            String word = (String) e.get("word");
            int mistakes = (int) e.get("mistakes");
            int timeNeeded = (int) e.get("timeNeeded");
            int score  = (int) e.get("timeNeeded");

            // TODO: rework date exchange!
            String dateTimeStr = (String) e.get("timestamp");
            String dateStr = dateTimeStr.substring(0, 10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(dateStr, formatter);

            Score newScore = new Score(id, username, word, mistakes, timeNeeded, score, date);
            scores.add(newScore);
        }

        return scores;
    }
}

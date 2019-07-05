package at.hangman.hangman;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.sql.Date;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HangmanDBService {

    public static final String URI = "http://192.168.43.135:8090/score";

    @Autowired
    private RestTemplate restTemplate;

    public void addScore(Score score) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("names", score);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String transferObject = score.getUsername() + ";" + score.getWord() + ";"
            + score.getMistakes() + ";" + score.getScore() + ";" + score.getTimeNeeded();
        restTemplate.postForObject(URI, transferObject, String.class);
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
            int score  = (int) e.get("score");

            String dateTimeStr = (String) e.get("timestamp");
            LocalDate date = LocalDate.MIN;
            if(dateTimeStr !=null){
                String dateStr = dateTimeStr.substring(0, 10);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                date = LocalDate.parse(dateStr, formatter);
            }


            Score newScore = new Score(id, username, word, mistakes, timeNeeded, score, date);
            scores.add(newScore);
        }

        return scores;
    }
}

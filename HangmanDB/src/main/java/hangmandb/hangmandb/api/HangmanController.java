package hangmandb.hangmandb.api;

import com.fasterxml.jackson.databind.util.JSONPObject;
import entities.Score;
import hangmandb.hangmandb.services.DBService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class HangmanController {

    private DBService dbService = new DBService();

    @RequestMapping("/")
    public String index() {
        return "Welcome, here is the hangman controller!";
    }

    @PostMapping("/score")
    public Score addGame(@RequestBody String transferObject) {
        String[] transferObjects = transferObject.split(";");

        String username = transferObjects[0];
        String word = transferObjects[1];
        int mistakes = Integer.parseInt(transferObjects[2]);
        int score = Integer.parseInt(transferObjects[3]);
        int timeNeeded = Integer.parseInt(transferObjects[4]);

        return dbService.addGame(new Score(username, word, mistakes, timeNeeded, score, LocalDateTime.now()));
    }

    @GetMapping("/score")
    public List<Score> getScoreBoard(int size) {
        return dbService.getScoreBoard(size);
    }
}

package hangmandb.hangmandb.api;

import com.fasterxml.jackson.databind.util.JSONPObject;
import entities.Score;
import hangmandb.hangmandb.services.DBService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class HangmanController {

    private DBService dbService = new DBService();

    @RequestMapping("/")
    public String index() {
        return "Welcome, here is the hangman controller!";
    }

    @PostMapping("/score")
    public Score addGame(@RequestBody Object jsonpObject) {
        return dbService.addGame(new Score());
    }

    @GetMapping("/score")
    public List<Score> getScoreBoard(int size) {
        return dbService.getScoreBoard(size);
    }
}

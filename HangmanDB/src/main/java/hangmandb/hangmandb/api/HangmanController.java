package hangmandb.hangmandb.api;

import entities.Game;
import entities.Player;
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

    @PostMapping("/player")
    public Player addPlayer(@RequestBody Player player) {
        return dbService.addPlayer(player);
    }

    @PostMapping("/game")
    public Game addGame(@RequestBody Game game) {
        return dbService.addGame(game);
    }

    @GetMapping("/game")
    public List<Game> getScoreBoard(int size) {
        return dbService.getScoreBoard(size);
    }
}

package at.hangman.hangman;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ScoreController {
    private HangmanDBService hangmanDBService = new HangmanDBService();
    public static final int SCOREBOARD_SIZE = 10;

    @PostMapping("/scores")
    public List<Score> submitScore(@RequestParam String id) {
       // String id = payload.get("id");
        List<Room> rooms = RoomWrapper.getRooms();
        Player player = null;
        for(Room room : rooms) {
            List<Player> players = room.getPlayers();
            if(players.get(0).getId().equals(id)) {
                player = players.get(0);
                break;
            }
            if(players.get(1).getId().equals(id)) {
                player = players.get(1);
                break;
            }
        }
        if(player!=null){
            Score calcScore = player.getScoreObject();
            if(Player.MAX_MISTAKES>=calcScore.getMistakes() && calcScore.getScore()>0){
                hangmanDBService.addScore(calcScore);
            }

        }
        return getTopScores();
    }
    @RequestMapping("/scores")
    public List<Score> getScoreBoard() {
        return getTopScores();
    }

    private List<Score> getTopScores(){
        return hangmanDBService.getScores(SCOREBOARD_SIZE);
    }

}

package at.hangman.hangman;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Room {

    private final static int PLAYERS_PER_ROOM = 2;
    private List<Player> players = new ArrayList<>();
    private final String id;

    public Room() {
        id = UUID.randomUUID().toString().replace("-", "");
    }

    public void addPlayer(String name, String choosenWord){
        Player p = new Player(name, choosenWord);
        if(maxPlayersReached()){
            return;
        }
        players.add(p);
        setGuessWord();
    }

    public void setGuessWord() {
        if(maxPlayersReached()) {
            Player player1 = players.get(0);
            Player player2 = players.get(1);

            player1.setWordToGuess(player2.getChoosenWord());
            player2.setWordToGuess(player1.getChoosenWord());
        }
    }

    public boolean maxPlayersReached(){
        return players.size() >= PLAYERS_PER_ROOM;

    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public String getId() {
        return id;
    }
}

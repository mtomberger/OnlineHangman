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
        addPlayer(p);
    }
    public void addPlayer(Player p){
        if(maxPlayersReached()){
            return;
        }
        players.add(p);
        setGuessWord();
    }

    public void setGuessWord() {
        if(maxPlayersReached()) {
            Player nextP = players.get(players.size()-1);
            for(Player p : players){
                p.setWordToGuess(nextP.getChoosenWord());
                nextP = p;
            }
        }
    }

    public boolean maxPlayersReached(){
        return players.size() >= PLAYERS_PER_ROOM;

    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }
}

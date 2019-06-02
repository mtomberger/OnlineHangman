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

    public void addPlayer(String name, String word){
        Player p = new Player(name,word);
        if(maxPlayersReached()){
            return;
        }
        players.add(p);
    }
    public boolean maxPlayersReached(){
        return players.size() >= PLAYERS_PER_ROOM;

    }

    public String getId() {
        return id;
    }
}

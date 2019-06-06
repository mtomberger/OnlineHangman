package at.hangman.hangman;

import java.util.*;
import java.util.stream.Collectors;

public class Room {

    private final static int PLAYERS_PER_ROOM = 2;
    private List<Player> players = new ArrayList<>();
    private final String id;

    public Room() {
        id = UUID.randomUUID().toString().replace("-", "");
    }

    public void addPlayer(Player p){
        if(maxPlayersReached()){
            return;
        }
        players.add(p);
        setGuessWord();
    }
    public long countPlayers(){
        return players.stream().filter(p -> p!=null).count();
    }

    public void setGuessWord() {
        if(maxPlayersReached()) {
            Player nextP = players.get(getPlayerCount()-1);
            for(Player p : players){
                p.setWordToGuess(nextP.getChoosenWord());
                nextP = p;
            }
        }
    }

    public boolean maxPlayersReached(){
        return getPlayerCount() >= PLAYERS_PER_ROOM;

    }
    public boolean hasPlayer(String playerId){
        boolean x =players.stream().anyMatch(p -> p.getId().equals(playerId));
       return x;
    }
    public Player getPlayer(String playerId){
        Optional<Player> searched =  players.stream().filter(p -> p.getId().equals(playerId)).findFirst();
        if(!searched.isPresent()){
            return null;
        }
        return searched.get();
    }
    public List<Player> getFinishedPlayers(){
        return players.stream().filter(p-> p.isFinished()).sorted(Comparator.comparing(Player::getMistakes)).collect(Collectors.toList());
    }
    public List<Player> getPlayingPlayers(){
        return players.stream().filter(p-> !p.isFinished()).sorted(Comparator.comparing(Player::getMistakes)).collect(Collectors.toList());
    }
    public String getScores(String delimiter){
        return players.stream().map(Player::getScore).collect(Collectors.joining(delimiter));
    }
    public int getPlayerCount(){
        return players.size();
    }
    public List<Player> getPlayers() {
        return players;
    }

    public String getId() {
        return id;
    }
}

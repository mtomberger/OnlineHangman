package at.hangman.hangman;

public class Room {
    private String player1Name;
    private String player2Name;

    // Dieses Wort wird vom Spieler1 ausgewählt
    private String wordForPlayer2;

    // Dieses Wort wird vom Spieler1 ausgewählt
    private String wordForPlayer1;

    private int anzMistakesPlayer1=0;
    private int anzMistakesPlayer2=0;

    public String getPlayer1Name() {
        return player1Name;
    }

    public void setPlayer1Name(String player1Name) {
        this.player1Name = player1Name;
    }

    public String getPlayer2Name() {
        return player2Name;
    }

    public void setPlayer2Name(String player2Name) {
        this.player2Name = player2Name;
    }

    public String getWordForPlayer2() {
        return wordForPlayer2;
    }

    public void setWordForPlayer2(String wordForPlayer2) {
        this.wordForPlayer2 = wordForPlayer2;
    }

    public String getGetWordForPlayer1() {
        return wordForPlayer1;
    }

    public void setGetWordForPlayer1(String getWordForPlayer1) {
        this.wordForPlayer1 = getWordForPlayer1;
    }
}

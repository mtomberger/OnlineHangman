package entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class Game {

    @Id
    private Player player1;

    private Player player2;

    private Player winner;

    private LocalDateTime dateTime;

    @Column(name = "wordGuess1")
    private String wordGuess1;

    @Column(name = "wordGuess2")
    private String wordGuess2;
}

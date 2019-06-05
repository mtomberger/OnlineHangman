package hangmandb.hangmandb.services;

import entities.Game;
import entities.Player;

import javax.persistence.*;
import java.util.List;

public class DBService {
    private static final String persistenceUnitName = "unithangman";

    private static EntityManagerFactory factory;
    private static EntityManager manager;
    private static EntityTransaction transaction;

    public DBService() {
        factory = Persistence.createEntityManagerFactory(persistenceUnitName);
        manager = factory.createEntityManager();
        transaction = manager.getTransaction();
    }

    public Player addPlayer(Player player) {
        transaction.begin();
        manager.persist(player);
        transaction.commit();
        return player;
    }

    public Game addGame(Game game) {
        transaction.begin();
        manager.persist(game);
        transaction.commit();
        return game;
    }

    public List<Game> getScoreBoard(int size) {
        TypedQuery<Game> q2 = manager.createQuery("SELECT g FROM Game g", Game.class);
        q2.setMaxResults(size);

        return q2.getResultList();
    }
}

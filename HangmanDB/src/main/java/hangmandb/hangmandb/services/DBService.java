package hangmandb.hangmandb.services;

import entities.Score;

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

    public Score addGame(Score score) {
        transaction.begin();
        manager.persist(score);
        transaction.commit();
        return score;
    }

    public List<Score> getScoreBoard(int size) {
        TypedQuery<Score> q2 = manager.createQuery("SELECT g FROM game g", Score.class);
        q2.setMaxResults(size);

        return q2.getResultList();
    }
}

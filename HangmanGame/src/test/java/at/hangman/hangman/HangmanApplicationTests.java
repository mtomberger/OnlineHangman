package at.hangman.hangman;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HangmanApplicationTests {

    @Test
    public void contextLoads() {
    }
    @Test
    public void scoreCalculationTest() {
        ScoreCalculator c = ScoreCalculator.getInstance();
        int[] mistakes = new int[10];
        int[] time = new int[10];
        for(int i=0;i<mistakes.length;i++){
            mistakes[i] = c.calculateScore("abc",mistakes.length-i,100);
        }
        for(int i=0;i<time.length;i++){
            time[i] = c.calculateScore("abc",0,i*50);
        }
        int s1 = c.calculateScore("awkward",4,100);
        int s2 = c.calculateScore("happy",4,100);
        int s3 = c.calculateScore("car",4,100);

        int s4 = c.calculateScore("death",0,56);
        int s5 = c.calculateScore("car",11,27);

        //more mistakes, less score
        int prevS = mistakes[0];
        for(int i=1;i<mistakes.length;i++){
            Assert.assertTrue(mistakes[i]>prevS);
            prevS = mistakes[i];
        }
        //more needed time, less score (<=: No Time bonus if certain max time is reached)
        prevS = time[0];
        for(int i=1;i<time.length;i++){
            Assert.assertTrue(time[i]<=prevS);
            prevS = time[i];
        }
        //complexer word, more score
        Assert.assertTrue(s1>s2);
        Assert.assertTrue(s2>s3);

        //all together
        Assert.assertTrue(s4>s5);


    }

}

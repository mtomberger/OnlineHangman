package at.hangman.hangman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class ScoreCalculator {
    private static ScoreCalculator INSTANCE;
    private static HashMap<Character,Float> frequencies;

    private static final int MAX_SECONDS = 300;
    private static final double BASE_SCORE_PER_LETTER = 100;

    private ScoreCalculator(){
        fillFrequencies();
    }
    private void fillFrequencies(){
        frequencies = new HashMap<>();
        frequencies.put('E',12.02f);
        frequencies.put('T',9.10f);
        frequencies.put('A',8.12f);
        frequencies.put('O',7.68f);
        frequencies.put('I',7.31f);
        frequencies.put('N',6.95f);
        frequencies.put('S',6.28f);
        frequencies.put('R',6.02f);
        frequencies.put('H',5.92f);
        frequencies.put('D',4.32f);
        frequencies.put('L',3.98f);
        frequencies.put('U',2.88f);
        frequencies.put('C',2.71f);
        frequencies.put('M',2.61f);
        frequencies.put('F',2.30f);
        frequencies.put('Y',2.11f);
        frequencies.put('W',2.09f);
        frequencies.put('G',2.03f);
        frequencies.put('P',1.82f);
        frequencies.put('B',1.49f);
        frequencies.put('V',1.11f);
        frequencies.put('K',0.69f);
        frequencies.put('X',0.17f);
        frequencies.put('Q',0.11f);
        frequencies.put('J',0.10f);
        frequencies.put('Z',0.07f);

    }
    public static ScoreCalculator getInstance(){
        if(INSTANCE==null){
            INSTANCE = new ScoreCalculator();
        }
        return INSTANCE;
    }
    public int calculateScore(String word, int mistakes, long secondsNeeded){
        word = word.toUpperCase();
        int wordL = word.length();
        double score = 0;
        List<Character> wordChars = new ArrayList<>();
        for(char c : word.toCharArray()){
            wordChars.add(c);
        }
        wordChars = wordChars.stream().distinct().collect(Collectors.toList());
        for(char c : wordChars){
            //score per unique letter, frequency
            score += BASE_SCORE_PER_LETTER + 1/frequencies.get(c);
        }
        //less score per mistake
        score = score * 1/(mistakes+1);
        //less score per needed seconds (time bonus)
        double secondsMultiplier = MAX_SECONDS-secondsNeeded<0?1:1+(MAX_SECONDS-secondsNeeded)/(MAX_SECONDS/100.0)/100;
        return (int)Math.floor(score * secondsMultiplier);
    }
}

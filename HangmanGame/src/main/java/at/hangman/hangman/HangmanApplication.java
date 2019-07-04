package at.hangman.hangman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = ScoreController.class)
public class HangmanApplication {
    public static void main(String[] args) {
        SpringApplication.run(HangmanApplication.class, args);
    }
}

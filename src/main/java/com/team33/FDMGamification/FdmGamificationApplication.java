package com.team33.FDMGamification;

import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@SpringBootApplication
public class FdmGamificationApplication {

	private final Logger log = LoggerFactory.getLogger(FdmGamificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FdmGamificationApplication.class, args);
	}

	@Bean
	public CommandLineRunner run(ChallengeService cls, QuestionService qts, ChoiceService chs) {
		return args -> {
//			Challenge challenge1 = cls.create("First challenge", 0);
//			Question question1 = qts.create("Question 1, Hello ", challenge1.getId());
//			Choice choice1 = chs.create("Choice 1, World", 2, question1.getQuestionId());
//			Choice choice2 = chs.create("Choice 2, Bye bye", 1, question1.getQuestionId());
		};
	}
}

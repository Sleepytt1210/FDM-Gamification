package com.team33.FDMGamification;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootApplication
public class FdmGamificationApplication {

	@Autowired
	private ChallengeService challengeService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private ChoiceService choiceService;

	private final Logger log = LoggerFactory.getLogger(FdmGamificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FdmGamificationApplication.class, args);
	}

	/**
	 * Adding mock data to the server on startup
	 *
	 */
	@Bean
	public void run(){
		List<Challenge> challenges = new ArrayList<>();
		log.info("Adding mock data");
		try (BufferedReader br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("Mock data.csv").getFile().replace("%20", " "), StandardCharsets.UTF_8))) {
			// Get number of challenges
			int n = br.readLine().codePointAt(0) - '0';
			for (int i = 0; i < n; i++) {
				String[] line = br.readLine().split(",");
				int comp = Integer.parseInt(line[2]);
				Challenge challenge = challengeService.create(new Challenge(line[0], line[1], line[3], line[4], comp));
				// Get number of questions
				int m = (br.readLine().charAt(0) - '0');
				for (int j = 0; j < m; j++) {
					String[] qLine = br.readLine().split(",");
					int qComp = Integer.parseInt(qLine[2]);
					Question question = questionService.create(challenge, new Question(qLine[0], qLine[1], qComp));
					// Get number of choices
					int c = (br.readLine().charAt(0) - '0');
					for (int k = 0; k < c; k++) {
						String[] cLine = br.readLine().split(",");
						int cWeight = Integer.parseInt(cLine[2]);
						Choice choice = new Choice(cLine[0], cWeight, cLine[1]);
						choiceService.create(question, choice);
					}
				}
			}
			log.info("Finished adding mock data.");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}
}

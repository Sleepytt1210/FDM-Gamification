package com.team33.FDMGamification;

import com.team33.FDMGamification.Model.*;
import com.team33.FDMGamification.Service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;


@SpringBootApplication
public class FdmGamificationApplication {

	@Autowired
	private ChallengeService challengeService;

	@Autowired
	private QuestionService questionService;

	@Autowired
	private ChoiceService choiceService;

	@Autowired
	private ChallengeFeedbackService cfs;

	private final Logger log = LoggerFactory.getLogger(FdmGamificationApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(FdmGamificationApplication.class, args);
	}

	/**
	 * Adding mock data to the server on startup
	 *
	 */
	@Bean
	@Transactional
	public void run(){
		log.info("Adding mock data");
		try (BufferedReader br = new BufferedReader(new FileReader(getClass().getClassLoader().getResource("Mock data.csv").getFile().replace("%20", " "), StandardCharsets.UTF_8))) {
			// Get number of challenges
			int n = br.readLine().codePointAt(0) - '0';
			for (int i = 0; i < n; i++) {
				String[] line = br.readLine().split(",");
				int comp = Integer.parseInt(line[2]);
				Thumbnail processed = processImage(line[3]);
				Challenge challenge = challengeService.create(new Challenge(line[0], line[1], Stream.valueOf(line[4]), comp));
				challenge.updateThumbnailProperties(processed);
				// Get number of questions
				int m = (br.readLine().charAt(0) - '0');
				for (int j = 0; j < m; j++) {
					String[] qLine = br.readLine().split(",");
					int qComp = Integer.parseInt(qLine[2]);
					Question question = questionService.create(challenge, new Question(qLine[0], qLine[1], qComp, QuestionType.valueOf(qLine[3])));
					// Get number of choices
					int c = (br.readLine().charAt(0) - '0');
					for (int k = 0; k < c; k++) {
						String[] cLine = br.readLine().split(",");
						int cWeight = Integer.parseInt(cLine[2]);
						Choice choice = new Choice(cLine[0], cWeight, cLine[1]);
						choiceService.create(question, choice);
					}
				}
				cfs.create(challenge, new ChallengeFeedback("Good Job", "You did so well, here's some links for reference", true));
				cfs.create(challenge, new ChallengeFeedback("Oh No", "You did so badly, here's some links to improve yourself", false));
			}
			log.info("Finished adding mock data.");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private Thumbnail processImage(String path){
		try {
			File resource = new ClassPathResource(path).getFile();
			Path filePath = resource.toPath();
			String fileName = resource.getName();
			String mimeType = Files.probeContentType(filePath);
			String base64 = Base64Utils.encodeToString(Files.readAllBytes(resource.toPath()));

			return new Thumbnail(fileName, mimeType, base64);
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
		return null;
	}
}

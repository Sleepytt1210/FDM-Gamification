package com.team33.FDMGamification;

import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.DAO.ChoiceRepository;
import com.team33.FDMGamification.DAO.QuestionRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeService;
import com.team33.FDMGamification.Service.ChoiceService;
import com.team33.FDMGamification.Service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class DatabaseTests {

    @TestConfiguration
    static class QuestionServiceTestConfiguration{
        @Bean
        protected QuestionService questionService(){
            return new QuestionService();
        }
    }

    @TestConfiguration
    static class ChoiceServiceTestConfiguration{
        @Bean
        protected ChoiceService choiceService(){
            return new ChoiceService();
        }
    }

    @TestConfiguration
    static class ChallengeServiceTestConfiguration{
        @Bean
        protected ChallengeService challengeService(){
            return new ChallengeService();
        }
    }

    @Autowired
    private ChallengeService challengeS;

    @Autowired
    private QuestionService questionS;

    @Autowired
    private ChoiceService choiceS;

    @Autowired
    private ChallengeRepository challengeRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChoiceRepository choiceRepo;

    private Challenge challenge1;
    private Question question1;
    private Choice choice1;
    private Choice choice2;

    @BeforeEach
    public void setup(){
        try {
            challenge1 = new Challenge("This is challenge one.", 0);
            challengeS.create(challenge1);
            question1 = new Question("This is question one.");
            questionS.create(question1, challenge1.getId());
            choice1 = new Choice("World", 2);
            choice2 = new Choice("Bye", 1);
            choiceS.create(choice1, question1.getQuestionId());
            choiceS.create(choice2, question1.getQuestionId());
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testChallengeCreateWithProperties() {
        challengeS.create("This is challenge two.", 1);
        assertEquals(2, challengeRepo.findAll().size());
        assertEquals("This is challenge two.", challengeS.findById(2).getIntroduction());
    }

    @Test
    public void testChallengeFindById() {
        Challenge challenge = challengeS.findById(1);
        assertNotNull(challenge);
        assertEquals("This is challenge one.", challenge.getIntroduction());
        assertEquals(0, challengeS.findById(1).getCompletion());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeGetAll() {
        challengeS.create("This is challenge two.", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("This is challenge three.", 100);
        assertEquals(3, challengeS.getAll().size());
    }

    @Test
    public void testChallengeUpdateOne() {
        String newIntro = "This is challenge 1.";

        assertEquals("This is challenge one.", challengeS.findById(1).getIntroduction());
        challengeS.update(1, newIntro, 10);

        Challenge updatedChallenge = challengeS.findById(1);
        assertEquals(newIntro, updatedChallenge.getIntroduction());
        assertEquals(10, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeDeleteOneByEntity() {
        challengeS.create("This is challenge two", 0);
        assertEquals(2, challengeS.getAll().size());

        Challenge challenge2 = challengeS.findById(2);
        challengeS.delete(challenge2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeDeleteOneById() {
        challengeS.create("This is challenge two", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.delete(2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeBatchDelete() {
        challengeS.create("This is challenge two", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("This is challenge three", 0);
        assertEquals(3, challengeS.getAll().size());

        List<Challenge> challengeList = challengeRepo.findAllById(List.of(2,3));
        challengeS.batchDelete(challengeList);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeAddQuestion() {
        Challenge challenge = challengeS.findById(1);

        // Ensure the question is added
        Question question = challenge.getQuestion().get(1);
        assertNotNull(question);

        // Ensure bidirectional relationship from question
        assertEquals(1, question.getQuestionId());
        assertEquals(challenge, question.getChallenge());

        // Ensure the same question cannot be added to the same challenge
        assertThrows(InstanceAlreadyExistsException.class, () -> challengeS.addQuestion(1, question1));
    }

    @Test
    public void testQuestionCreateWithProperties() {
        assertDoesNotThrow(() -> questionS.create("This is question two.", 1));
        assertEquals(2, questionRepo.findAll().size());
        assertEquals("This is question two.", questionS.findById(2).getQuestionText());
    }

    @Test
    public void testQuestionFindById() {
        assertEquals("This is question one.", questionS.findById(1).getQuestionText());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionGetAll() {
        assertDoesNotThrow(() -> questionS.create("This is question two.", 1));
        assertEquals(2, questionS.getAll().size());

        assertDoesNotThrow(() -> questionS.create("This is question three.", 1));
        assertEquals(3, questionS.getAll().size());
    }

    @Test
    public void testQuestionUpdateOne() {
        String newQuestionText = "This is question 1.";

        assertEquals("This is question one.", questionS.findById(1).getQuestionText());
        questionS.update(1, newQuestionText);

        Question updatedQuestion = questionS.findById(1);
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
    }

    @Test
    public void testQuestionDeleteOneByEntity() {
        assertDoesNotThrow(() -> questionS.create("This is question two.", 1));
        assertEquals(2, questionS.getAll().size());

        Question question2 = questionS.findById(2);
        questionS.delete(question2);

        // Ensure only 1 question in database
        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");

        // Ensure question is removed from associated challenge
        assertNull(challenge1.getQuestion().get(2));
    }

    @Test
    public void testQuestionDeleteOneById() {
        // Create a dummy question for deletion
        assertDoesNotThrow(() -> questionS.create("This is question two.", 1));
        assertEquals(2, questionS.getAll().size());

        questionS.delete(2);

        // Ensure only 1 question in database
        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");

        // Ensure question is removed from associated challenge
        assertNull(challenge1.getQuestion().get(2));
    }

    @Test
    public void testQuestionBatchDelete() {
        assertDoesNotThrow(() -> questionS.create("This is question two.", 1));
        assertEquals(2, questionS.getAll().size());

        assertDoesNotThrow(() -> questionS.create("This is question three.", 1));
        assertEquals(3, questionS.getAll().size());

        List<Question> questionList = questionRepo.findAllById(List.of(2,3));
        questionS.batchDelete(questionList);
        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testQuestionAddChoice() {
        Question question = questionS.findById(1);

        // Ensure the question is added
        assertEquals(2, question.getChoices().size());

        Choice choiceA = question.getChoices().get(1);
        Choice choiceB = question.getChoices().get(2);

        assertNotNull(choiceA);
        assertNotNull(choiceB);

        // Ensure bidirectional relationship from choices
        assertEquals(1, choiceA.getId());
        assertEquals(question, choiceA.getQuestion());

        assertEquals(2, choiceB.getId());
        assertEquals(question, choiceB.getQuestion());

        // Ensure the same choice cannot be added to the same question
        assertThrows(InstanceAlreadyExistsException.class, () -> questionS.addChoice(1, choice1));
        assertThrows(InstanceAlreadyExistsException.class, () -> questionS.addChoice(1, choice2));
    }

    @Test
    public void testChoiceCreateWithProperties() {
        assertDoesNotThrow(() -> choiceS.create("Choice C", 2,1));
        assertEquals(3, choiceRepo.findAll().size());
        assertEquals("Choice C", choiceS.findById(3).getChoiceText());
    }

    @Test
    public void testChoiceFindById() {
        assertEquals("World", choiceS.findById(1).getChoiceText());
        assertEquals("Bye", choiceS.findById(2).getChoiceText());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChoiceGetAll() {
        assertEquals(2, choiceS.getAll().size());

        assertDoesNotThrow(() -> choiceS.create("Choice C",2, 1));
        assertEquals(3, choiceS.getAll().size());
    }

    @Test
    public void testChoiceUpdateOne() {
        String newChoiceText = "Hello World!";

        assertEquals("World", choiceS.findById(1).getChoiceText());
        choiceS.update(1, newChoiceText, null);

        Choice updatedChoice = choiceS.findById(1);
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
    }

    @Test
    public void testChoiceDeleteOneByEntity() {
        assertDoesNotThrow(() -> choiceS.create("Choice C", 2, 1));
        assertEquals(3, choiceS.getAll().size());

        Choice choice3 = choiceS.findById(3);
        choiceS.delete(choice3);

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertNull(question1.getChoices().get(3));
    }

    @Test
    public void testChoiceDeleteOneById() {
        // Create a dummy choice for deletion
        assertDoesNotThrow(() -> choiceS.create("Choice C", 2, 1));
        assertEquals(3, choiceS.getAll().size());

        choiceS.delete(3);

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertNull(question1.getChoices().get(3));
    }

    @Test
    public void testChoiceBatchDelete() {
        assertDoesNotThrow(() -> choiceS.create("Choice C",  2,1));
        assertEquals(3, choiceS.getAll().size());

        assertDoesNotThrow(() -> choiceS.create("Choice D", 1,1));
        assertEquals(4, choiceS.getAll().size());

        List<Choice> choiceList = choiceRepo.findAllById(List.of(3,4));
        choiceS.batchDelete(choiceList);
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(4), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionAndChoicesOnDeleteCascade() {
        challengeS.delete(1);

        // Ensure questions are deleted
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(1));
        assertEquals(0, questionS.getAll().size());

        // Ensure choices are deleted
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(1));
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(2));
        assertEquals(0, choiceS.getAll().size());
    }

    @Test
    public void testChoicesOnDeleteCascade() {
        questionS.delete(1);

        // Ensure question is deleted
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(1));
        assertEquals(0, questionS.getAll().size());
        assertEquals(0, challenge1.getQuestion().size());

        // Ensure choices are deleted
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(1));
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(2));
        assertEquals(0, choiceS.getAll().size());
    }

    // Child entity retrieved from database check

    @Test
    public void testChallengeFromDatabaseHasQuestion() {
        Challenge challenge = challengeS.findById(1);
        System.out.println(challenge);
        assertNotNull(challenge);
        assertEquals(1, challenge.getQuestion().size());
        assertNotNull(challenge.getQuestion().get(1));
    }

    @Test
    public void testQuestionFromDatabaseHasChoices() {
        Question question = questionS.findById(1);
        assertNotNull(question);
        assertEquals(2, question.getChoices().size());
        assertNotNull(question.getChoices().get(1));
        assertNotNull(question.getChoices().get(2));
    }

}

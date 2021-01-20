package com.team33.FDMGamification;

import com.team33.FDMGamification.DAO.*;
import com.team33.FDMGamification.Model.*;
import com.team33.FDMGamification.Service.*;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @TestConfiguration
    static class ChallengeFeedbackServiceTestConfiguration{
        @Bean
        protected ChallengeFeedbackService challengeFeedbackService(){
            return new ChallengeFeedbackService();
        }
    }

    @TestConfiguration
    static class RatingServiceTestConfiguration{
        @Bean
        protected RatingService ratingService() {
            return new RatingService();
        }
    }

    @TestConfiguration
    static class ThumbnailServiceTestConfiguration{
        @Bean
        protected ThumbnailService thumbnailService() {
            return new ThumbnailService();
        }
    }

    @Autowired
    private ChallengeService challengeS;
    @Autowired
    private QuestionService questionS;
    @Autowired
    private ChoiceService choiceS;
    @Autowired
    private ChallengeFeedbackService challengeFbS;
    @Autowired
    private RatingService ratingS;
    @Autowired
    private ThumbnailService thumbnailS;
    @Autowired
    private ChallengeRepository challengeRepo;
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private ChoiceRepository choiceRepo;
    @Autowired
    private RatingRepository ratingRepo;
    @Autowired
    private ThumbnailRepository thumbnailRepo;
    private Challenge challenge1;
    private Question question1;
    private ChallengeFeedback feedback1;
    private ChallengeFeedback feedback2;
    private Choice choice1;
    private Choice choice2;
    private Rating rating1;
    private Rating rating2;
    private Rating rating3;

    @BeforeEach
    public void setup() {
        try {
            challenge1 = new Challenge("Challenge one", "This is challenge one.", Stream.ST, 0);
            challenge1 = challengeS.create(challenge1);
            question1 = new Question("Question one", "This is question one.", 0, QuestionType.DRAG_DROP);
            question1 = questionS.create(challenge1.getId(), question1);
            feedback1 = new ChallengeFeedback("Congratulation!", "You scored well!", true);
            feedback2 = new ChallengeFeedback("Oh no!", "You need to work harder!", false);
            feedback1 = challengeFbS.create(challenge1.getId(), feedback1);
            feedback2 = challengeFbS.create(challenge1.getId(), feedback2);
            choice1 = new Choice("World", 2, "Because Hello World");
            choice2 = new Choice("Bye", 1, "Because not Hello Bye");
            choice1 = choiceS.create(question1.getQuestionId(), choice1);
            choice2 = choiceS.create(question1.getQuestionId(), choice2);
            rating1 = new Rating(5);
            rating2 = new Rating(3);
            rating3 = new Rating(1);
            rating1 = ratingS.create(challenge1.getId(), rating1);
            rating2 = ratingS.create(challenge1.getId(), rating2);
            rating3 = ratingS.create(challenge1.getId(), rating3);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testChallengeCreateWithProperties() {
        challengeS.create("Challenge two", "This is challenge two.", Stream.ST, 1);
        assertEquals(2, challengeRepo.findAll().size());
        assertEquals("This is challenge two.", challengeS.findById(2).getDescription());
    }

    @Test
    public void testChallengeFindById() {
        Challenge challenge = challengeS.findById(challenge1.getId());
        assertNotNull(challenge);
        assertEquals("This is challenge one.", challenge.getDescription());
        assertEquals(0, challengeS.findById(challenge1.getId()).getCompletion());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeGetAll() {
        challengeS.create("Challenge two", "This is challenge two.", Stream.ST, 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("Challenge three", "This is challenge three.", Stream.BI, 100);
        assertEquals(3, challengeS.getAll().size());
    }

    @Test
    public void testChallengeUpdateOne() {
        String newTitle = "Challenge 1";
        String newIntro = "This is challenge 1.";
        Integer newCompletion = 10;
        Stream newStream = Stream.BI;

        assertEquals("Challenge one", challengeS.findById(challenge1.getId()).getChallengeTitle());
        assertEquals("This is challenge one.", challengeS.findById(challenge1.getId()).getDescription());
        challengeS.update(challenge1.getId(), newTitle, newIntro, null, newStream, newCompletion, null, null, null);

        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        assertEquals(newTitle, updatedChallenge.getChallengeTitle());
        assertEquals(newIntro, updatedChallenge.getDescription());
        assertEquals(newCompletion, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeUpdateOneByEntity() {
        String newTitle = "Challenge 1";
        String newIntro = "This is challenge 1.";
        Integer newCompletion = 10;
        Stream newStream = Stream.BI;

        Challenge newChallenge = new Challenge(newTitle, newIntro, newStream, newCompletion);

        assertEquals("Challenge one", challengeS.findById(challenge1.getId()).getChallengeTitle());
        assertEquals("This is challenge one.", challengeS.findById(challenge1.getId()).getDescription());
        challengeS.update(1, newChallenge);

        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        assertEquals(newTitle, updatedChallenge.getChallengeTitle());
        assertEquals(newIntro, updatedChallenge.getDescription());
        assertEquals(newCompletion, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeDeleteOneByEntity() {
        challengeS.create("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        Challenge challenge2 = challengeS.findById(2);
        challengeS.delete(challenge2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeDeleteOneById() {
        challengeS.create("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.delete(2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeBatchDelete() {
        challengeS.create("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("Challenge two", "This is challenge three.", Stream.BI, 0);
        assertEquals(3, challengeS.getAll().size());

        List<Challenge> challengeList = challengeRepo.findAllById(List.of(2, 3));
        challengeS.batchDelete(challengeList);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeAddQuestion() {
        Challenge challenge = challengeS.findById(challenge1.getId());

        // Ensure the question is added
        Question question = challenge.getQuestions().get(0);
        assertNotNull(question);

        // Ensure bidirectional relationship from question
        assertEquals(question1.getQuestionId(), question.getQuestionId());
        assertEquals(challenge, question.getChallenge());

    }

    @Test
    public void testQuestionCreateWithProperties() {
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE));
        assertEquals(2, questionRepo.findAll().size());
        assertEquals("This is question two.", questionS.findById(2).getQuestionText());
    }

    @Test
    public void testQuestionFindById() {
        assertEquals("This is question one.", questionS.findById(question1.getQuestionId()).getQuestionText());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionGetAll() {
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE));
        assertEquals(2, questionS.getAll().size());

        assertDoesNotThrow(() -> questionS.create(1, "Question three", "This is question three.", 0, QuestionType.TEXTBOX));
        assertEquals(3, questionS.getAll().size());
    }

    @Test
    public void testQuestionUpdateOne() {
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;

        assertEquals("This is question one.", questionS.findById(question1.getQuestionId()).getQuestionText());
        questionS.update(1, newQuestionTitle, newQuestionText, newCompletion, null, null);

        Question updatedQuestion = questionS.findById(question1.getQuestionId());
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());
    }

    @Test
    public void testQuestionDeleteOneByEntity() {
        Question question2 = questionS.create(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        questionS.delete(question2);

        // Ensure only 1 question in database
        System.out.println(questionS.getAll());
//        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");

        // Ensure question is removed from associated challenge
        assertFalse(challenge1.getQuestions().contains(question2));
    }

    @Test
    public void testQuestionDeleteOneById() {
        // Create a dummy question for deletion
        Question question2 = questionS.create(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        Integer qid2 = question2.getQuestionId();

        questionS.delete(qid2);

        // Ensure only 1 question in database
        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(qid2), "Expected Entity Not Found to be thrown!");

        // Ensure question is removed from associated challenge
        assertEquals(1, challenge1.getQuestions().size());
    }

    @Test
    public void testQuestionBatchDelete() {
        Question question2 = questionS.create(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        Question question3 = questionS.create(challenge1.getId(), "Question three", "This is question three.", 0, QuestionType.TEXTBOX);
        assertEquals(3, questionS.getAll().size());

        Integer qid2 = question2.getQuestionId();
        Integer qid3 = question3.getQuestionId();

        List<Question> questionList = questionRepo.findAllById(List.of(qid2, qid3));
        questionS.batchDelete(questionList);
        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(qid3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(qid2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testQuestionAddChoice() {
        Question question = questionS.findById(question1.getQuestionId());

        // Ensure the choices are added
        assertEquals(2, question.getChoices().size());

        Choice choiceA = question.getChoices().get(0);
        Choice choiceB = question.getChoices().get(1);

        assertNotNull(choiceA);
        assertNotNull(choiceB);

        // Ensure bidirectional relationship from choices
        assertEquals(choice1.getChoiceId(), choiceA.getChoiceId());
        assertEquals(question, choiceA.getQuestion());

        assertEquals(choice2.getChoiceId(), choiceB.getChoiceId());
        assertEquals(question, choiceB.getQuestion());
    }

    @Test
    public void testChoiceCreateWithProperties() {
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2, "Random weight"));
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

        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2, "Random choice."));
        assertEquals(3, choiceS.getAll().size());
    }

    @Test
    public void testChoiceUpdateOne() {
        String newChoiceText = "Hello World!";

        assertEquals("World", choiceS.findById(1).getChoiceText());
        choiceS.update(1, newChoiceText, null, null);

        Choice updatedChoice = choiceS.findById(1);
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
    }

    @Test
    public void testChoiceDeleteOneByEntity() {
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2, "Random choice."));
        assertEquals(3, choiceS.getAll().size());

        Choice choice3 = choiceS.findById(3);
        choiceS.delete(choice3);

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertFalse(question1.getChoices().contains(choice3));
    }

    @Test
    public void testChoiceDeleteOneById() {
        // Create a dummy choice for deletion
        Choice choice3 = choiceS.create(1, "Choice C", 2, "Random choice.");
        assertEquals(3, choiceS.getAll().size());

        choiceS.delete(3);

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertFalse(question1.getChoices().contains(choice3));
    }

    @Test
    public void testChoiceBatchDelete() {
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2, "Random choice."));
        assertEquals(3, choiceS.getAll().size());

        assertDoesNotThrow(() -> choiceS.create(1, "Choice D", 1, "Random choice 2."));
        assertEquals(4, choiceS.getAll().size());

        List<Choice> choiceList = choiceRepo.findAllById(List.of(3, 4));
        choiceS.batchDelete(choiceList);
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(4), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionAndChoicesOnDeleteCascade() {
        challengeS.delete(1);

        // Ensure questions are deleted
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(question1.getQuestionId()));
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
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(question1.getQuestionId()));
        assertEquals(0, questionS.getAll().size());
        assertEquals(0, challenge1.getQuestions().size());

        // Ensure choices are deleted
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(1));
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(2));
        assertEquals(0, choiceS.getAll().size());
    }

    @Test
    public void testFeedbackFindById() {
        assertDoesNotThrow(() -> challengeFbS.findById(feedback1.getFeedback_id()));
        assertDoesNotThrow(() -> challengeFbS.findById(feedback2.getFeedback_id()));
        ChallengeFeedback feedbackP = challengeFbS.findById(feedback1.getFeedback_id());
        ChallengeFeedback feedbackN = challengeFbS.findById(feedback2.getFeedback_id());

        assertEquals("Congratulation!", feedbackP.getFeedback_title());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedback_title());
        assertFalse(feedbackN.isPositive());

        assertThrows(EntityNotFoundException.class, () -> challengeFbS.findById(3));
    }

    @Test
    public void testFeedbackFindByChallengeIdAndPositive() {
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), true));
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), false));
        ChallengeFeedback feedbackP = challengeFbS.findByPositive(challenge1.getId(), true);
        ChallengeFeedback feedbackN = challengeFbS.findByPositive(challenge1.getId(), false);

        assertEquals("Congratulation!", feedbackP.getFeedback_title());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedback_title());
        assertFalse(feedbackN.isPositive());

        assertThrows(EntityNotFoundException.class, () -> challengeFbS.findByPositive(2, true));
    }

    @Test
    public void testFeedbackGetAll() {
        List<ChallengeFeedback> feedbacks = challengeFbS.getAll();
        assertEquals(2, feedbacks.size());
    }

    @Test
    public void testFeedbackUpdateOne() {
        String newTitle = "Congratz!";
        String newText = "Updated!";
        challengeFbS.update(feedback1.getFeedback_id(), newTitle, newText);

        ChallengeFeedback updated = challengeFbS.findById(feedback1.getFeedback_id());
        assertEquals(newTitle, updated.getFeedback_title());
        assertEquals(newText, updated.getFeedback_text());
        assertTrue(updated.isPositive());
    }

    @Test
    public void testFeedbackDeleteOneById() {
        challengeFbS.delete(feedback1.getFeedback_id());
        assertEquals(1, challengeFbS.getAll().size());
        assertNull(challengeS.findById(challenge1.getId()).getChallengeFeedback().get(true));
    }

    @Test
    public void testFeedbackDeleteOneByEntity() {
        challengeFbS.delete(feedback1);
        assertEquals(1, challengeFbS.getAll().size());
        assertNull(challengeS.findById(challenge1.getId()).getChallengeFeedback().get(true));
    }

    @Test
    public void testFeedbackBatchDelete() {
        List<ChallengeFeedback> feedbacks = List.of(feedback1, feedback2);
        challengeFbS.batchDelete(feedbacks);
        assertEquals(0, challengeFbS.getAll().size());
        assertNull(challengeS.findById(challenge1.getId()).getChallengeFeedback().get(true));
        assertNull(challengeS.findById(challenge1.getId()).getChallengeFeedback().get(false));
    }

    @Test
    public void testRatingCreateWithProperties() {
        assertDoesNotThrow(() -> ratingS.create(challenge1.getId(), 4));
        assertEquals(4, challengeS.findById(challenge1.getId()).getRatings().size());
    }

    @Test
    public void testRatingFindById() {
        assertDoesNotThrow(() -> ratingS.findById(rating1.getRatingId()));
        assertDoesNotThrow(() -> ratingS.findById(rating2.getRatingId()));
        assertDoesNotThrow(() -> ratingS.findById(rating3.getRatingId()));
        assertEquals(5, ratingS.findById(rating1.getRatingId()).getRatingValue());
        assertEquals(3, ratingS.findById(rating2.getRatingId()).getRatingValue());
        assertEquals(1, ratingS.findById(rating3.getRatingId()).getRatingValue());

        assertThrows(EntityNotFoundException.class, () -> ratingS.findById(4));
    }

    @Test
    public void testRatingGetAll() {
        List<Rating> feedbacks = ratingS.getAll();
        assertEquals(3, feedbacks.size());
    }

    @Test
    public void testRatingUpdateOne() {
        Integer newRate = 2;
        ratingS.update(rating1.getRatingId(), newRate);

        Rating updated = ratingS.findById(rating1.getRatingId());
        assertEquals(newRate, updated.getRatingValue());
    }

    @Test
    public void testRatingDeleteOneById() {
        ratingS.delete(rating1.getRatingId());
        assertEquals(2, ratingS.getAll().size());
        assertFalse(challengeS.findById(challenge1.getId()).getRatings().contains(rating1));
    }

    @Test
    public void testRatingDeleteOneByEntity() {
        ratingS.delete(rating1);
        assertEquals(2, ratingS.getAll().size());
        assertFalse(challengeS.findById(challenge1.getId()).getRatings().contains(rating1));
    }

    @Test
    public void testRatingBatchDelete() {
        List<Rating> ratings = List.of(rating1, rating2);
        ratingS.batchDelete(ratings);
        assertEquals(1, ratingS.getAll().size());
        assertFalse(challengeS.findById(challenge1.getId()).getRatings().contains(rating1));
        assertFalse(challengeS.findById(challenge1.getId()).getRatings().contains(rating2));
    }

    @Test
    public void testAverageRatingOnChallenge() {
        String res = "3.0";
        assertEquals(res, challenge1.getAvgRating());
    }

    @Test
    public void testAverageRatingOnChallenge_WithUpdate() {
        Integer newRating = 2;
        assertDoesNotThrow(() -> ratingS.create(challenge1.getId(), newRating));
        String res = "2.8";
        assertEquals(res, challenge1.getAvgRating());
    }

    // Child entity retrieved from database test

    @Test
    public void testChallengeFromDatabaseHasQuestion() {
        Challenge challenge = challengeS.findById(challenge1.getId());
        System.out.println(challenge);
        assertNotNull(challenge);
        assertEquals(1, challenge.getQuestions().size());
        assertNotNull(challenge.getQuestions().get(0));
    }

    @Test
    public void testQuestionFromDatabaseHasChoices() {
        Question question = questionS.findById(question1.getQuestionId());
        assertNotNull(question);
        assertEquals(2, question.getChoices().size());
        assertTrue(question.getChoices().contains(choice1));
        assertTrue(question.getChoices().contains(choice2));
    }

    /**
     * Also tested that choices, questions, challenge feedbacks can be persisted with challenge creation.
     */
    @Test
    public void testChallengeCreationWithFilledQuestion_ChoicesAndChallengeFeedbacks() {

        String newChallengeTitle = "Challenge 5";
        String newChallengeDescription = "This is Challenge 5";

        // Fill in form values of challenges
        Challenge newChallenge = new Challenge();
        newChallenge.setChallengeTitle(newChallengeTitle);
        newChallenge.setDescription(newChallengeDescription);

        // Populate challenge feedback
        ChallengeFeedback positive = new ChallengeFeedback("Good job", "<p>Good job<br /><a href=\"https://www.w3schools.com\">Reference</a></p>\n", true);
        ChallengeFeedback negative = new ChallengeFeedback("Oh no", "<p>Improvemeent needed<br /><a href=\"https://www.w3schools.com\">Reference</a></p>\n", false);

        Map<Boolean, ChallengeFeedback> challengeFeedbackMap = new HashMap<>();
        challengeFeedbackMap.put(true, positive);
        challengeFeedbackMap.put(false, negative);
        newChallenge.setChallengeFeedback(challengeFeedbackMap);

        // Populate stream
        newChallenge.setStream(Stream.ST);

        // Populate questions
        List<Question> newQuestions = newChallenge.getQuestions();

        Question question51 = new Question("Question 5.1", "This is question 5.1", 0, QuestionType.MULTIPLE_CHOICE);
        List<Choice> choices51 = question51.getChoices();
        choices51.add(new Choice("Choice 5.1.1", 2, "Perfect choice"));
        choices51.add(new Choice("Choice 5.1.2", 1, "Not bad"));
        choices51.add(new Choice("Choice 5.1.3", 0, "Bad choice"));

        newQuestions.add(question51);

        Question question52 = new Question("Question 5.2", "This is question 5.2", 0, QuestionType.TEXTBOX);
        List<Choice> choices52 = question52.getChoices();
        choices52.add(new Choice("Choice 5.2.1", 2, "Perfect chocie"));

        newQuestions.add(question52);

        assertDoesNotThrow(()->{
            Challenge updatedChallege = challengeS.create(newChallenge);
            // Ensure 2 questions are persisted
            assertEquals(2, updatedChallege.getQuestions().size());

            // Ensure choices are persisted for each question
            assertEquals(4, updatedChallege.getQuestions().get(0).getChoices().size());
            assertEquals(1, updatedChallege.getQuestions().get(1).getChoices().size());

            // Ensure challenge feedbacks are persisted
            assertEquals("Good job", updatedChallege.getChallengeFeedback().get(true).getFeedback_title());
            assertEquals("Oh no", updatedChallege.getChallengeFeedback().get(false).getFeedback_title());

            // Ensure correct value of challenge stream
            assertEquals(Stream.ST, updatedChallege.getStream());
        });
    }

    @Test
    public void testQuestionCreationWithExistingChallenge() {
        Question question = new Question("New question", "This is question 1", 0, QuestionType.MULTIPLE_CHOICE);
        question.getChoices().add(new Choice("Choice A", 2, "Perfect choice"));
        question.getChoices().add(new Choice("Choice B", 1, "Not bad"));
        assertDoesNotThrow(()->{
            questionS.create(challenge1.getId(), question);
        });
        assertEquals(2, challengeS.findById(challenge1.getId()).getQuestions().size());
        assertEquals(2, challengeS.findById(challenge1.getId()).getQuestions().get(1).getChoices().size());
    }

    @Test
    public void testQuestionUpdatedInChallenge() {
        // Update the question
        int questionId = question1.getQuestionId();
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;

        assertEquals("This is question one.", questionS.findById(questionId).getQuestionText());
        questionS.update(questionId, newQuestionTitle, newQuestionText, newCompletion, null, null);

        Question updatedQuestion = questionS.findById(questionId);
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());

        Challenge challenge = challengeS.findById(updatedQuestion.getChallenge().getId());
        assertEquals(newQuestionTitle, challenge.getQuestions().get(0).getQuestionTitle());
        assertEquals(newQuestionText, challenge.getQuestions().get(0).getQuestionText());
        assertEquals(newCompletion, challenge.getQuestions().get(0).getQuestionCompletion());
    }

    @Test
    public void testUpdateQuestionFromChallenge() {
        String newChallengeDescription = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newQuestionTitle = "New question";
        String newQuestionText = "This is new question.";
        Integer newQuestionCompletion = 10;

        // Create an updated dummy challenge (Not persisted)
        Challenge updatedChallenge = new Challenge(null, newChallengeDescription, null, newChallengeCompletion);

        // Create an updated dummy question that is linked to dummy challenge (Not persisted)
        Question newQuestion = new Question(newQuestionTitle, newQuestionText, newQuestionCompletion, QuestionType.TEXTBOX);
        newQuestion.setQuestionId(question1.getQuestionId());
        newQuestion.setChallenge(challenge1);

        // Put the question into the challenge set.
        updatedChallenge.getQuestions().add(newQuestion);

        // Before updates
        assertEquals(challenge1.getChallengeTitle(), challengeS.findById(challenge1.getId()).getChallengeTitle());
        assertEquals(challenge1.getDescription(), challengeS.findById(challenge1.getId()).getDescription());

        challengeS.update(newQuestion.getChallenge().getId(), updatedChallenge);

        // After updates
        updatedChallenge = challengeS.findById(challenge1.getId());
        Question updatedQuestion = questionS.findById(question1.getQuestionId());

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeDescription, updatedChallenge.getDescription());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        // Check question update
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newQuestionCompletion, updatedQuestion.getQuestionCompletion());
        assertEquals(QuestionType.TEXTBOX, updatedQuestion.getQuestionType());
    }

    @Test
    public void testUpdateChoiceFromChallenge() {
        String newChallengeDescription = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newChoiceText = "This is new choice";
        int newChoiceWeight = 0;
        String newChoiceReason = "This is a bad choice because its not good.";

        // Create an updated dummy challenge (Not persisted)
        challenge1.setDescription(newChallengeDescription);
        challenge1.setCompletion(newChallengeCompletion);
        
        // Create an update dummy choice that is linked to dummy question1 (Not persisted)
        choice1.setChoiceText(newChoiceText);
        choice1.setChoiceWeight(newChoiceWeight);
        choice1.setChoiceReason(newChoiceReason);

        // Update
        challengeS.update(choice1.getQuestion().getChallenge().getId(), challenge1);

        // After updates
        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        Choice updatedChoice = choiceS.findById(question1.getQuestionId());

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeDescription, updatedChallenge.getDescription());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        System.out.println(updatedChallenge);

        // Check choice update
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
        assertEquals(newChoiceWeight, updatedChoice.getChoiceWeight());
        assertEquals(newChoiceReason, updatedChoice.getChoiceReason());
    }
}

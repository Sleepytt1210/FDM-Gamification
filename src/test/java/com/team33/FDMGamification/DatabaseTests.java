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

import javax.persistence.EntityNotFoundException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE, connection = EmbeddedDatabaseConnection.H2)
@ActiveProfiles(value = "test")
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

    /*
     * Create without any child entities for testing purpose
     */
    private Challenge challengeCreateShortHand(String title, String description, Stream stream, Integer completion){
        return challengeS.create(title, description, stream, completion, null, null, null);
    }

    private Question questionCreateShortHand(Integer challengeId, String title, String text, Integer completion, QuestionType questionType){
        return questionS.create(challengeId, title, text, completion, questionType, null);
    }

    @BeforeEach
    public void setup() {
        try {
            System.out.println("Initializing...");
            challenge1 = challengeS.findById(1);
            question1 = questionS.findById(1);
            choice1 = choiceS.findById(1);
            choice2 = choiceS.findById(2);
            rating1 = ratingS.findById(1);
            rating2 = ratingS.findById(2);
            rating3 = ratingS.findById(3);
            feedback1 = challengeFbS.findById(1);
            feedback2 = challengeFbS.findById(2);
            System.out.println("Finished initializing");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testChallengeCreateWithProperties() {
        challengeCreateShortHand("Challenge two", "This is challenge two.", Stream.ST, 1);
        assertEquals(2, challengeRepo.findAll().size());
        assertEquals("Challenge two", challengeS.findById(2).getChallengeTitle());
    }

    @Test
    public void testChallengeFindById() {
        Challenge challenge = challengeS.findById(challenge1.getId());
        assertNotNull(challenge);
        assertEquals("Challenge one", challenge.getChallengeTitle());
        assertEquals(0, challengeS.findById(challenge1.getId()).getCompletion());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeGetAll() {
        challengeCreateShortHand("Challenge two", "This is challenge two.", Stream.ST, 0);
        assertEquals(2, challengeS.getAll().size());

        challengeCreateShortHand("Challenge three", "This is challenge three.", Stream.BI, 100);
        assertEquals(3, challengeS.getAll().size());
    }

    @Test
    public void testChallengeUpdateOne() {
        String newTitle = "Challenge 1";
        String newIntro = "This is challenge 1.";
        Integer newCompletion = 10;
        Stream newStream = Stream.BI;

        challengeS.update(challenge1, newTitle, newIntro, newStream, newCompletion, null, null, null);

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

        challenge1.setChallengeTitle(newTitle);
        challenge1.setDescription(newIntro);
        challenge1.setCompletion(newCompletion);
        challenge1.setStream(newStream);

        challengeS.update(challenge1);

        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        assertEquals(newTitle, updatedChallenge.getChallengeTitle());
        assertEquals(newIntro, updatedChallenge.getDescription());
        assertEquals(newCompletion, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeDeleteOneByEntity() {
        Challenge challenge2 = challengeCreateShortHand("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.delete(challenge2);

        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(challenge2.getId()), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeDeleteOneById() {
        challengeCreateShortHand("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.delete(2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeBatchDelete() {
        Challenge challenge2 = challengeCreateShortHand("Challenge two", "This is challenge two.", Stream.ST,0);
        assertEquals(2, challengeS.getAll().size());

        Challenge challenge3 = challengeCreateShortHand("Challenge two", "This is challenge three.", Stream.BI, 0);
        assertEquals(3, challengeS.getAll().size());

        List<Challenge> challengeList = challengeRepo.findAllById(List.of(challenge2.getId(), challenge3.getId()));
        challengeS.batchDelete(challengeList);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(challenge2.getId()), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(challenge3.getId()), "Expected Entity Not Found to be thrown!");

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
        Question question2 = questionCreateShortHand(1, "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionRepo.findAll().size());
        assertEquals("This is question two.", questionS.findById(2).getQuestionText());
    }

    @Test
    public void testQuestionFindById() {
        assertEquals(question1.getQuestionText(), questionS.findById(question1.getQuestionId()).getQuestionText());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionGetAll() {
        Question question2 = questionCreateShortHand(1, "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        Question question3 = questionCreateShortHand(1, "Question three", "This is question three.", 0, QuestionType.TEXTBOX);
        assertEquals(3, questionS.getAll().size());
    }

    @Test
    public void testQuestionUpdateOne() {
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;

        assertEquals(question1.getQuestionText(), questionS.findById(question1.getQuestionId()).getQuestionText());
        questionS.update(question1.getQuestionId(), newQuestionTitle, newQuestionText, newCompletion, null);

        Question updatedQuestion = questionS.findById(question1.getQuestionId());
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());
    }

    @Test
    public void testQuestionDeleteOneByEntity() {
        Question question2 = questionCreateShortHand(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        questionS.delete(question2);

        // Ensure only 1 question in database
        System.out.println(questionS.getAll());
//        assertEquals(1, questionS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(question2.getQuestionId()), "Expected Entity Not Found to be thrown!");

        // Ensure question is removed from associated challenge
        assertFalse(challenge1.getQuestions().contains(question2));
    }

    @Test
    public void testQuestionDeleteOneById() {
        // Create a dummy question for deletion
        Question question2 = questionCreateShortHand(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
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
        Question question2 = questionCreateShortHand(challenge1.getId(), "Question two", "This is question two.", 0, QuestionType.MULTIPLE_CHOICE);
        assertEquals(2, questionS.getAll().size());

        Question question3 = questionCreateShortHand(challenge1.getId(), "Question three", "This is question three.", 0, QuestionType.TEXTBOX);
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
        Choice choice3 = choiceS.create(question1.getQuestionId(), "Choice C", 2, "Random weight");
        assertEquals(3, choiceRepo.findAll().size());
        assertEquals("Choice C", choiceS.findById(choice3.getChoiceId()).getChoiceText());
    }

    @Test
    public void testChoiceFindById() {
        assertEquals("A", choiceS.findById(choice1.getChoiceId()).getChoiceText());
        assertEquals("B", choiceS.findById(choice2.getChoiceId()).getChoiceText());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(3), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChoiceGetAll() {
        assertEquals(2, choiceS.getAll().size());

        assertDoesNotThrow(() -> choiceS.create(question1.getQuestionId(), "Choice C", 2, "Random choice."));
        assertEquals(3, choiceS.getAll().size());
    }

    @Test
    public void testChoiceUpdateOne() {
        String newChoiceText = "Hello World!";

        assertEquals("A", choiceS.findById(choice1.getChoiceId()).getChoiceText());
        choiceS.update(choice1.getChoiceId(), newChoiceText, null, null);

        Choice updatedChoice = choiceS.findById(choice1.getChoiceId());
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
    }

    @Test
    public void testChoiceDeleteOneByEntity() {
        Choice choice3 = choiceS.create(question1.getQuestionId(), "Choice C", 2, "Random choice.");
        assertEquals(3, choiceS.getAll().size());

        choiceS.delete(choice3);

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice3.getChoiceId()), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertFalse(question1.getChoices().contains(choice3));
    }

    @Test
    public void testChoiceDeleteOneById() {
        // Create a dummy choice for deletion
        Choice choice3 = choiceS.create(question1.getQuestionId(), "Choice C", 2, "Random choice.");
        assertEquals(3, choiceS.getAll().size());

        choiceS.delete(choice3.getChoiceId());

        // Ensure only 2 choice in database
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice3.getChoiceId()), "Expected Entity Not Found to be thrown!");

        // Ensure choice is removed from associated challenge
        assertFalse(question1.getChoices().contains(choice3));
    }

    @Test
    public void testChoiceBatchDelete() {
        Choice choice3 = choiceS.create(1, "Choice C", 2, "Random choice.");
        assertEquals(3, choiceS.getAll().size());

        Choice choice4 = choiceS.create(1, "Choice D", 1, "Random choice 2.");
        assertEquals(4, choiceS.getAll().size());

        List<Choice> choiceList = choiceRepo.findAllById(List.of(choice3.getChoiceId(), choice4.getChoiceId()));
        choiceS.batchDelete(choiceList);
        assertEquals(2, choiceS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice3.getChoiceId()), "Expected Entity Not Found to be thrown!");
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice4.getChoiceId()), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testQuestionAndChoicesOnDeleteCascade() {
        challengeS.delete(challenge1.getId());

        // Ensure questions are deleted
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(question1.getQuestionId()));
        assertEquals(0, questionS.getAll().size());

        // Ensure choices are deleted
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice1.getChoiceId()));
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice2.getChoiceId()));
        assertEquals(0, choiceS.getAll().size());
    }

    @Test
    public void testChoicesOnDeleteCascade() {
        questionS.delete(question1.getQuestionId());

        // Ensure question is deleted
        assertThrows(EntityNotFoundException.class, () -> questionS.findById(question1.getQuestionId()));
        assertEquals(0, questionS.getAll().size());
        assertEquals(0, challenge1.getQuestions().size());

        // Ensure choices are deleted
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice1.getChoiceId()));
        assertThrows(EntityNotFoundException.class, () -> choiceS.findById(choice2.getChoiceId()));
        assertEquals(0, choiceS.getAll().size());
    }

    @Test
    public void testFeedbackFindById() {
        assertDoesNotThrow(() -> challengeFbS.findById(feedback1.getFeedbackId()));
        assertDoesNotThrow(() -> challengeFbS.findById(feedback2.getFeedbackId()));
        ChallengeFeedback feedbackP = challengeFbS.findById(feedback1.getFeedbackId());
        ChallengeFeedback feedbackN = challengeFbS.findById(feedback2.getFeedbackId());

        assertEquals("Congratulation!", feedbackP.getFeedbackTitle());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedbackTitle());
        assertFalse(feedbackN.isPositive());

        assertThrows(EntityNotFoundException.class, () -> challengeFbS.findById(3));
    }

    @Test
    public void testFeedbackFindByChallengeIdAndPositive() {
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), true));
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), false));
        ChallengeFeedback feedbackP = challengeFbS.findByPositive(challenge1.getId(), true);
        ChallengeFeedback feedbackN = challengeFbS.findByPositive(challenge1.getId(), false);

        assertEquals("Congratulation!", feedbackP.getFeedbackTitle());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedbackTitle());
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
        challengeFbS.update(feedback1.getFeedbackId(), newTitle, newText);

        ChallengeFeedback updated = challengeFbS.findById(feedback1.getFeedbackId());
        assertEquals(newTitle, updated.getFeedbackTitle());
        assertEquals(newText, updated.getFeedbackText());
        assertTrue(updated.isPositive());
    }

    @Test
    public void testFeedbackDeleteOneById() {
        challengeFbS.delete(feedback1.getFeedbackId());
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
        Rating rating4 = ratingS.create(challenge1.getId(), 4);
        assertEquals(4, challengeS.findById(challenge1.getId()).getRatings().size());
    }

    @Test
    public void testRatingFindById() {
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
        assertEquals(res, challengeS.findById(challenge1.getId()).getAvgRating());
    }

    @Test
    public void testAverageRatingOnChallenge_WithUpdate() {
        assertDoesNotThrow(() -> ratingS.create(challenge1.getId(), 2));
        String res = "2.8";
        assertEquals(res, challengeS.findById(challenge1.getId()).getAvgRating());
    }

    /*
     * Integration Tests
     */
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

    /*
     * Test that choices, questions, challenge feedbacks can be persisted with challenge creation.
     * (Simulate add children by creating new parent form)
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

        positive.setChallenge(newChallenge);
        negative.setChallenge(newChallenge);

        Map<Boolean, ChallengeFeedback> challengeFeedbackMap = newChallenge.getChallengeFeedback();
        challengeFeedbackMap.put(true, positive);
        challengeFeedbackMap.put(false, negative);

        // Populate stream
        newChallenge.setStream(Stream.ST);

        // Populate questions
        Question question51 = new Question("Question 5.1", "This is question 5.1", 0, QuestionType.MULTIPLE_CHOICE);
        List<Choice> choices51 = new ArrayList<>();
        choices51.add(new Choice("Choice 5.1.1", 2, "Perfect choice"));
        choices51.add(new Choice("Choice 5.1.2", 1, "Not bad"));
        choices51.add(new Choice("Choice 5.1.3", 0, "Bad choice"));
        for (Choice choice: choices51) {
            question51.addChoice(choice);
        }

        newChallenge.addQuestion(question51);

        Question question52 = new Question("Question 5.2", "This is question 5.2", 0, QuestionType.TEXTBOX);
        List<Choice> choices52 = new ArrayList<>();
        choices52.add(new Choice("Choice 5.2.1", 2, "Perfect choice"));
        for (Choice choice: choices52) {
            question52.addChoice(choice);
        }

        newChallenge.addQuestion(question52);

        // Persist challenge
        Challenge updatedChallenge = challengeS.create(newChallenge);

        // Ensure 2 questions are persisted
        assertEquals(2, updatedChallenge.getQuestions().size());

        // Ensure choices are persisted for each question
        assertEquals(3, updatedChallenge.getQuestions().get(0).getChoices().size());
        assertEquals(1, updatedChallenge.getQuestions().get(1).getChoices().size());

        // Ensure challenge feedbacks are persisted
        assertEquals("Good job", updatedChallenge.getChallengeFeedback().get(true).getFeedbackTitle());
        assertEquals("Oh no", updatedChallenge.getChallengeFeedback().get(false).getFeedbackTitle());

        // Ensure correct value of challenge stream
        assertEquals(Stream.ST, updatedChallenge.getStream());
    }

    /*
     * Simulate creating new children form and select a parent entity to associate to.
     */
    @Test
    public void testQuestionCreationAndAssociateToExistingChallenge() {
        Question question = new Question("New question", "This is question 1", 0, QuestionType.MULTIPLE_CHOICE);
        question.addChoice(new Choice("Choice A", 2, "Perfect choice"));
        question.addChoice(new Choice("Choice B", 1, "Not bad"));

        // Persist question to challenge1
        questionS.create(challenge1, question);

        // Fetch challenge entity
        Challenge fetched = challengeS.findById(challenge1.getId());

        // Ensure the question is persisted in database.
        assertEquals(2, fetched.getQuestions().size());
        assertEquals(2, fetched.getQuestions().get(1).getChoices().size());
    }

    @Test
    public void testChoiceCreationAndAssociateToExistingQuestion() {
        Choice choice = new Choice("Choice C", 2, "Best choice");

        // Persist choice to question1
        choice = choiceS.create(question1.getQuestionId(), choice);

        // Fetch question entity
        Question question = questionS.findById(question1.getQuestionId());

        // Ensure the choice is persisted in database.
        assertEquals(3, question.getChoices().size());
        assertEquals(2, question.getChoices().get(2).getChoiceWeight());
        assertEquals(choice, questionS.findById(question1.getQuestionId()).getChoices().get(2));
    }

    /*
     * Tests entities integration update
     */
    @Test
    public void testQuestionUpdatePersistedInChallengeFetched() {
        // Update the question
        int questionId = question1.getQuestionId();
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;
        QuestionType newQuestionType = QuestionType.MULTIPLE_CHOICE;

        // Ensure question not updated
        assertEquals(question1.getQuestionText(), questionS.findById(questionId).getQuestionText());

        // Update the question
        questionS.update(questionId, newQuestionTitle, newQuestionText, newCompletion, newQuestionType);

        // Fetch updated question
        Question updatedQuestion = questionS.findById(questionId);

        // Ensure the fields are updated
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());
        assertEquals(newQuestionType, updatedQuestion.getQuestionType());

        // Fetch associated challenge from database
        Challenge challenge = challengeS.findById(updatedQuestion.getChallenge().getId());

        // Ensure the fields are updated
        Question questionFromChallenge = challenge.getQuestions().get(0);
        assertEquals(newQuestionTitle, questionFromChallenge.getQuestionTitle());
        assertEquals(newQuestionText, questionFromChallenge.getQuestionText());
        assertEquals(newCompletion, questionFromChallenge.getQuestionCompletion());
    }

    /*
     * Child entities are updated as parent is updated
     */
    @Test
    public void testUpdateQuestionFromExistingChallenge() {
        String newChallengeDescription = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newQuestionTitle = "New question";
        String newQuestionText = "This is new question.";
        Integer newQuestionCompletion = 10;
        QuestionType newQuestionType = QuestionType.TEXTBOX;

        // Create an updated dummy challenge (Not persisted)
        challenge1.setDescription(newChallengeDescription);
        challenge1.setCompletion(newChallengeCompletion);

        // Create an updated dummy question that is linked to dummy challenge (Not persisted)
        question1.setQuestionTitle(newQuestionTitle);
        question1.setQuestionText(newQuestionText);
        question1.setQuestionCompletion(newQuestionCompletion);
        question1.setQuestionType(newQuestionType);

        // Update
        challengeS.update(challenge1);

        // After updates
        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
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
    public void testUpdateChoiceFromExistingChallenge() {
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
        challengeS.update(challenge1);

        // After updates
        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        Choice updatedChoice = choiceS.findById(question1.getQuestionId());

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeDescription, updatedChallenge.getDescription());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        // Check choice update
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
        assertEquals(newChoiceWeight, updatedChoice.getChoiceWeight());
        assertEquals(newChoiceReason, updatedChoice.getChoiceReason());
    }

    /*
     * Child entities create from existing parent entities (Simulate add children by editing parent form)
     */
    @Test
    public void testCreateQuestionFromExistingChallenge() {
        String newChallengeDescription = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newQuestionTitle = "New question";
        String newQuestionText = "This is new question.";
        Integer newQuestionCompletion = 10;
        QuestionType newQuestionType = QuestionType.TEXTBOX;

        // Create an updated dummy challenge (Not persisted)
        challenge1.setDescription(newChallengeDescription);
        challenge1.setCompletion(newChallengeCompletion);


        // Create an new question and adds it to the challenge
        Question newQuestion = new Question();
        newQuestion.setQuestionTitle(newQuestionTitle);
        newQuestion.setQuestionText(newQuestionText);
        newQuestion.setQuestionCompletion(newQuestionCompletion);
        newQuestion.setQuestionType(newQuestionType);

        challenge1.addQuestion(newQuestion);

        /*
         challenge1 will be persisted in database when any modification method is called due to it
         being a entity managed object after using .create(method).
         */
        challengeS.update(challenge1);

        // After updates
        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        Question updatedQuestion = updatedChallenge.getQuestions().get(updatedChallenge.getQuestions().size()-1);

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
    public void testCreateChoiceFromExistingChallenge() {
        String newChallengeDescription = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newChoiceText = "C";
        int newChoiceWeight = 0;
        String newChoiceReason = "Worst choice";

        // Create an updated dummy challenge (Not persisted)
        challenge1.setDescription(newChallengeDescription);
        challenge1.setCompletion(newChallengeCompletion);

        // Create an update dummy choice that is linked to dummy question1 (Not persisted)
        Choice choice = new Choice(newChoiceText, newChoiceWeight, newChoiceReason);
        question1.addChoice(choice);

        // Update
        challengeS.update(challenge1);

        // After updates
        Challenge updatedChallenge = challengeS.findById(challenge1.getId());
        Choice updatedChoice = updatedChallenge.getQuestionById(question1.getQuestionId()).getChoiceByIndex(2);

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeDescription, updatedChallenge.getDescription());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        // Check choice update
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
        assertEquals(newChoiceWeight, updatedChoice.getChoiceWeight());
        assertEquals(newChoiceReason, updatedChoice.getChoiceReason());
    }

    @Test
    public void testCreateChoiceFromExistingQuestion() {
        String newQuestionTitle = "This question is updated";
        Integer newQuestionCompletion = 10;
        QuestionType newQuestionType = QuestionType.MULTIPLE_CHOICE;

        String newChoiceText = "C";
        int newChoiceWeight = 0;
        String newChoiceReason = "Worst choice";

        // Create an updated dummy challenge (Not persisted)
        question1.setQuestionTitle(newQuestionTitle);
        question1.setQuestionCompletion(newQuestionCompletion);
        question1.setQuestionType(newQuestionType);

        // Create an update dummy choice that is linked to dummy question1 (Not persisted)
        Choice choice = new Choice(newChoiceText, newChoiceWeight, newChoiceReason);
        question1.addChoice(choice);

        // Update
        questionS.update(question1);

        // After updates
        Question updatedQuestion = questionS.findById(question1.getQuestionId());
        Choice updatedChoice = updatedQuestion.getChoiceByIndex(question1.getChoices().size()-1);

        // Check Question update
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionCompletion, updatedQuestion.getQuestionCompletion());

        // Check choice update
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
        assertEquals(newChoiceWeight, updatedChoice.getChoiceWeight());
        assertEquals(newChoiceReason, updatedChoice.getChoiceReason());
    }


    /*
     * Child entities replace parent
     */
    @Test
    public void testQuestionReplaceChallenge() {
        // Create a new parent
        Challenge challenge2 =challengeCreateShortHand("Challenge 2", "This is challenge 2", Stream.ST, 0);

        // Before update
        assertEquals(1, challengeS.findById(challenge1.getId()).getQuestions().size());
        Question updatedQuestion = questionS.updateChallenge(challenge2, question1);

        // Fetch
        Challenge challenge01 = challengeS.findById(challenge1.getId());

        // After update
        assertEquals(challenge2.getId() ,updatedQuestion.getChallenge().getId());
        assertEquals(0, challenge01.getQuestions().size());
    }

    @Test
    public void testChoiceReplaceQuestion() {
        // Create a new parent
        Question question2 = questionCreateShortHand(challenge1.getId(),"Question 2", "This is question 2", 0, QuestionType.TEXTBOX);

        // Before update
        assertEquals(2, questionS.findById(question1.getQuestionId()).getChoices().size());
        Choice updatedChoice = choiceS.updateQuestion(question2, choice1);

        // Fetch
        Question question01 = questionS.findById(question1.getQuestionId());

        // After update
        assertEquals(question2.getQuestionId() ,updatedChoice.getQuestion().getQuestionId());
        assertEquals(1, question01.getChoices().size());
    }
}

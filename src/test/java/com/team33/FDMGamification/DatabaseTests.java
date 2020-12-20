package com.team33.FDMGamification;

import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.DAO.ChoiceRepository;
import com.team33.FDMGamification.DAO.QuestionRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.ChallengeFeedback;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Service.ChallengeFeedbackService;
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

    @TestConfiguration
    static class ChallengeFeedbackServiceTestConfiguration{
        @Bean
        protected ChallengeFeedbackService challengeFeedbackService(){
            return new ChallengeFeedbackService();
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
    private ChallengeRepository challengeRepo;

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChoiceRepository choiceRepo;

    private Challenge challenge1;
    private Question question1;
    private ChallengeFeedback feedback1;
    private ChallengeFeedback feedback2;
    private Choice choice1;
    private Choice choice2;

    @BeforeEach
    public void setup(){
        try {
            challenge1 = new Challenge("Challenge one", "This is challenge one.", 0);
            challengeS.create(challenge1);
            question1 = new Question("Question one","This is question one.", 0);
            questionS.create(challenge1.getId(), question1);
            feedback1 = new ChallengeFeedback("Congratulation!", "You scored well!", true);
            feedback2 = new ChallengeFeedback("Oh no!", "You need to work harder!", false);
            challengeFbS.create(challenge1.getId(), feedback1);
            challengeFbS.create(challenge1.getId(), feedback2);
            choice1 = new Choice("World", 2);
            choice2 = new Choice("Bye", 1);
            choiceS.create(question1.getQuestionId(), choice1);
            choiceS.create(question1.getQuestionId(), choice2);
        } catch(Exception e) {
            System.err.println(e.getMessage());
        }
    }

    @Test
    public void testChallengeCreateWithProperties() {
        challengeS.create("Challenge two", "This is challenge two.", 1);
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
        challengeS.create("Challenge two", "This is challenge two.", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("Challenge three", "This is challenge three.", 100);
        assertEquals(3, challengeS.getAll().size());
    }

    @Test
    public void testChallengeUpdateOne() {
        String newTitle = "Challenge 1";
        String newIntro = "This is challenge 1.";
        Integer newCompletion = 10;

        assertEquals("Challenge one", challengeS.findById(1).getChallengeTitle());
        assertEquals("This is challenge one.", challengeS.findById(1).getIntroduction());
        challengeS.update(1, newTitle, newIntro, newCompletion, null);

        Challenge updatedChallenge = challengeS.findById(1);
        assertEquals(newTitle, updatedChallenge.getChallengeTitle());
        assertEquals(newIntro, updatedChallenge.getIntroduction());
        assertEquals(newCompletion, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeUpdateOneByEntity() {
        String newTitle = "Challenge 1";
        String newIntro = "This is challenge 1.";
        Integer newCompletion = 10;

        Challenge newChallenge = new Challenge(newTitle, newIntro, newCompletion);

        assertEquals("Challenge one", challengeS.findById(1).getChallengeTitle());
        assertEquals("This is challenge one.", challengeS.findById(1).getIntroduction());
        challengeS.update(1, newChallenge);

        Challenge updatedChallenge = challengeS.findById(1);
        assertEquals(newTitle, updatedChallenge.getChallengeTitle());
        assertEquals(newIntro, updatedChallenge.getIntroduction());
        assertEquals(newCompletion, updatedChallenge.getCompletion());
    }

    @Test
    public void testChallengeDeleteOneByEntity() {
        challengeS.create("Challenge two", "This is challenge two.", 0);
        assertEquals(2, challengeS.getAll().size());

        Challenge challenge2 = challengeS.findById(2);
        challengeS.delete(challenge2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");

    }

    @Test
    public void testChallengeDeleteOneById() {
        challengeS.create("Challenge two", "This is challenge two.", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.delete(2);
        assertEquals(1, challengeS.getAll().size());
        assertThrows(EntityNotFoundException.class, () -> challengeS.findById(2), "Expected Entity Not Found to be thrown!");
    }

    @Test
    public void testChallengeBatchDelete() {
        challengeS.create("Challenge two", "This is challenge two.", 0);
        assertEquals(2, challengeS.getAll().size());

        challengeS.create("Challenge two", "This is challenge three.", 0);
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
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0));
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
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0));
        assertEquals(2, questionS.getAll().size());

        assertDoesNotThrow(() -> questionS.create(1, "Question three", "This is question three.", 0));
        assertEquals(3, questionS.getAll().size());
    }

    @Test
    public void testQuestionUpdateOne() {
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;

        assertEquals("This is question one.", questionS.findById(1).getQuestionText());
        questionS.update(1, newQuestionTitle, newQuestionText, newCompletion);

        Question updatedQuestion = questionS.findById(1);
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());
    }

    @Test
    public void testQuestionDeleteOneByEntity() {
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0));
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
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0));
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
        assertDoesNotThrow(() -> questionS.create(1, "Question two", "This is question two.", 0));
        assertEquals(2, questionS.getAll().size());

        assertDoesNotThrow(() -> questionS.create(1, "Question three", "This is question three.", 0));
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
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2));
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

        assertDoesNotThrow(() -> choiceS.create(1, "Choice C",2));
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
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2));
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
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C", 2));
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
        assertDoesNotThrow(() -> choiceS.create(1, "Choice C",  2));
        assertEquals(3, choiceS.getAll().size());

        assertDoesNotThrow(() -> choiceS.create(1, "Choice D", 1));
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

    @Test
    public void testFeedbackCreateWithProperties_ThrowInstanceAlreadyExist(){
        assertThrows(InstanceAlreadyExistsException.class,
                () -> challengeFbS.create(1,
                        "Congratulation!",
                        "Existed",
                        true));
        assertThrows(InstanceAlreadyExistsException.class,
                () -> challengeFbS.create(1,
                        "Oh no!",
                        "Existed",
                        false));
    }

    @Test
    public void testFeedbackFindById(){
        assertDoesNotThrow(() -> challengeFbS.findById(feedback1.getFeedback_id()));
        assertDoesNotThrow(() -> challengeFbS.findById(feedback2.getFeedback_id()));
        ChallengeFeedback feedbackP = challengeFbS.findById(feedback1.getFeedback_id());
        ChallengeFeedback feedbackN = challengeFbS.findById(feedback2.getFeedback_id());

        assertEquals("Congratulation!" ,feedbackP.getFeedback_title());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedback_title());
        assertFalse(feedbackN.isPositive());

        assertThrows(EntityNotFoundException.class, () -> challengeFbS.findById(3));
    }

    @Test
    public void testFeedbackFindByChallengeIdAndPositive(){
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), true));
        assertDoesNotThrow(() -> challengeFbS.findByPositive(challenge1.getId(), false));
        ChallengeFeedback feedbackP = challengeFbS.findByPositive(challenge1.getId(), true);
        ChallengeFeedback feedbackN = challengeFbS.findByPositive(challenge1.getId(), false);

        assertEquals("Congratulation!" ,feedbackP.getFeedback_title());
        assertTrue(feedbackP.isPositive());

        assertEquals("Oh no!", feedbackN.getFeedback_title());
        assertFalse(feedbackN.isPositive());

        assertThrows(EntityNotFoundException.class, () -> challengeFbS.findByPositive(2, true));
    }

    @Test
    public void testFeedbackGetAll(){
        List<ChallengeFeedback> feedbacks = challengeFbS.getAll();
        assertEquals(2, feedbacks.size());
    }

    @Test
    public void testFeedbackUpdateOne(){
        String newTitle = "Congratz!";
        String newText = "Updated!";
        challengeFbS.update(feedback1.getFeedback_id(), newTitle, newText);

        ChallengeFeedback updated = challengeFbS.findById(feedback1.getFeedback_id());
        assertEquals(newTitle, updated.getFeedback_title());
        assertEquals(newText, updated.getFeedback_text());
        assertTrue(updated.isPositive());
    }

    @Test
    public void testFeedbackDeleteOneById(){
        challengeFbS.delete(feedback1.getFeedback_id());
        assertEquals(1, challengeFbS.getAll().size());
        assertNull(challengeS.findById(1).getChallengeFeedback().get(true));
    }

    @Test
    public void testFeedbackDeleteOneByEntity(){
        challengeFbS.delete(feedback1);
        assertEquals(1, challengeFbS.getAll().size());
        assertNull(challengeS.findById(1).getChallengeFeedback().get(true));
    }

    @Test
    public void testFeedbackBatchDelete(){
        List<ChallengeFeedback> feedbacks = List.of(feedback1, feedback2);
        challengeFbS.batchDelete(feedbacks);
        assertEquals(0, challengeFbS.getAll().size());
        assertNull(challengeS.findById(1).getChallengeFeedback().get(true));
        assertNull(challengeS.findById(1).getChallengeFeedback().get(false));
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

    @Test
    public void testQuestionUpdatedInChallenge() {
        // Update the question
        int questionId = 1;
        String newQuestionTitle = "Question 1";
        String newQuestionText = "This is question 1.";
        Integer newCompletion = 100;

        assertEquals("This is question one.", questionS.findById(1).getQuestionText());
        questionS.update(questionId, newQuestionTitle, newQuestionText, newCompletion);

        Question updatedQuestion = questionS.findById(questionId);
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newCompletion, updatedQuestion.getQuestionCompletion());

        Challenge challenge = challengeS.findById(updatedQuestion.getChallenge().getId());
        assertEquals(newQuestionTitle, challenge.getQuestion().get(questionId).getQuestionTitle());
        assertEquals(newQuestionText, challenge.getQuestion().get(questionId).getQuestionText());
        assertEquals(newCompletion, challenge.getQuestion().get(questionId).getQuestionCompletion());
    }

    @Test
    public void testUpdateQuestionFromChallenge() {
        String newChallengeIntro = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newQuestionTitle = "New question";
        String newQuestionText = "This is new question.";
        Integer newQuestionCompletion = 10;

        // Create an updated dummy challenge (Not persisted)
        Challenge updatedChallenge = new Challenge(null, newChallengeIntro, newChallengeCompletion);

        // Create an update dummy question that is linked to dummy challenge (Not persisted)
        Question newQuestion = new Question(newQuestionTitle, newQuestionText, newQuestionCompletion);
        newQuestion.setQuestionId(question1.getQuestionId());
        newQuestion.setChallenge(challenge1);

        // Put the question into the challenge set.
        updatedChallenge.getQuestion().put(newQuestion.getQuestionId(), newQuestion);

        // Before updates
        assertEquals(challenge1.getChallengeTitle(), challengeS.findById(1).getChallengeTitle());
        assertEquals(challenge1.getIntroduction(), challengeS.findById(1).getIntroduction());

        challengeS.update(newQuestion.getChallenge().getId(), updatedChallenge);

        // After updates
        updatedChallenge = challengeS.findById(1);
        Question updatedQuestion = questionS.findById(question1.getQuestionId());

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeIntro, updatedChallenge.getIntroduction());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        // Check question update
        assertEquals(newQuestionTitle, updatedQuestion.getQuestionTitle());
        assertEquals(newQuestionText, updatedQuestion.getQuestionText());
        assertEquals(newQuestionCompletion, updatedQuestion.getQuestionCompletion());
    }

    @Test
    public void testUpdateChoiceFromChallenge() {
        String newChallengeIntro = "This challenge is updated";
        Integer newChallengeCompletion = 15;

        String newChoiceText = "This is new choice";
        Integer newChoiceWeight = 0;

        // Create an updated dummy challenge (Not persisted)
        Challenge updatedChallenge = new Challenge(null, newChallengeIntro, newChallengeCompletion);

        // Create an update dummy question that is linked to dummy challenge (Not persisted)
        Choice newChoice = new Choice(newChoiceText, newChoiceWeight);
        newChoice.setId(choice1.getId());
        newChoice.setQuestion(question1);

        // Put the question into the challenge set.
        question1.getChoices().put(newChoice.getId(), newChoice);
        updatedChallenge.getQuestion().put(question1.getQuestionId(), question1);

        // Before updates
        assertEquals(challenge1.getChallengeTitle(), challengeS.findById(1).getChallengeTitle());
        assertEquals(challenge1.getIntroduction(), challengeS.findById(1).getIntroduction());

        challengeS.update(newChoice.getQuestion().getChallenge().getId(), updatedChallenge);

        // After updates
        updatedChallenge = challengeS.findById(1);
        Choice updatedChoice = choiceS.findById(question1.getQuestionId());

        // Check Challenge update
        assertEquals(challenge1.getChallengeTitle(), updatedChallenge.getChallengeTitle());
        assertEquals(newChallengeIntro, updatedChallenge.getIntroduction());
        assertEquals(newChallengeCompletion, updatedChallenge.getCompletion());

        System.out.println(updatedChallenge);

        // Check choice update
        assertEquals(newChoiceText, updatedChoice.getChoiceText());
        assertEquals(newChoiceWeight, updatedChoice.getWeight());
    }

}

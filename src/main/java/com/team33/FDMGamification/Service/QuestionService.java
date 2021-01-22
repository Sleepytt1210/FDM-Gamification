package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.QuestionRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);
    @Autowired
    private QuestionRepository questionRepo;
    @Autowired
    private ChallengeService cls;
    @Autowired
    private ChoiceService chs;
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Insert and persist data into Question Table with properties and foreign key ID.
     *
     * @param challengeId   Foreign key id of challenge to be added to.
     * @param questionTitle Title of question.
     * @param questionText  Text of question.
     * @param completion    Number of completions of question.
     * @param questionType  Type of current question.
     * @param choices       Choices of question.
     * @return Question: Question entity persisted in database.
     */
    public Question create(Integer challengeId, String questionTitle, String questionText,
                           Integer completion, QuestionType questionType, List<Choice> choices) {
        Question question = new Question(questionTitle, questionText, completion, questionType);
        if(choices != null && !choices.isEmpty()){
            for (Choice choice : choices) {
                question.addChoice(choice);
            }
        }
        return create(challengeId, question);
    }

    /**
     * Insert and persist data into Question Table with Question entity and foreign key ID.
     *
     * @param challengeId Foreign key id of challenge to be added to.
     * @param question    Question entity with properties.
     * @return Question: Question entity persisted in database.
     */
    public Question create(Integer challengeId, Question question) {
        Challenge challenge = cls.findById(challengeId);
        return create(challenge, question);
    }

    /**
     * Insert and persist data into Question Table with Question entity and foreign entity.
     *
     * @param challenge Foreign entity challenge to be added to.
     * @param question  Question entity with properties.
     * @return Question: Question entity persisted in database.
     */
    public Question create(Challenge challenge, Question question) {
        challenge.addQuestion(question);
        return questionRepo.saveAndFlush(question);
    }

    /**
     * Find a question by its ID.
     *
     * @param questionId Id of question.
     * @return Question: Question entity if found.
     * @throws EntityNotFoundException: If question is not found.
     */
    public Question findById(Integer questionId) {
        return questionRepo.findById(questionId).orElseThrow(() -> new EntityNotFoundException("Question not found!"));
    }

    /**
     * Get all questions in database.
     *
     * @return List<Question>: All the questions in database.
     */
    public List<Question> getAll() {
        return questionRepo.findAll();
    }

    public List<Question> getQuestionsByType(String type) {
        return getQuestionsByType(QuestionType.valueOf(type));
    }

    public List<Question> getQuestionsByType(QuestionType type) {
        return questionRepo.queryQuestionsByQuestionTypeEquals(type);
    }

    /**
     * Return questions list of a challenge.
     *
     * @param challengeId Id of a challenge.
     * @return List<Question> questions: Map of questions with their id as key.
     */
    public List<Question> getQuestions(Integer challengeId){
        return questionRepo.getQuestionsByChallenge_Id(challengeId);
    }




    /**
     * Update existing question in database with properties.
     *
     * @param questionId    Id of question to be updated.
     * @param questionTitle New title of question.
     * @param questionText  New text of question.
     * @param completion    New completion count of question.
     * @return Question: Updated question entity.
     */
    public Question update(Integer questionId, String questionTitle, String questionText, Integer completion, QuestionType questionType) {
        if(questionId== null) throw new IllegalArgumentException("Use create for new entity!");
        Question question = findById(questionId);
        question.setQuestionTitle(questionTitle);
        question.setQuestionText(questionText);
        question.setQuestionCompletion(completion);
        question.setQuestionType(questionType);
        return questionRepo.saveAndFlush(question);
    }

    /**
     * Updated existing question in database with question entity.
     *
     * @param question Question entity with updated value.
     * @return Question: Updated question entity.
     */
    public Question update(Question question) {
        return update(question.getQuestionId(), question.getQuestionTitle(), question.getQuestionText(), question.getQuestionCompletion(), question.getQuestionType());
    }

    /**
     * Replace Challenge foreign key of question.
     *
     * @param newChallenge New challenge foreign key entity.
     * @param question     Question entity to be updated.
     * @return Question: Updated question entity.
     */
    @Transactional
    public Question updateChallenge(Challenge newChallenge, Question question) {
        question.setChallenge(newChallenge);
        newChallenge.getQuestions().add(question);
        questionRepo.replaceChallenge(newChallenge, question.getQuestionId());
        return questionRepo.saveAndFlush(question);
    }

    /**
     * Adds one to question's completion
     *
     * @param question Question entity to be updated.
     * @return Question: Updated challenge entity.
     */
    public Question completionIncrement(Question question){
        question.setQuestionCompletion(question.getQuestionCompletion() + 1);
        return questionRepo.saveAndFlush(question);
    }

    /**
     * Delete a question by its ID.
     *
     * @param questionId Id of question to be deleted.
     */
    public void delete(Integer questionId) {
        delete(findById(questionId));
    }

    /**
     * Delete a question by its entity.
     *
     * @param question Question entity to be deleted.
     */
    public void delete(Question question) {
        // To ensure bidirectional persistence in database
        question.getChallenge().removeQuestion(question);
        questionRepo.delete(question);
    }

    /**
     * Delete a collection of questions with entities.
     *
     * @param questions Collection of questions to be deleted.
     */
    public void batchDelete(Iterable<Question> questions) {
        // To ensure bidirectional persistence in database
        questions.forEach(q -> q.getChallenge().getQuestions().remove(q));
        questionRepo.deleteAll(questions);
    }
}

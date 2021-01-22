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
import javax.persistence.EntityNotFoundException;
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

    /**
     * Insert and persist data into Question Table with properties and foreign key ID.
     *
     * @param challengeId   Foreign key id of challenge to be added to.
     * @param questionTitle Title of question.
     * @param questionText  Text of question.
     * @param completion    Number of completions of question.
     * @param questionType  Type of current question.
     * @return Question: Question entity persisted in database.
     */
    public Question create(Integer challengeId, String questionTitle, String questionText, Integer completion, QuestionType questionType) {
        Question question = new Question(questionTitle, questionText, completion, questionType);
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
        question.setChallenge(challenge);
        if(question.getQuestionId() == null) {
            // Save questions without choices first due to empty question id.
            List<Choice> tempChoices = question.getChoices();
            question.setChoices(new ArrayList<>());
            question = questionRepo.saveAndFlush(question);
            if(tempChoices != null && tempChoices.size() > 0) chs.createAll(question, tempChoices);
            question = findById(question.getQuestionId());
        }else {
            question = questionRepo.saveAndFlush(question);
        }
        challenge.getQuestions().add(question);
        return question;
    }

    /**
     * Insert and persist a collection of questions into Question Table.
     *
     * @param challenge Foreign entity challenge to be added to.
     * @param questions A collection of question entities to be persisted in database.
     * @return List<Question>: A list of persisted question entities.
     */
    public List<Question> createAll(Challenge challenge, List<Question> questions){
        for(Question question : questions){
            create(challenge, question);
        }
        return challenge.getQuestions();
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
     * Return choices map of a question.
     *
     * @param id Id of the question.
     * @return List<Choice> choices: Map of choices with their id as key.
     */
    @Transactional
    public List<Choice> getChoices(Integer id){
        return findById(id).getChoices();
    }

    /**
     * Update existing question in database with properties.
     *
     * @param questionId    Id of question to be updated.
     * @param questionTitle New title of question.
     * @param questionText  New text of question.
     * @param completion    New completion count of question.
     * @param choices       New choices map of question.
     * @return Question: Updated question entity.
     */
    public Question update(Integer questionId, String questionTitle, String questionText, Integer completion, QuestionType questionType, List<Choice> choices) {
        Question tempNew = new Question(questionTitle, questionText, completion, questionType);
        tempNew.setChoices(choices);
        return update(questionId, tempNew);
    }

    /**
     * Updated existing question in database with question entity.
     *
     * @param questionId  Id of question to be updated.
     * @param newQuestion Question entity with updated value.
     * @return Question: Updated question entity.
     */
    public Question update(Integer questionId, Question newQuestion) {

        if(questionId == null) return create(newQuestion.getChallenge(), newQuestion);

        Question oldQuestion = findById(questionId);
        if (newQuestion.getQuestionTitle() != null) {
            oldQuestion.setQuestionTitle(newQuestion.getQuestionTitle());
        }
        if (newQuestion.getQuestionText() != null) {
            oldQuestion.setQuestionText(newQuestion.getQuestionText());
        }
        if (newQuestion.getQuestionCompletion() != null) {
            oldQuestion.setQuestionCompletion(newQuestion.getQuestionCompletion());
        }
        if (newQuestion.getQuestionType() != null) {
            oldQuestion.setQuestionType(newQuestion.getQuestionType());
        }
        if (newQuestion.getChallenge() != null &&
                !newQuestion.getChallenge().getId().equals(oldQuestion.getChallenge().getId())) {
            updateChallenge(cls.findById(newQuestion.getChallenge().getId()), oldQuestion);
        }

        oldQuestion = questionRepo.saveAndFlush(oldQuestion);
        List<Choice> newChoices = newQuestion.getChoices();
        if (newChoices != null && !newChoices.isEmpty()) {
            newChoices.forEach((v) -> chs.update(v.getChoiceId(), v.getChoiceText(), v.getChoiceWeight(), v.getChoiceReason(), questionId));
        }
        return oldQuestion;
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
        question.getChallenge().getQuestions().removeIf(question1 -> question1.getQuestionId().equals(question.getQuestionId()));
        questionRepo.delete(question);
    }

    /**
     * Delete a collection of questions with entities.
     *
     * @param questions Collection of questions to be deleted.
     */
    public void batchDelete(Iterable<Question> questions) {
        // To ensure bidirectional persistence in database
        questions.forEach(q -> q.getChallenge().getQuestions().removeIf(question1 -> question1.getQuestionId().equals(q.getQuestionId())));
        questionRepo.deleteAll(questions);
    }
}

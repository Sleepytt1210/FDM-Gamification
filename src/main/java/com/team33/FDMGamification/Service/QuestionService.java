package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.QuestionRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Choice;
import com.team33.FDMGamification.Model.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
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
     * @return Question: Question entity persisted in database.
     * @throws InstanceAlreadyExistsException If question already exists in challenge.
     */
    public Question create(Integer challengeId, String questionTitle, String questionText, Integer completion) throws InstanceAlreadyExistsException {
        Question question = new Question(questionTitle, questionText, completion);
        return create(challengeId, question);
    }

    /**
     * Insert and persist data into Question Table with Question entity and foreign key ID.
     *
     * @param challengeId Foreign key id of challenge to be added to.
     * @param question    Question entity with properties.
     * @return Question: Question entity persisted in database.
     * @throws InstanceAlreadyExistsException If question already exists in challenge.
     */
    public Question create(Integer challengeId, Question question) throws InstanceAlreadyExistsException {
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
        question = questionRepo.saveAndFlush(question);
        challenge.getQuestion().put(question.getQuestionId(), question);
        return question;
    }

    /**
     * Find a question by its ID.
     *
     * @param questionId Id of question.
     * @return Question: Question entity if found.
     * @throws EntityNotFoundException: If question is not found.
     */
    public Question findById(Integer questionId) {
        return questionRepo.findById(questionId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    /**
     * Get all questions in database.
     *
     * @return List<Question>: All the questions in database.
     */
    public List<Question> getAll() {
        return questionRepo.findAll();
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
    public Question update(Integer questionId, String questionTitle, String questionText, Integer completion, Map<Integer, Choice> choices) {
        Question tempNew = new Question(questionTitle, questionText, completion);
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
        Question oldQuestion = findById(questionId);
        if (newQuestion.getQuestionTitle() != null) oldQuestion.setQuestionTitle(newQuestion.getQuestionTitle());
        if (newQuestion.getQuestionText() != null) oldQuestion.setQuestionText(newQuestion.getQuestionText());
        if (newQuestion.getQuestionCompletion() != null)
            oldQuestion.setQuestionCompletion(newQuestion.getQuestionCompletion());
        oldQuestion = questionRepo.saveAndFlush(oldQuestion);
        Map<Integer, Choice> newChoices = newQuestion.getChoices();
        if (newChoices != null && !newChoices.isEmpty()) {
            newChoices.forEach((k, v) -> chs.update(k, v.getChoiceText(), v.getWeight()));
        }
        return oldQuestion;
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
        Challenge challenge = question.getChallenge();
        challenge.getQuestion().remove(question.getQuestionId());
        questionRepo.delete(question);
    }

    /**
     * Delete a collection of questions with entities.
     *
     * @param questions Collection of questions to be deleted.
     */
    public void batchDelete(Iterable<Question> questions) {
        for (Question q : questions) {
            Challenge challenge = q.getChallenge();
            challenge.getQuestion().remove(q.getQuestionId());
        }
        questionRepo.deleteAll(questions);
    }
}

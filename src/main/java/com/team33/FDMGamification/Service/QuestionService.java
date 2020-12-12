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

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ChallengeService cls;

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    public Question create(String questionText, Integer challengeId) throws InstanceAlreadyExistsException{
        Challenge challenge = cls.findById(challengeId);
        Question question = new Question(questionText);
        question.setChallenge(challenge);
        question = questionRepo.saveAndFlush(question);
        cls.addQuestion(challengeId, question);
        return question;
    }

    public Question create(Question question, Integer challengeId) throws InstanceAlreadyExistsException{
        Challenge challenge = cls.findById(challengeId);
        question.setChallenge(challenge);
        question = questionRepo.saveAndFlush(question);
        cls.addQuestion(challengeId, question);
        return question;
    }

    public List<Question> getAll(){
        return questionRepo.findAll();
    }

    public Question update(Integer questionId, String questionText) {
        Question question = findById(questionId);
        if(questionText != null) question.setQuestionText(questionText);
        return questionRepo.saveAndFlush(question);
    }

    public void delete(Integer questionId) {
        Question question = findById(questionId);
        Challenge challenge = question.getChallenge();
        challenge.getQuestion().remove(questionId);
        questionRepo.deleteById(questionId);
    }

    public void delete(Question question) {
        Challenge challenge = question.getChallenge();
        challenge.getQuestion().remove(question.getQuestionId());
        questionRepo.delete(question);
    }

    public void batchDelete(List<Question> questions) {
        for (Question q : questions) {
            Challenge challenge = q.getChallenge();
            challenge.getQuestion().remove(q.getQuestionId());
        }
        questionRepo.deleteAll(questions);
    }

    public Question findById(Integer questionId) {
        return questionRepo.findById(questionId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    public void addChoice(Integer questionId, Choice choice) throws InstanceAlreadyExistsException{
        Question question = findById(questionId);
        Map<Integer, Choice> choices = question.getChoices();
        if(choices.put(choice.getId(), choice) != null) {
            throw new InstanceAlreadyExistsException("The choice " + choice.getChoiceText() +" already exists in question " + question.getQuestionText() + "!");
        }
        questionRepo.saveAndFlush(question);
    }
}

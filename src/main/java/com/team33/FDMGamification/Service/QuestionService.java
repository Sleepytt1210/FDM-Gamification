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

    @Autowired
    private ChoiceService chs;

    private static final Logger log = LoggerFactory.getLogger(QuestionService.class);

    public Question create(Integer challengeId, String questionTitle, String questionText, Integer completion) throws InstanceAlreadyExistsException{
        Challenge challenge = cls.findById(challengeId);
        Question question = new Question(questionTitle, questionText, completion);
        question.setChallenge(challenge);
        question = questionRepo.saveAndFlush(question);
        cls.addQuestion(challengeId, question);
        return question;
    }

    public Question create(Integer challengeId, Question question) throws InstanceAlreadyExistsException{
        Challenge challenge = cls.findById(challengeId);
        question.setChallenge(challenge);
        question = questionRepo.saveAndFlush(question);
        cls.addQuestion(challengeId, question);
        return question;
    }

    public List<Question> getAll(){
        return questionRepo.findAll();
    }

    public Question update(Integer questionId, String questionTitle, String questionText, Integer completion) {
        Question question = findById(questionId);
        if(questionTitle != null) question.setQuestionTitle(questionTitle);
        if(questionText != null) question.setQuestionText(questionText);
        if(completion != null) question.setQuestionCompletion(completion);
        return questionRepo.saveAndFlush(question);
    }

    public Question update(Integer questionId, Question newQuestion) {
        Question oldQuestion = findById(questionId);
        if(newQuestion.getQuestionTitle() != null) oldQuestion.setQuestionTitle(newQuestion.getQuestionTitle());
        if(newQuestion.getQuestionText() != null) oldQuestion.setQuestionText(newQuestion.getQuestionText());
        if(newQuestion.getQuestionCompletion() != null) oldQuestion.setQuestionCompletion(newQuestion.getQuestionCompletion());
        oldQuestion = questionRepo.saveAndFlush(oldQuestion);
        Map<Integer, Choice> newChoices = newQuestion.getChoices();
        if(newChoices != null && !newChoices.isEmpty()){
            newChoices.forEach((k, v) -> chs.update(k, v.getChoiceText(), v.getWeight()));
        }
        return oldQuestion;
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

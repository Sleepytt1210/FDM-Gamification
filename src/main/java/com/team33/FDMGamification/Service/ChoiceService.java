package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChoiceRepository;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepo;

    @Autowired
    private QuestionService qts;

    private static final Logger log = LoggerFactory.getLogger(ChoiceService.class);

    public Choice create(String choiceText, Integer weight, Integer questionId) throws InstanceAlreadyExistsException {
        Question question = qts.findById(questionId);
        Choice choice = new Choice(choiceText, weight);
        choice.setQuestion(question);
        choice = choiceRepo.saveAndFlush(choice);
        qts.addChoices(questionId, choice);
        return choice;
    }

    public Choice create(Choice choice, Integer questionId) throws InstanceAlreadyExistsException {
        Question question = qts.findById(questionId);
        choice.setQuestion(question);
        choice = choiceRepo.saveAndFlush(choice);
        qts.addChoices(questionId, choice);
        return choice;
    }

    public List<Choice> getAll(){
        return choiceRepo.findAll();
    }

    public Choice update(Integer choiceId, String choiceText, Integer weight) {
        Choice choice = findById(choiceId);
        if(choiceText != null) choice.setChoiceText(choiceText);
        if(weight != null) choice.setWeight(weight);
        return choiceRepo.saveAndFlush(choice);
    }

    public void delete(Integer choiceId) {
        choiceRepo.deleteById(choiceId);
    }

    public void delete(Choice choice) {
        choiceRepo.delete(choice);
    }

    public void batchDelete(List<Choice> choices) {
        choiceRepo.deleteAll(choices);
    }

    public Choice findById(Integer choiceId) {
        return choiceRepo.findById(choiceId).orElseThrow(() -> new EntityNotFoundException("Choice not found!"));
    }

}

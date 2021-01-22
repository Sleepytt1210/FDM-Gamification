package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChoiceRepository;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.Choice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;

@Service
public class ChoiceService {

    @Autowired
    private ChoiceRepository choiceRepo;

    @Autowired
    private QuestionService qts;

    private static final Logger log = LoggerFactory.getLogger(ChoiceService.class);

    /**
     * Insert and persist data into Choice Table with properties and foreign key ID.
     * @param questionId Foreign key id of question to be added to.
     * @param choiceText Text of choice.
     * @param weight Score weight of choice.
     * @param choiceReason Reason of the weight of this choice.
     * @return Choice: Choice entity persisted in database.
     */
    public Choice create(Integer questionId, String choiceText, Integer weight, String choiceReason) {
        return create(questionId, new Choice(choiceText, weight, choiceReason));
    }

    /**
     * Insert and persist data in Choice Table with Choice entity and foreign key ID.
     * @param questionId Foreign key id of question to be added to.
     * @param choice Choice entity with properties.
     * @return Choice: Choice entity persisted in database.
     */
    public Choice create(Integer questionId, Choice choice) {
        Question question = qts.findById(questionId);
        return create(question, choice);
    }

    /**
     * Insert and persist data in Choice Table with Choice entity and foreign entity.
     * @param question Foreign entity question to be added to.
     * @param choice Choice entity with properties.
     * @return Choice: Choice entity persisted in database.
     */
    public Choice create(Question question, Choice choice) {
        question.addChoice(choice);
        return choiceRepo.saveAndFlush(choice);
    }

    /**
     * Find a choice by its ID.
     * @param choiceId Id of choice.
     * @return Choice: Choice entity if found.
     * @throws EntityNotFoundException: If choice is not found.
     */
    public Choice findById(Integer choiceId) {
        return choiceRepo.findById(choiceId).orElseThrow(() -> new EntityNotFoundException("Choice not found!"));
    }

    /**
     * Get all choices in the database.
     * @return List<Choice>: All choices in the database.
     */
    public List<Choice> getAll(){
        return choiceRepo.findAll();
    }

    /**
     * Return choices map of a question.
     *
     * @param questionId Id of the question.
     * @return List<Choice> choices: Map of choices with their id as key.
     */
    public List<Choice> getChoices(Integer questionId){
        return choiceRepo.getChoicesByQuestion_QuestionId(questionId);
    }

    /**
     * Update existing choice in database with properties.
     * @param choiceId Id of choice to be updated.
     * @param choiceText New text of choice.
     * @param weight New weight of choice.
     * @return Choice: Updated choice entity.
     */
    public Choice update(Integer choiceId, String choiceText, Integer weight, String choiceReason) {
        Choice choice = findById(choiceId);
        choice.setChoiceText(choiceText);
        choice.setChoiceWeight(weight);
        choice.setChoiceReason(choiceReason);
        return choiceRepo.saveAndFlush(choice);
    }

    /**
     * Replace question foreign key of choice entity.
     *
     * @param newQuestion New Question foreign key entity.
     * @param choice      Choice entity to be updated.
     * @return Choice: Updated choice entity.
     */
    @Transactional
    public Choice updateQuestion(Question newQuestion, Choice choice) {
        choice.setQuestion(newQuestion);
        newQuestion.getChoices().add(choice);
        choiceRepo.replaceQuestion(newQuestion, choice.getChoiceId());
        return choiceRepo.saveAndFlush(choice);
    }

    /**
     * Delete a choice by its id.
     * @param choiceId Id of choice to be deleted.
     */
    public void delete(Integer choiceId) {
        delete(findById(choiceId));
    }

    /**
     * Delete a choice by its entity.
     * @param choice Choice entity to be deleted.
     */
    public void delete(Choice choice) {
        // To ensure bidirectional persistence in database
        choice.getQuestion().getChoices().removeIf(choice1 -> choice1.getChoiceId().equals(choice.getChoiceId()));
        choiceRepo.delete(choice);
    }

    /**
     * Delete a collection of choices with entities.
     * @param choices Collection of choices to be deleted.
     */
    public void batchDelete(Iterable<Choice> choices) {
        // To ensure bidirectional persistence in database
        choices.forEach(c -> c.getQuestion().getChoices().removeIf(choice1 -> choice1.getChoiceId().equals(c.getChoiceId())));
        choiceRepo.deleteAll(choices);
    }

}

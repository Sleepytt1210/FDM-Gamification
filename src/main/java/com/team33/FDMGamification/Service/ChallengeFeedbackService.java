package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChallengeFeedbackRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.ChallengeFeedback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ChallengeFeedbackService {

    @Autowired
    private ChallengeFeedbackRepository challengeFeedbackRepo;

    @Autowired
    private ChallengeService cls;

    private static final Logger log = LoggerFactory.getLogger(ChallengeFeedbackService.class);

    /**
     * Insert and persist data in ChallengeFeedback Table with properties and foreign key ID.
     * @param challengeId Foreign key id of challenge to be added to.
     * @param challengeFeedbackTitle Title of challenge feedback.
     * @param challengeFeedbackText Text of challenge feedback.
     * @param positive Is the feedback positive?
     * @return ChallengeFeedback: ChallengeFeedback entity persisted in database.
     */
    public ChallengeFeedback create(Integer challengeId, String challengeFeedbackTitle, String challengeFeedbackText, boolean positive) throws InstanceAlreadyExistsException {
        return create(challengeId, new ChallengeFeedback(challengeFeedbackTitle, challengeFeedbackText, positive));
    }

    /**
     * Insert and persist data in ChallengeFeedback Table with ChallengeFeedback entity and foreign key ID.
     * @param challengeId Foreign key id of challenge to be added to.
     * @param challengeFeedback ChallengeFeedback entity with properties.
     * @return ChallengeFeedback: ChallengeFeedback entity persisted in database.
     */
    public ChallengeFeedback create(Integer challengeId, ChallengeFeedback challengeFeedback) {
        Challenge challenge = cls.findById(challengeId);
        return create(challenge, challengeFeedback);
    }

    /**
     * Insert and persist data in ChallengeFeedback Table with ChallengeFeedback entity and foreign entity.
     * @param challenge Foreign entity challenge to be added to.
     * @param challengeFeedback ChallengeFeedback entity with properties.
     * @return ChallengeFeedback: ChallengeFeedback entity persisted in database.
     */
    public ChallengeFeedback create(Challenge challenge, ChallengeFeedback challengeFeedback) {
        challengeFeedback.setChallenge(challenge);
        challengeFeedback = challengeFeedbackRepo.saveAndFlush(challengeFeedback);
        challenge.getChallengeFeedback().put(challengeFeedback.isPositive(), challengeFeedback);
        return challengeFeedback;
    }

    /**
     * Find a challenge feedback by its ID.
     * @param challengeFeedbackId Id of challenge feedback.
     * @return ChallengeFeedback: Challenge feedback entity if found.
     * @throws EntityNotFoundException: If challenge feedback is not found.
     */
    public ChallengeFeedback findById(Integer challengeFeedbackId) {
        return challengeFeedbackRepo.findById(challengeFeedbackId).orElseThrow(() -> new EntityNotFoundException("ChallengeFeedback not found!"));
    }

    /**
     * Find a challenge feedback by its positivity and its foreign key id.
     * @param challengeId Foreign key id of challenge to be queried.
     * @param positive Positivity of the feedback.
     * @return ChallengeFeedback: Challenge feedback entity if found.
     * @throws EntityNotFoundException: If challenge feedback is not found.
     */
    public ChallengeFeedback findByPositive(Integer challengeId, boolean positive) {
        ChallengeFeedback feedback = challengeFeedbackRepo.findByChallengeIdAndChallengeFeedbackPositive(challengeId, positive);
        if (feedback == null) throw new EntityNotFoundException("ChallengeFeedback not found!");
        return feedback;
    }

    /**
     * Get all challenge feedbacks in the database.
     * @return List<ChallengeFeedback>: All challenge feedbacks in the database.
     */
    public List<ChallengeFeedback> getAll(){
        return challengeFeedbackRepo.findAll();
    }

    /**
     * Update existing challenge feedback with properties.
     * @param challengeFeedbackId Id of challenge feedback to be updated.
     * @param challengeFeedbackTitle New title of challenge feedback.
     * @param challengeFeedbackText New text of challenge feedback.
     * @return ChallengeFeedback: Updated challenge feedback.
     */
    public ChallengeFeedback update(Integer challengeFeedbackId, String challengeFeedbackTitle, String challengeFeedbackText) {
        ChallengeFeedback challengeFeedback = findById(challengeFeedbackId);
        if(challengeFeedbackText != null) challengeFeedback.setFeedback_text(challengeFeedbackText);
        if(challengeFeedbackTitle != null) challengeFeedback.setFeedback_title(challengeFeedbackTitle);
        return challengeFeedbackRepo.saveAndFlush(challengeFeedback);
    }

    /**
     * Delete challenge feedback by its ID.
     * @param challengeFeedbackId Id of challenge feedback to be deleted.
     */
    public void delete(Integer challengeFeedbackId) {
        delete(findById(challengeFeedbackId));
    }

    /**
     * Delete challenge feedback by its entity.
     * @param challengeFeedback Challenge feedback entity to be deleted.
     */
    public void delete(ChallengeFeedback challengeFeedback) {
        Challenge challenge = challengeFeedback.getChallenge();
        challenge.getChallengeFeedback().remove(challengeFeedback.isPositive());
        challengeFeedbackRepo.delete(challengeFeedback);
    }

    /**
     * Delete a collection of challenge feedbacks by entities.
     * @param challengeFeedbacks Collection of challenge feedbacks to be deleted.
     */
    public void batchDelete(Iterable<ChallengeFeedback> challengeFeedbacks) {
        for(ChallengeFeedback challengeFeedback : challengeFeedbacks) {
            Challenge challenge = challengeFeedback.getChallenge();
            challenge.getChallengeFeedback().remove(challengeFeedback.isPositive());
        }
        challengeFeedbackRepo.deleteAll(challengeFeedbacks);
    }

}

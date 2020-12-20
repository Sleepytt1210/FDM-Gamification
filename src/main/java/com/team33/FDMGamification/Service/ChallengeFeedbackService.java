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
    private ChallengeService chs;

    private static final Logger log = LoggerFactory.getLogger(ChallengeFeedbackService.class);

    public ChallengeFeedback create(Integer challengeId, String challengeFeedbackTitle, String challengeFeedbackText, boolean positive) throws InstanceAlreadyExistsException {
        Challenge challenge = chs.findById(challengeId);
        ChallengeFeedback challengeFeedback = new ChallengeFeedback(challengeFeedbackTitle, challengeFeedbackText, positive);
        challengeFeedback.setChallenge(challenge);
        challengeFeedback = challengeFeedbackRepo.saveAndFlush(challengeFeedback);
        chs.addChallengeFeedback(challengeId, challengeFeedback);
        return challengeFeedback;
    }

    public ChallengeFeedback create(Integer challengeId, ChallengeFeedback challengeFeedback) throws InstanceAlreadyExistsException {
        Challenge challenge = chs.findById(challengeId);
        challengeFeedback.setChallenge(challenge);
        challengeFeedback = challengeFeedbackRepo.saveAndFlush(challengeFeedback);
        chs.addChallengeFeedback(challengeId, challengeFeedback);
        return challengeFeedback;
    }

    public List<ChallengeFeedback> getAll(){
        return challengeFeedbackRepo.findAll();
    }


    public ChallengeFeedback update(Integer challengeFeedbackId, String challengeFeedbackTitle, String challengeFeedbackText) {
        ChallengeFeedback challengeFeedback = findById(challengeFeedbackId);
        if(challengeFeedbackText != null) challengeFeedback.setFeedback_text(challengeFeedbackText);
        if(challengeFeedbackTitle != null) challengeFeedback.setFeedback_title(challengeFeedbackTitle);
        return challengeFeedbackRepo.saveAndFlush(challengeFeedback);
    }

    public void delete(Integer challengeFeedbackId) {
        ChallengeFeedback challengeFeedback = findById(challengeFeedbackId);
        Challenge challenge = challengeFeedback.getChallenge();
        challenge.getChallengeFeedback().remove(challengeFeedback.isPositive());
        challengeFeedbackRepo.deleteById(challengeFeedbackId);
    }

    public void delete(ChallengeFeedback challengeFeedback) {
        Challenge challenge = challengeFeedback.getChallenge();
        challenge.getChallengeFeedback().remove(challengeFeedback.isPositive());
        challengeFeedbackRepo.delete(challengeFeedback);
    }

    public void batchDelete(List<ChallengeFeedback> challengeFeedbacks) {
        for(ChallengeFeedback challengeFeedback : challengeFeedbacks) {
            Challenge challenge = challengeFeedback.getChallenge();
            challenge.getChallengeFeedback().remove(challengeFeedback.isPositive());
        }
        challengeFeedbackRepo.deleteAll(challengeFeedbacks);
    }

    public ChallengeFeedback findById(Integer challengeFeedbackId) {
        return challengeFeedbackRepo.findById(challengeFeedbackId).orElseThrow(() -> new EntityNotFoundException("ChallengeFeedback not found!"));
    }

    public ChallengeFeedback findByPositive(Integer challengeId, boolean positive) {
        ChallengeFeedback feedback = challengeFeedbackRepo.findByChallengeIdAndChallengeFeedbackPositive(challengeId, positive);
        if (feedback == null) throw new EntityNotFoundException("ChallengeFeedback not found!");
        return feedback;
    }

}

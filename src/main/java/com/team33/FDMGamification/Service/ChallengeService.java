package com.team33.FDMGamification.Service;

import com.sun.istack.NotNull;
import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Question;
import org.hibernate.annotations.NotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepo;

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    public Challenge create(String introduction, Integer completion) {
        Challenge challenge = new Challenge(introduction, completion);
        challengeRepo.saveAndFlush(challenge);
        return challenge;
    }

    public List<Challenge> getAll() {
        return challengeRepo.findAll();
    }

    public Challenge update(Integer challengeId, String introduction, Integer completion) {
        Challenge challenge = findById(challengeId);
        if(introduction != null) challenge.setIntroduction(introduction);
        if(completion != null) challenge.setCompletion(completion);
        return challengeRepo.saveAndFlush(challenge);
    }

    public void delete(Integer challengeId) {
        challengeRepo.deleteById(challengeId);
    }

    public void delete(Challenge challenge) {
        challengeRepo.delete(challenge);
    }

    public void batchDelete(List<Challenge> challenges) {
        challengeRepo.deleteInBatch(challenges);
    }

    public Challenge findById(Integer challengeId) {
        return challengeRepo.findById(challengeId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    public void addQuestion(Integer challengeId, Question question) throws InstanceAlreadyExistsException, RuntimeException {
        Challenge challenge = findById(challengeId);
        if(challenge.getQuestion().put(question.getQuestionId(), question) != null){
            throw new InstanceAlreadyExistsException("The question " + question.getQuestionId() + " already exists in challenge " + challengeId + '!');
        }
        challengeRepo.saveAndFlush(challenge);
    }
}

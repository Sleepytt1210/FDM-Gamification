package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.ChallengeFeedback;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChallengeService {

    @Autowired
    private ChallengeRepository challengeRepo;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private ChallengeFeedbackService challengeFeedbackService;

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    public Challenge create(String title, String introduction, String thumbnail, Integer completion) {
        Challenge challenge = new Challenge(title, introduction, thumbnail, completion);
        challengeRepo.saveAndFlush(challenge);
        return challenge;
    }

    public Challenge create(Challenge challenge) {
        return challengeRepo.saveAndFlush(challenge);
    }

    public List<Challenge> getAll() {
        return challengeRepo.findAll();
    }

    public Challenge update(Integer challengeId, String title, String introduction, String thumbnail, Integer completion, Map<Integer, Question> questions, Map<Boolean, ChallengeFeedback> feedbacks, Set<Rating> ratings){
        Challenge tempNew = new Challenge(title, introduction, thumbnail, completion);
        tempNew.setQuestion(questions);
        tempNew.setChallengeFeedback(feedbacks);
        tempNew.setRatings(ratings);
        return update(challengeId, tempNew);
    }

    public Challenge update(Integer challengeId, Challenge newChallenge) {
        Challenge oldChallenge = findById(challengeId);
        if(newChallenge.getChallengeTitle() != null) oldChallenge.setChallengeTitle(newChallenge.getChallengeTitle());
        if(newChallenge.getIntroduction() != null) oldChallenge.setIntroduction(newChallenge.getIntroduction());
        if(newChallenge.getThumbnail() != null) oldChallenge.setThumbnail(newChallenge.getThumbnail());
        if(newChallenge.getCompletion() != null) oldChallenge.setCompletion(newChallenge.getCompletion());
        oldChallenge = challengeRepo.saveAndFlush(oldChallenge);
        Map<Integer, Question> newQuestions = newChallenge.getQuestion();
        Map<Boolean, ChallengeFeedback> newFeedback = newChallenge.getChallengeFeedback();
        if(newQuestions != null && !newQuestions.isEmpty()){
            newQuestions.forEach((k, v) -> questionService.update(k, v));
        }
        if(newFeedback != null && !newFeedback.isEmpty()) {
            newFeedback.forEach((k, v) -> challengeFeedbackService.update(v.getFeedback_id(), v.getFeedback_title(), v.getFeedback_text()));
        }
        return oldChallenge;
    }

    public void delete(Integer challengeId) {
        challengeRepo.deleteById(challengeId);
    }

    public void delete(Challenge challenge) {
        challengeRepo.delete(challenge);
    }

    public void batchDelete(List<Challenge> challenges) {
        challengeRepo.deleteAll(challenges);
    }

    public Challenge findById(Integer challengeId) {
        return challengeRepo.findById(challengeId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    public void addQuestion(Challenge challenge, Question question) throws InstanceAlreadyExistsException {
        if(challenge.getQuestion().put(question.getQuestionId(), question) != null){
            throw new InstanceAlreadyExistsException("The question " + question.getQuestionId() + " already exists in challenge " + challenge.getId() + '!');
        }
        challengeRepo.saveAndFlush(challenge);
    }

    public void addChallengeFeedback(Challenge challenge, ChallengeFeedback challengeFeedback) throws InstanceAlreadyExistsException {
        if(challenge.getChallengeFeedback().put(challengeFeedback.isPositive(), challengeFeedback) != null){
            throw new InstanceAlreadyExistsException("The feedback " + challengeFeedback.getFeedback_id() + " already exists in challenge " + challenge.getId() + '!');
        }
        challengeRepo.saveAndFlush(challenge);
    }

    public void addRating(Challenge challenge, Rating rating) throws InstanceAlreadyExistsException {
        if(!challenge.getRatings().add(rating)){
            throw new InstanceAlreadyExistsException("The rating " + rating.getRating_id() + " already exists in challenge " + challenge.getId() + '!');
        }
        challenge.setAvgRating(average(challenge.getRatings()));
        challengeRepo.saveAndFlush(challenge);
    }

    private String average(Set<Rating> ratings) {
        int avg = 0;
        for (Rating cur : ratings) {
            avg += cur.getRating_value();
        }
        return String.format("%.1f", ((avg*1.0)/(ratings.size()*1.0)));
    }
}

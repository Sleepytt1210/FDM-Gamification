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

    /**
     * Insert and persist data into Challenge Table with properties.
     * @param title Title of challenge.
     * @param introduction Introduction text of challenge.
     * @param thumbnail URL of thumbnail for the challenge.
     * @param completion Number of completion.
     * @return Challenge: Challenge object persisted in database.
     */
    public Challenge create(String title, String introduction, String thumbnail, Integer completion) {
        Challenge challenge = new Challenge(title, introduction, thumbnail, completion);
        return create(challenge);
    }

    /**
     * Insert and persist data into Challenge Table with a Challenge object.
     * @param challenge Challenge object with properties.
     * @return Challenge: Challenge object persisted in database.
     */
    public Challenge create(Challenge challenge) {
        return challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Find a challenge by its ID.
     * @param challengeId Id of challenge.
     * @return Challenge: Challenge object if found.
     * @throws EntityNotFoundException: If challenge is not found.
     */
    public Challenge findById(Integer challengeId) {
        return challengeRepo.findById(challengeId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    /**
     * Get all challenges in the database.
     * @return List<Challenge>: All the challenge in the database.
     */
    public List<Challenge> getAll() {
        return challengeRepo.findAll();
    }

    /**
     * Update existing challenge in database with properties.
     * @param challengeId Id of challenge to be updated.
     * @param title New title of challenge.
     * @param introduction New introduction of challenge.
     * @param thumbnail New thumbnail url of challenge.
     * @param completion New completion count of challenge.
     * @param questions New questions map of challenge.
     * @param feedbacks New feedback map of challenge.
     * @param ratings New ratings set of challenge.
     * @return Challenge: Updated challenge object.
     */
    public Challenge update(Integer challengeId, String title, String introduction, String thumbnail, Integer completion, Map<Integer, Question> questions, Map<Boolean, ChallengeFeedback> feedbacks, Set<Rating> ratings){
        Challenge tempNew = new Challenge(title, introduction, thumbnail, completion);
        tempNew.setQuestion(questions);
        tempNew.setChallengeFeedback(feedbacks);
        tempNew.setRatings(ratings);
        return update(challengeId, tempNew);
    }

    /**
     * Update existing challenge in database with a challenge object.
     * @param challengeId Id of the challenge to be updated.
     * @param newChallenge Challenge object with updated properties.
     * @return Challenge: Updated challenge object.
     */
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

    /**
     * Delete a challenge by its ID.
     * @param challengeId Id of challenge to be deleted.
     */
    public void delete(Integer challengeId) {
        challengeRepo.deleteById(challengeId);
    }

    /**
     * Delete a challenge by its entity.
     * @param challenge Challenge object to be deleted.
     */
    public void delete(Challenge challenge) {
        challengeRepo.delete(challenge);
    }

    /**
     * Delete a collection of challenges with entities.
     * @param challenges Collection of challenges to be deleted.
     */
    public void batchDelete(Iterable<Challenge> challenges) {
        challengeRepo.deleteAll(challenges);
    }

    /**
     * Add question to a challenge object and persist the relationship in database.
     * @param challenge Challenge object to add question.
     * @param question Question object to be added.
     * @throws InstanceAlreadyExistsException If question already exists in the challenge.
     */
    public void addQuestion(Challenge challenge, Question question) throws InstanceAlreadyExistsException {
        if(challenge.getQuestion().put(question.getQuestionId(), question) != null){
            throw new InstanceAlreadyExistsException("The question " + question.getQuestionId() + " already exists in challenge " + challenge.getId() + '!');
        }
        challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Add challenge feedback to a challenge object and persist the relationship in database.
     * @param challenge Challenge object to add question.
     * @param challengeFeedback ChallengeFeedback object to be added.
     * @throws InstanceAlreadyExistsException If challengeFeedback already exists in the challenge.
     */
    public void addChallengeFeedback(Challenge challenge, ChallengeFeedback challengeFeedback) throws InstanceAlreadyExistsException {
        if(challenge.getChallengeFeedback().put(challengeFeedback.isPositive(), challengeFeedback) != null){
            throw new InstanceAlreadyExistsException("The feedback " + challengeFeedback.getFeedback_id() + " already exists in challenge " + challenge.getId() + '!');
        }
        challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Add rating to a challenge object and persist the relationship in database.
     * @param challenge Challenge object to add question.
     * @param rating Rating object to be added.
     * @throws InstanceAlreadyExistsException If rating already exists in the challenge.
     */
    public void addRating(Challenge challenge, Rating rating) throws InstanceAlreadyExistsException {
        if(!challenge.getRatings().add(rating)){
            throw new InstanceAlreadyExistsException("The rating " + rating.getRating_id() + " already exists in challenge " + challenge.getId() + '!');
        }
        challenge.setAvgRating(average(challenge.getRatings()));
        challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Calculate the average value of ratings.
     * @param ratings Set of ratings to be calculated.
     * @return String: Average value of ratings with one decimal point.
     */
    private String average(Set<Rating> ratings) {
        int avg = 0;
        for (Rating cur : ratings) {
            avg += cur.getRating_value();
        }
        return String.format("%.1f", ((avg*1.0)/(ratings.size()*1.0)));
    }
}

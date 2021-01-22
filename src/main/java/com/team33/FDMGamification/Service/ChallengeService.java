package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;

@Service
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);
    @Autowired
    private ChallengeRepository challengeRepo;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ChallengeFeedbackService challengeFeedbackService;
    @Autowired
    private ThumbnailService thumbnailService;
    @Autowired
    private RatingService ratingService;

    /**
     * Insert and persist data into Challenge Table with properties.
     *
     * @param title        Title of challenge.
     * @param introduction Introduction text of challenge.
     * @param stream       Stream of the challenge.
     * @param completion   Number of completion.
     * @return Challenge: Challenge entity persisted in database.
     */
    public Challenge create(String title, String introduction, Stream stream, Integer completion) {
        Challenge challenge = new Challenge(title, introduction, stream, completion);
        return create(challenge);
    }

    /**
     * Insert and persist data into Challenge Table with a Challenge entity.
     *
     * @param challenge Challenge entity with properties.
     * @return Challenge: Challenge entity persisted in database.
     */
    public Challenge create(Challenge challenge) {

        // Create a temporary challenge with empty relational entities
        Challenge tempNew = new Challenge(challenge.getChallengeTitle(), challenge.getDescription(), challenge.getStream(), challenge.getCompletion());
        tempNew.setThumbnail(null);

        List<Question> tempQuestions = null;
        Map<Boolean, ChallengeFeedback> tempFeedbacks = null;
        Set<Rating> ratings = null;
        Thumbnail tempThumbnail = null;

        // Temp store relational entities
        if(challenge.getQuestions() != null && !challenge.getQuestions().isEmpty()) tempQuestions = challenge.getQuestions();
        if(challenge.getChallengeFeedback() != null && !challenge.getChallengeFeedback().isEmpty()) tempFeedbacks = challenge.getChallengeFeedback();
        if(challenge.getRatings() != null && !challenge.getRatings().isEmpty()) ratings = challenge.getRatings();
        if(challenge.getThumbnail() != null) tempThumbnail = challenge.getThumbnail();

        challenge = challengeRepo.saveAndFlush(tempNew);

        // Update relational entities
        if(tempThumbnail != null) thumbnailService.create(challenge, tempThumbnail);
        if(tempQuestions != null && !tempQuestions.isEmpty()) questionService.createAll(challenge, tempQuestions);
        if(tempFeedbacks != null && !tempFeedbacks.isEmpty()) challengeFeedbackService.createBoth(challenge, tempFeedbacks);
        if(ratings != null && ratings.isEmpty()) ratingService.createAll(challenge, ratings);

        return findById(challenge.getId());
    }

    /**
     * Find a challenge by its ID.
     *
     * @param challengeId Id of challenge.
     * @return Challenge: Challenge entity if found.
     * @throws EntityNotFoundException: If challenge is not found.
     */
    public Challenge findById(Integer challengeId) {
        return challengeRepo.findById(challengeId).orElseThrow(() -> new EntityNotFoundException("Challenge not found!"));
    }

    /**
     * Find a list of challenges by its stream.
     *
     * @param stream Stream of challenge (i.e "BI", "ST", "TO")
     * @return List<Challenge>: List of challenges of given stream.
     */
    public List<Challenge> findByStream(String stream) {
        return findByStream(Stream.valueOf(stream));
    }

    /**
     * Find a list of challenges by its stream.
     *
     * @param stream Stream of challenge (i.e Stream.BI, Stream.ST, Stream.TO)
     * @return List<Challenge>: List of challenges of given stream.
     */
    public List<Challenge> findByStream(Stream stream) {
        return challengeRepo.findChallengesByStreamEquals(stream);
    }

    /**
     * Get all challenges in the database.
     *
     * @return List<Challenge>: All the challenge in the database.
     */
    public List<Challenge> getAll() {
        return challengeRepo.findAll();
    }

    /**
     * Return questions map of a challenge.
     *
     * @param id Id of the challenge.
     * @return List<Question> questions: Map of questions with their id as key.
     */
    @Transactional
    public List<Question> getQuestions(Integer id){
        return findById(id).getQuestions();
    }

    /**
     * Update existing challenge in database with properties.
     *
     * @param challengeId  Id of challenge to be updated.
     * @param title        New title of challenge.
     * @param introduction New introduction of challenge.
     * @param thumbnail    New thumbnail url of challenge.
     * @param stream       New stream of challenge.
     * @param completion   New completion count of challenge.
     * @param questions    New questions map of challenge.
     * @param feedbacks    New feedback map of challenge.
     * @param ratings      New ratings set of challenge.
     * @return Challenge: Updated challenge entity.
     */
    public Challenge update(Integer challengeId, String title, String introduction, Thumbnail thumbnail,
                            Stream stream, Integer completion, List<Question> questions,
                            Map<Boolean, ChallengeFeedback> feedbacks, Set<Rating> ratings) {
        Challenge tempNew = new Challenge(title, introduction, stream, completion);
        tempNew.setQuestions(questions);
        tempNew.setChallengeFeedback(feedbacks);
        tempNew.setRatings(ratings);
        tempNew.setThumbnail(thumbnail);
        return update(challengeId, tempNew);
    }

    /**
     * Update existing challenge in database with a challenge entity.
     *
     * @param challengeId  Id of the challenge to be updated.
     * @param newChallenge Challenge entity with updated properties.
     * @return Challenge: Updated challenge entity.
     */
    public Challenge update(Integer challengeId, Challenge newChallenge) {
        Challenge oldChallenge = findById(challengeId);

        // Null checks before update
        if (newChallenge.getChallengeTitle() != null) oldChallenge.setChallengeTitle(newChallenge.getChallengeTitle());
        if (newChallenge.getDescription() != null) oldChallenge.setDescription(newChallenge.getDescription());
        if (newChallenge.getStream() != null) oldChallenge.setStream(newChallenge.getStream());
        if (newChallenge.getCompletion() != null) oldChallenge.setCompletion(newChallenge.getCompletion());
        if (newChallenge.getThumbnail() != null && newChallenge.getThumbnail().getId() != null)
            thumbnailService.update(newChallenge.getThumbnail().getId(), newChallenge.getThumbnail());

        oldChallenge = challengeRepo.saveAndFlush(oldChallenge);
        List<Question> newQuestions = newChallenge.getQuestions();
        Map<Boolean, ChallengeFeedback> newFeedback = newChallenge.getChallengeFeedback();

        if (newQuestions != null && !newQuestions.isEmpty()) {
            Challenge finalOldChallenge = oldChallenge;
            newQuestions.forEach((v) -> {
                if(v.getQuestionId() == null) v.setChallenge(finalOldChallenge);
                questionService.update(v.getQuestionId(), v);
            });
        }
        if (newFeedback != null && !newFeedback.isEmpty()) {
            newFeedback.forEach((k, v) -> challengeFeedbackService.update(v.getFeedback_id(), v.getFeedback_title(), v.getFeedback_text()));
        }
        return oldChallenge;
    }

    /**
     * Adds one to challenge's completion
     *
     * @param challenge Challenge entity to be updated.
     * @return Challenge: Updated challenge entity.
     */
    public Challenge completionIncrement(Challenge challenge){
        challenge.setCompletion(challenge.getCompletion() + 1);
        return challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Delete a challenge by its ID.
     *
     * @param challengeId Id of challenge to be deleted.
     */
    public void delete(Integer challengeId) {
        challengeRepo.deleteById(challengeId);
    }

    /**
     * Delete a challenge by its entity.
     *
     * @param challenge Challenge entity to be deleted.
     */
    public void delete(Challenge challenge) {
        challengeRepo.delete(challenge);
    }

    /**
     * Delete a collection of challenges with entities.
     *
     * @param challenges Collection of challenges to be deleted.
     */
    public void batchDelete(Iterable<Challenge> challenges) {
        challengeRepo.deleteAll(challenges);
    }

    /**
     * Add rating to a challenge entity and persist the relationship in database.
     *
     * @param challenge Challenge entity to add question.
     * @param rating    Rating entity to be added.
     */
    public void addRating(Challenge challenge, Rating rating) {
        challenge.getRatings().add(rating);
        challenge.setAvgRating(average(challenge.getRatings()));
        challengeRepo.saveAndFlush(challenge);
    }

    /**
     * Calculate the average value of ratings.
     *
     * @param ratings Set of ratings to be calculated.
     * @return String: Average value of ratings with one decimal point.
     */
    private String average(Set<Rating> ratings) {
        int avg = 0;
        for (Rating cur : ratings) {
            avg += cur.getRatingValue();
        }
        return String.format("%.1f", ((avg * 1.0) / (ratings.size() * 1.0)));
    }
}

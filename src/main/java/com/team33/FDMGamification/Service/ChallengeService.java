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

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);
    @Autowired
    private ChallengeRepository challengeRepo;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private ChallengeFeedbackService challengeFeedbackService;

    /**
     * Insert and persist data into Challenge Table with properties.
     *
     * @param title        Title of challenge.
     * @param introduction Introduction text of challenge.
     * @param thumbnail    URL of thumbnail for the challenge.
     * @param stream       Stream of the challenge.
     * @param completion   Number of completion.
     * @return Challenge: Challenge entity persisted in database.
     */
    public Challenge create(String title, String introduction, String thumbnail, String stream, Integer completion) {
        Challenge challenge = new Challenge(title, introduction, thumbnail, stream, completion);
        return create(challenge);
    }

    /**
     * Insert and persist data into Challenge Table with a Challenge entity.
     *
     * @param challenge Challenge entity with properties.
     * @return Challenge: Challenge entity persisted in database.
     */
    public Challenge create(Challenge challenge) {
        return challengeRepo.saveAndFlush(challenge);
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
    public Challenge update(Integer challengeId, String title, String introduction, String thumbnail,
                            String stream, Integer completion, Map<Integer, Question> questions,
                            Map<Boolean, ChallengeFeedback> feedbacks, Set<Rating> ratings) {
        Challenge tempNew = new Challenge(title, introduction, thumbnail, stream, completion);
        tempNew.setQuestion(questions);
        tempNew.setChallengeFeedback(feedbacks);
        tempNew.setRatings(ratings);
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
        if (newChallenge.getChallengeTitle() != null) oldChallenge.setChallengeTitle(newChallenge.getChallengeTitle());
        if (newChallenge.getIntroduction() != null) oldChallenge.setIntroduction(newChallenge.getIntroduction());
        if (newChallenge.getThumbnail() != null) oldChallenge.setThumbnail(newChallenge.getThumbnail());
        if (newChallenge.getStream() != null) oldChallenge.setStream(newChallenge.getStream());
        if (newChallenge.getCompletion() != null) oldChallenge.setCompletion(newChallenge.getCompletion());
        oldChallenge = challengeRepo.saveAndFlush(oldChallenge);
        Map<Integer, Question> newQuestions = newChallenge.getQuestion();
        Map<Boolean, ChallengeFeedback> newFeedback = newChallenge.getChallengeFeedback();
        if (newQuestions != null && !newQuestions.isEmpty()) {
            newQuestions.forEach((k, v) -> questionService.update(k, v));
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
            avg += cur.getRating_value();
        }
        return String.format("%.1f", ((avg * 1.0) / (ratings.size() * 1.0)));
    }
}

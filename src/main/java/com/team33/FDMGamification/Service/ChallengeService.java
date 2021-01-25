package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.ChallengeRepository;
import com.team33.FDMGamification.Model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.*;

@Service
public class ChallengeService {

    private final Logger log = LoggerFactory.getLogger(ChallengeService.class);

    @Autowired
    private ChallengeRepository challengeRepo;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Insert and persist data into Challenge Table with properties.
     *
     * @param title        Title of challenge.
     * @param description  Description text of challenge.
     * @param stream       Stream of the challenge.
     * @param completion   Number of completion.
     * @param thumbnail    Thumbnail of challenge.
     * @param positiveFeedback Positive feedback of challenge.
     * @param negativeFeedback Negative feedback of challenge.
     * @return Challenge: Challenge entity persisted in database.
     */
    public Challenge create(String title, String description,
                            Stream stream, Integer completion,
                            Thumbnail thumbnail,
                            ChallengeFeedback positiveFeedback, ChallengeFeedback negativeFeedback) {
        Challenge challenge = new Challenge(title, description, stream, completion);
        challenge.updateThumbnailProperties(thumbnail);
        challenge.updateFeedbacksProperties(positiveFeedback, negativeFeedback);
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
        List<Challenge> result = challengeRepo.findAll();
        // Avoid auto persistence
        entityManager.setFlushMode(FlushModeType.COMMIT);
        return result;
    }

    /**
     * Update existing challenge in database with properties.
     *
     * @param challengeId  Id of challenge to be updated.
     * @param title        New title of challenge.
     * @param description  New description of challenge.
     * @param stream       New stream of challenge.
     * @param completion   New completion count of challenge.
     * @param thumbnail    New thumbnail of challenge.
     * @param positiveFeedback New positive feedback of challenge.
     * @param negativeFeedback New negative feedback of challenge.
     * @return Challenge: Updated challenge entity.
     */
    @Transactional
    public Challenge update(Integer challengeId, String title, String description,
                            Stream stream, Integer completion,
                            Thumbnail thumbnail,
                            ChallengeFeedback positiveFeedback, ChallengeFeedback negativeFeedback) {
        Challenge challenge = findById(challengeId);
        return update(challenge, title, description, stream, completion, thumbnail, positiveFeedback, negativeFeedback);
    }

    /**
     * Update existing challenge in database with properties.
     *
     * @param challenge    Challenge entity to be updated.
     * @param title        New title of challenge.
     * @param description  New description of challenge.
     * @param stream       New stream of challenge.
     * @param completion   New completion count of challenge.
     * @param thumbnail    New thumbnail of challenge.
     * @param positiveFeedback New positive feedback of challenge.
     * @param negativeFeedback New negative feedback of challenge.
     * @return Challenge: Updated challenge entity.
     */
    public Challenge update(Challenge challenge, String title, String description,
                            Stream stream, Integer completion,
                            Thumbnail thumbnail,
                            ChallengeFeedback positiveFeedback, ChallengeFeedback negativeFeedback) {

        challenge.setChallengeTitle(title);
        challenge.setDescription(description);
        challenge.setStream(stream);
        challenge.setCompletion(completion);
        challenge.updateThumbnailProperties(thumbnail);
        challenge.updateFeedbacksProperties(positiveFeedback, negativeFeedback);

        return challengeRepo.saveAndFlush(challenge);
    }

    public Challenge update(Challenge challenge){
        if(challenge.getId() == null) throw new IllegalArgumentException("Use create for new entity!");
        return update(challenge, challenge.getChallengeTitle(), challenge.getDescription(), challenge.getStream(), challenge.getCompletion(), challenge.getThumbnail(), challenge.getChallengeFeedback().get(true), challenge.getChallengeFeedback().get(false));
    }

    /**
     * Adds one to challenge's completion
     *
     * @param challenge Challenge entity to be updated.
     * @return Challenge: Updated challenge entity.
     */
    public Challenge completionIncrement(Challenge challenge){
        challenge.completionIncrement();
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
}

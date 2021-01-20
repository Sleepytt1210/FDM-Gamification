package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.RatingRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private ChallengeService cls;

    /**
     * Insert and persist data into Rating Table with properties and foreign key ID.
     *
     * @param challengeId  Foreign key id of challenge to be added to.
     * @param rating_value Value of rating (1 - 5).
     * @return Rating: Rating entity persisted in database.
     */
    public Rating create(Integer challengeId, Integer rating_value) {
        Rating rating = new Rating(rating_value);
        return create(challengeId, rating);
    }

    /**
     * Insert and persist data into Rating Table with rating entity and foreign key ID.
     *
     * @param challengeId Foreign key id of challenge to be added to.
     * @param rating      Rating entity with properties.
     * @return Rating: Rating entity persisted in database.
     * @throws IllegalArgumentException If rating value is not between 1 and 5.
     */
    public Rating create(Integer challengeId, Rating rating) {
        Challenge challenge = cls.findById(challengeId);
        return create(challenge, rating);
    }

    /**
     * Insert and persist data into Rating Table with rating entity and foreign entity.
     *
     * @param challenge Foreign entity challenge to be added to.
     * @param rating    Rating entity with properties.
     * @return Rating: Rating entity persisted in database.
     */
    public Rating create(Challenge challenge, Rating rating) {
        rating.setChallenge(challenge);
        rating = ratingRepo.saveAndFlush(rating);
        cls.addRating(challenge, rating);
        return rating;
    }

    /**
     * Insert and persist a collection of ratings into Rating Table.
     *
     * @param challenge Foreign entity challenge to be added to.
     * @param ratings A collection of rating entities with properties to be persisted.
     * @return Set<Rating> ratings
     */
    public Set<Rating> createAll(Challenge challenge, Set<Rating> ratings) {
        for (Rating rating : ratings) {
            rating.setChallenge(challenge);
            rating = ratingRepo.saveAndFlush(rating);
            cls.addRating(challenge, rating);
        }
        return ratings;
    }

    /**
     * Find rating by its ID.
     *
     * @param id Id of rating.
     * @return Rating: Rating entity if found:
     * @throws EntityNotFoundException If rating is not found.
     */
    public Rating findById(Integer id) {
        return ratingRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating not found!"));
    }

    /**
     * Get all ratings in the database.
     *
     * @return List<Rating>: All ratings in the database.
     */
    public List<Rating> getAll() {
        return ratingRepo.findAll();
    }

    /**
     * Update existing rating in database with its ID.
     *
     * @param ratingId     Id of rating to be updated.
     * @param rating_value New value of rating.
     * @return Rating: Updated rating entity.
     */
    public Rating update(Integer ratingId, Integer rating_value) {
        Rating rating = findById(ratingId);
        if (rating_value != null) rating.setRatingValue(rating_value);
        return ratingRepo.saveAndFlush(rating);
    }

    /**
     * Delete a rating by its ID.
     *
     * @param ratingId Id of rating to be deleted.
     */
    public void delete(Integer ratingId) {
        delete(findById(ratingId));
    }

    /**
     * Delete a rating by its entity.
     *
     * @param rating Rating entity to be deleted.
     */
    public void delete(Rating rating) {
        // To ensure bidirectional persistence in database
        rating.getChallenge().getRatings().removeIf(rating1 -> rating1.getRatingId().equals(rating.getRatingId()));
        ratingRepo.delete(rating);
    }

    /**
     * Delete a collection of ratings by entities.
     *
     * @param ratings Collection of ratings to be deleted.
     */
    public void batchDelete(Iterable<Rating> ratings) {
        // To ensure bidirectional persistence in database
        ratings.forEach(q -> q.getChallenge().getRatings().removeIf(rating1 -> rating1.getRatingId().equals(q.getRatingId())));
        ratingRepo.deleteAll(ratings);
    }
}

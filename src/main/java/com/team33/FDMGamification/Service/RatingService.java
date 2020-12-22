package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.RatingRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;

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
     * @throws IllegalArgumentException If rating value is not between 1 and 5.
     */
    public Rating create(Challenge challenge, Rating rating) throws IllegalArgumentException {
        if (rating.getRating_value() < 1 || rating.getRating_value() > 5)
            throw new IllegalArgumentException("Rating value should be between 1 and 5!");
        rating.setChallenge(challenge);
        rating = ratingRepo.saveAndFlush(rating);
        cls.addRating(challenge, rating);
        return rating;
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
        if (rating_value != null) rating.setRating_value(rating_value);
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
        Challenge challenge = rating.getChallenge();
        challenge.getRatings().remove(rating);
        ratingRepo.delete(rating);
    }

    /**
     * Delete a collection of ratings by entities.
     *
     * @param ratings Collection of ratings to be deleted.
     */
    public void batchDelete(Iterable<Rating> ratings) {
        for (Rating r : ratings) {
            Challenge challenge = r.getChallenge();
            challenge.getRatings().remove(r);
        }
        ratingRepo.deleteAll(ratings);
    }
}

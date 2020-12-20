package com.team33.FDMGamification.Service;

import com.team33.FDMGamification.DAO.RatingRepository;
import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.management.InstanceAlreadyExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.List;

@Service
public class RatingService {
    @Autowired
    private RatingRepository ratingRepo;

    @Autowired
    private ChallengeService cls;

    public Rating create(Integer challengeId, Rating rating) throws InstanceAlreadyExistsException {
        Challenge challenge = cls.findById(challengeId);
        rating.setChallenge(challenge);
        rating = ratingRepo.saveAndFlush(rating);
        cls.addRating(challenge, rating);
        return rating;
    }

    public Rating create(Integer challengeId, Integer rating_value) throws InstanceAlreadyExistsException {
        Rating rating = new Rating(rating_value);
        Challenge challenge = cls.findById(challengeId);
        rating.setChallenge(challenge);
        rating = ratingRepo.saveAndFlush(rating);
        cls.addRating(challenge, rating);
        return rating;
    }

    public List<Rating> getAll() {
        return ratingRepo.findAll();
    }

    public Rating findById(Integer id){
        return ratingRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Rating not found!"));
    }

    public Rating update(Integer ratingId, Integer rating_value) {
        Rating rating = findById(ratingId);
        if(rating_value != null) rating.setRating_value(rating_value);
        return ratingRepo.saveAndFlush(rating);
    }

    public void delete(Integer ratingId) {
        delete(findById(ratingId));
    }

    public void delete(Rating rating) {
        Challenge challenge = rating.getChallenge();
        challenge.getRatings().remove(rating);
        ratingRepo.delete(rating);
    }

    public void batchDelete(Iterable<Rating> ratings) {
        for (Rating r : ratings) {
            Challenge challenge = r.getChallenge();
            challenge.getRatings().remove(r);
        }
        ratingRepo.deleteAll(ratings);
    }
}

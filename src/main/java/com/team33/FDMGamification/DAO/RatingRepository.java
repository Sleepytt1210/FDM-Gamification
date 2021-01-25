package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    @Query("SELECT r FROM Rating r WHERE r.challenge.id = :challengeId ")
    List<Rating> getRatingsByChallenge_Id(@Param("challengeId") Integer challengeId);
}

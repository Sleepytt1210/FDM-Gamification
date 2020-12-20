package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.ChallengeFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChallengeFeedbackRepository extends JpaRepository<ChallengeFeedback, Integer> {

    @Query("SELECT f FROM ChallengeFeedback f WHERE f.challenge.id = :id AND f.positive = :positive")
    ChallengeFeedback findByChallengeIdAndChallengeFeedbackPositive(@Param("id") Integer id, @Param("positive") boolean positive);


}

package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    List<Challenge> findChallengesByStreamEquals(Stream stream);
}

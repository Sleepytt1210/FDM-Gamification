package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {

    @Transactional
    @Query("SELECT chl FROM Challenge chl WHERE chl.stream = :stream")
    List<Challenge> findChallengesByStreamEquals(@Param("stream") Stream stream);
}

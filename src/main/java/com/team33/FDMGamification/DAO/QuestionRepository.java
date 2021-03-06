package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Challenge;
import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.QuestionType;
import com.team33.FDMGamification.Model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.questionType = :questionType")
    List<Question> queryQuestionsByQuestionTypeEquals(@Param("questionType") QuestionType questionType);

    @Query("SELECT q FROM Question q WHERE q.challenge.id = :challengeId ")
    List<Question> getQuestionsByChallenge_Id(@Param("challengeId") Integer challengeId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Question q SET q.challenge = :challenge WHERE q.questionId = :questionId")
    void replaceChallenge(@Param("challenge") Challenge challenge, @Param("questionId") Integer questionId);
}

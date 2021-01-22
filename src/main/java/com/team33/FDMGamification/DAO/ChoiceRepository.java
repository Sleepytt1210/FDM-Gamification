package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface ChoiceRepository extends JpaRepository<Choice, Integer> {

    @Query("SELECT c FROM Choice c WHERE c.question.questionId = :questionId")
    List<Choice> getChoicesByQuestion_QuestionId(@Param("questionId") Integer questionId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Choice c SET c.question = :question WHERE c.choiceId = :choiceId")
    void replaceQuestion(@Param("question") Question question, @Param("choiceId") Integer choiceId);
}

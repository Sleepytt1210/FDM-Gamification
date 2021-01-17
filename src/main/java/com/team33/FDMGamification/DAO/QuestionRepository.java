package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Question;
import com.team33.FDMGamification.Model.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Integer> {

    @Query("SELECT q FROM Question q WHERE q.questionType = :questionType")
    List<Question> queryQuestionsByQuestionTypeEquals(@Param("questionType") QuestionType questionType);
}

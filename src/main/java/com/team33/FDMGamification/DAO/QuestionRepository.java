package com.team33.FDMGamification.DAO;

import com.team33.FDMGamification.Model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
}

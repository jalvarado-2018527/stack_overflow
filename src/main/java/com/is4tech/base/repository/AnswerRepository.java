package com.is4tech.base.repository;

import com.is4tech.base.domain.Answers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answers, Integer> {
    Page<Answers> findAllByAnswerNotContainsIgnoreCase(String search, Pageable page);

    Page<Answers> findAllByQuestionId(Integer questionId, String search, Pageable page);

    @Query("select count(a.answer) from Answers a where a.questionId = :questionId")
    Integer countAnswers(@Param("questionId") Integer questionId);

}

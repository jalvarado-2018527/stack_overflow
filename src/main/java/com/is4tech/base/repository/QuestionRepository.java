package com.is4tech.base.repository;

import com.is4tech.base.domain.Questions;
import com.is4tech.base.domain.Votes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface QuestionRepository extends JpaRepository<Questions, Integer> {
    Optional<Questions> findFirstByQuestionIgnoreCase(String question);

    List<Questions> findAllByTitleContainsIgnoreCase(String title);

    List<Questions> findAllByQuestionUserContainsIgnoreCase(String user);


    List<Questions> findAllByQuestionContainsIgnoreCase(String search);

    Page<Questions> findAllByQuestionNotContainsIgnoreCase(String search, Pageable page);


}


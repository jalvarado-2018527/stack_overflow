package com.is4tech.base.repository;

import com.is4tech.base.domain.QuestionsTags;
import com.is4tech.base.domain.Tags;
import com.is4tech.base.dto.QuestionsTagsDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagsRepository extends JpaRepository<QuestionsTags, QuestionsTagsDTO> {

    @Query("select t from Tags t join QuestionsTags qt on qt.id.tagId = t.id where qt.id.questionId = :questionId")
    List<Tags> findTag(@Param("questionId") Integer questionId);
}


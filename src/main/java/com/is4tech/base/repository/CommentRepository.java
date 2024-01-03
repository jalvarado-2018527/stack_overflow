package com.is4tech.base.repository;

import com.is4tech.base.domain.Comments;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface CommentRepository extends JpaRepository<Comments, Integer> {
    Page<Comments> findAllByCommentNotContainsIgnoreCase(String search, Pageable page);

    Page<Comments> findAllByAnswerId(Integer answerId, String search, Pageable page);

    @Query("select count(c.comment) from Comments c where c.answerId = :answerId")
    Integer countComments(@Param("answerId") Integer questionId);
}

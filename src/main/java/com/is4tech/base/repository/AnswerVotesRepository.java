package com.is4tech.base.repository;

import com.is4tech.base.domain.AnswerVotes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerVotesRepository extends JpaRepository<AnswerVotes, Integer> {
    Page<AnswerVotes> findAllByVoteNotContainsIgnoreCase(String search, Pageable page);

    @Query("select v from AnswerVotes v where v.answerVoteId = :answerVoteId")
    List<AnswerVotes> findAllByAnswerId(@Param("answerVoteId") Integer answerVoteId);

    @Query("select sum(v.vote) from AnswerVotes v where v.answerVoteId = :answerVoteId")
    Integer sumAnswerVotes(@Param("answerVoteId") Integer answerVoteId);
}

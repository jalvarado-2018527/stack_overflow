package com.is4tech.base.repository;

import com.is4tech.base.domain.Votes;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoteRepository extends JpaRepository<Votes, Integer >{

    Page<Votes> findAllByVoteNotContainsIgnoreCase(String search, Pageable page);

    @Query("select v from Votes v where v.questionId = :questionId")
    List<Votes> findAllByQuestionId(@Param("questionId") Integer questionId);

    @Query("SELECT SUM(v.vote) FROM Votes v WHERE v.questionId = :questionId")
    Integer sumVotes(@Param("questionId") Integer questionId);

}

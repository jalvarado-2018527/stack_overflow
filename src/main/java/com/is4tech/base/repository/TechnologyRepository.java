package com.is4tech.base.repository;

import com.is4tech.base.domain.Technology;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TechnologyRepository extends JpaRepository<Technology, Integer> {
    Optional<Technology> findFirstByNameIgnoreCase(String name);

    List<Technology> findAllByNameContains(String name);
    Page<Technology> findAllByNameNotContainsIgnoreCase(String search, Pageable page);
}


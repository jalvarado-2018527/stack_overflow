package com.is4tech.base.repository;



import com.is4tech.base.domain.Tags;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tags, Integer> {
    Optional<Tags> findFirstByCodeIgnoreCase(String code);


    List<Tags> findByNameContainsIgnoreCase(String name);

    Page<Tags> findAllByCodeNotContainsIgnoreCase(String code, Pageable page);
}

package com.is4tech.base.repository;

import com.is4tech.base.domain.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findFirstByEmailIgnoreCase(String email);

    Optional<Users> findByEmail(String email);
    List<Users> findAllByNameContainsIgnoreCase(String name);
    Page<Users> findAllByEmailNotContainsIgnoreCase(String search, Pageable page);
}

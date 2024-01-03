package com.is4tech.base.repository;

import com.is4tech.base.domain.Roles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.relation.Role;
import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Integer> {
    Optional<Roles> findFirstByCodeIgnoreCase(String code);
    List<Roles> findAllByCodeContains(String code);
    Page<Roles> findAllByCodeNotContainsIgnoreCase(String code, Pageable page);

}

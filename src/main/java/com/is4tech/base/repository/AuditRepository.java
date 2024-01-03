package com.is4tech.base.repository;

import com.is4tech.base.domain.Audit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditRepository extends JpaRepository<Audit, Integer> {
    Optional<Audit> findFirstByUserAuditIgnoreCase(String user);

    List<Audit> findAllByUserAuditContainsIgnoreCase(String auditUser);

    List<Audit> findByEntity(String entity);

    List<Audit> findByAction(String action);

    Page<Audit> findAllByUserAuditNotContainsIgnoreCase(String search, Pageable page);



    @Query("SELECT a FROM Audit a WHERE a.changeDate BETWEEN :startDate AND :endDate")
    List<Audit> findAuditsByDateRange(@Param("startDate") Timestamp startDate, @Param("endDate") Timestamp endDate);

}

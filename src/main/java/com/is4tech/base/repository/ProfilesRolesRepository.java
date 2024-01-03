package com.is4tech.base.repository;

import com.is4tech.base.domain.ProfilesRoles;
import com.is4tech.base.domain.ProfilesRolesId;
import com.is4tech.base.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProfilesRolesRepository extends JpaRepository<ProfilesRoles, ProfilesRolesId> {

    @Query("select r from Roles r join ProfilesRoles pr on pr.id.roleId = r.roleId where pr.id.profileId = :profileId")
    List<Roles> findRoles(@Param("profileId") Integer profileId);
}
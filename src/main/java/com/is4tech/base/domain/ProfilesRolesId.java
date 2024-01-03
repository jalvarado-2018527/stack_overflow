package com.is4tech.base.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProfilesRolesId implements Serializable {
    private Integer profileId;
    private Integer roleId;
}

package com.is4tech.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDTO implements Serializable {

    private Integer userId;
    private String name;
    private String surname;
    private String email;
    @JsonIgnore
    private String password;
    private Boolean status;
    private Integer profileId;

}

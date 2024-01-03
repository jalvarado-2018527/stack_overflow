package com.is4tech.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileDTO implements Serializable {
    private Integer profileId;
    private String code;
    private String description;
    private Integer status;
    private List<String>  resources;
}

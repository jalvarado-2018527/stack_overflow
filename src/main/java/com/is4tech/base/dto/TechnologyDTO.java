package com.is4tech.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class TechnologyDTO {
    private Integer technologyId;
    private String abbreviation;
    private String name;
    private Boolean status;
}

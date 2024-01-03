package com.is4tech.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class VoteDTO implements Serializable {
    private Integer voteId;
    private String name;
    private Integer vote;
    private Integer questionId;
}

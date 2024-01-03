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
public class AnswerVotesDTO implements Serializable {
    private Integer answerVoteId;
    private String name;
    private Integer vote;
    private Integer answerId;
}

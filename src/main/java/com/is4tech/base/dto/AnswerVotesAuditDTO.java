package com.is4tech.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerVotesAuditDTO implements Serializable {
    private Integer answerVoteId;
    private String name;
    private Integer vote;
    private Integer answerId;
}

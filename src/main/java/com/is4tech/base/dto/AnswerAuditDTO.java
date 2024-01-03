package com.is4tech.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.is4tech.base.domain.AnswerVotes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AnswerAuditDTO implements Serializable {
    private Integer answerId;
    private String answer;
    private String answerUser;
    private Date createdAt;
    private Integer questionId;
    private List<AnswerVotes> answerVotes;
}

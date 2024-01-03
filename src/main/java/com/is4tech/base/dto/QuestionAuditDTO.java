package com.is4tech.base.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.is4tech.base.domain.Votes;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuestionAuditDTO implements Serializable {
    private Integer questionId;
    private String title;
    private String question;
    private String technology;
    private Timestamp createdAt;
    private String questionUser;
    private List<String> questionTags;
    private List<Votes> questionVotes;
}

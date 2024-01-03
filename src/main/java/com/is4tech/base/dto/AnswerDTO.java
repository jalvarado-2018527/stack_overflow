package com.is4tech.base.dto;

import com.is4tech.base.domain.AnswerVotes;
import com.is4tech.base.domain.Comments;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class AnswerDTO implements Serializable {
    private Integer answerId;
    private String answer;
    private String answerUser;
    private Date createdAt;
    private Integer questionId;
    private List<AnswerVotes> answerVotes;
}

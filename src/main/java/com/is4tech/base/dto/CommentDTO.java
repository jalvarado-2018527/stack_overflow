package com.is4tech.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CommentDTO implements Serializable {

    private Integer commentId;
    private String comment;
    private String userComment;
    private Date createdAt;
    private Integer answerId;

}

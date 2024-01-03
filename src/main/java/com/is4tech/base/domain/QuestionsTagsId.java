package com.is4tech.base.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class QuestionsTagsId implements Serializable {

    private Integer questionId;
    private Integer tagId;

}

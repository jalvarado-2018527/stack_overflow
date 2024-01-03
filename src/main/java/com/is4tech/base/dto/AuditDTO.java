package com.is4tech.base.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class AuditDTO implements Serializable{
    private Integer id;
    private Timestamp changeDate;
    private String userAudit;
    private String requestBody;
    private String entity;
    private Integer statusCode;
    private String action;

}

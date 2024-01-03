package com.is4tech.base.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class ErrorDTO {

    private ApiError error;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiError {

        private Integer errorCode;
        private String errorType;
        private String code;
        private String description;
    }
}

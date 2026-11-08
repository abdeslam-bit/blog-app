package com.example.blog.domain.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@AllArgsConstructor 
@NoArgsConstructor 
@Builder 
public class ApiErrorResponse {
    private int status;
    private String message;
    private List<ErrorField> errors;
    
    @Data 
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ErrorField {
        @JsonProperty("field")
        @JsonAlias("fieldName")
        private String field;

        @JsonProperty("message")
        @JsonAlias("errorMessage")
        private String message;

        public String getFieldName() {
            return field;
        }

        public String getErrorMessage() {
            return message;
        }
    }
}

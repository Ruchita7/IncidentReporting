package com.example.incidentreport.contract;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.List;

/**
 * API response contract class
 */
@JsonDeserialize
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult implements Serializable {

    private String message;
    private List<String> details;
    public ResponseResult(String message) {
        this.message=message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ResponseResult(String message, List<String> details) {
        this.message = message;
        this.details = details;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }
}

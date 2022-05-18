package com.example.incidentreport.utils;

import com.example.incidentreport.contract.ResponseResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    private static  final String ALREADY_EXISTS="Already exists";
    private static final String BAD_REQUEST="Bad Request";

    @ExceptionHandler(ConflictException.class)
    public static ResponseEntity<ResponseResult> handleConflictException(ConflictException ex, WebRequest webRequest) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ResponseResult error = new ResponseResult(ALREADY_EXISTS, details);
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadRequestException.class)
    public static ResponseEntity<ResponseResult> handleBadRequestException(BadRequestException ex,
                                                                           WebRequest webRequest) {
        List<String> details = new ArrayList<>();
        details.add(ex.getLocalizedMessage());
        ResponseResult error = new ResponseResult(BAD_REQUEST, details);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}

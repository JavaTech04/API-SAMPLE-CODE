package com.javatech.exception;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ExceptionResponse {
    private Date timestamp;
    private int status;
    private String path;
    private String error;
    private String message;
}

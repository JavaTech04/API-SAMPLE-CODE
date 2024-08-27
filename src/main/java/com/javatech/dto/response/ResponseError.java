package com.javatech.dto.response;

public class ResponseError<T> extends ResponseData<T> {
    public ResponseError(int status, String message) {
        super(status, message);
    }
}
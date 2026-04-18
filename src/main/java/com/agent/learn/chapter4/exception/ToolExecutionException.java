package com.agent.learn.chapter4.exception;

import org.springframework.http.HttpStatus;

public class ToolExecutionException extends AgentException {

    public ToolExecutionException(String code, String message) {
        super(HttpStatus.BAD_GATEWAY, code, message);
    }
}

package com.agent.learn.chapter4.exception;

import org.springframework.http.HttpStatus;

public class LlmInvocationException extends AgentException {

    public LlmInvocationException(String code, String message) {
        super(HttpStatus.BAD_GATEWAY, code, message);
    }
}

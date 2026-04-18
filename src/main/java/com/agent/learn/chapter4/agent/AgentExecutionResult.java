package com.agent.learn.chapter4.agent;

public record AgentExecutionResult(
        boolean success,
        String code,
        String message,
        String answer
) {

    public static AgentExecutionResult success(String answer) {
        return new AgentExecutionResult(true, "OK", "success", answer);
    }

    public static AgentExecutionResult failure(String code, String message) {
        return new AgentExecutionResult(false, code, message, null);
    }
}

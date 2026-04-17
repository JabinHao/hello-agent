package com.agent.learn.chapter4.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AgentResponse {
    private String answer;

    public AgentResponse() {
    }

    public AgentResponse(String answer) {
        this.answer = answer;
    }
}

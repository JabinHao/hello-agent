package com.agent.learn.chapter4.web;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AgentRequest {
    private String question;
    private String task;
}

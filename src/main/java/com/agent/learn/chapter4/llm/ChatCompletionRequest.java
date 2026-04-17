package com.agent.learn.chapter4.llm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ChatCompletionRequest {
    private String model;
    private List<ChatMessage> messages;
    private double temperature;
    private boolean stream;

    public ChatCompletionRequest(String model, List<ChatMessage> messages, double temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
        this.stream = false;
    }
}

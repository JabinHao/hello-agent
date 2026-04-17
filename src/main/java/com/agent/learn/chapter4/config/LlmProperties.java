package com.agent.learn.chapter4.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "llm")
public class LlmProperties {
    private String modelId;
    private String apiKey;
    private String baseUrl;
    private int timeout = 60;
}

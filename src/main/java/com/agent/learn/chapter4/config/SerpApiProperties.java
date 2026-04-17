package com.agent.learn.chapter4.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ToString
@ConfigurationProperties(prefix = "serpapi")
public class SerpApiProperties {
    private String apiKey;
}

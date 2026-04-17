package com.agent.learn.chapter4.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties({LlmProperties.class, SerpApiProperties.class})
public class Chapter4Config {

    @Bean(name = "llmRestClient")
    public RestClient llmRestClient(LlmProperties props) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(props.getTimeout()));
        return RestClient.builder().requestFactory(factory).build();
    }

    @Bean(name = "serpApiRestClient")
    public RestClient serpApiRestClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(10));
        factory.setReadTimeout(Duration.ofSeconds(30));
        return RestClient.builder()
                .baseUrl("https://serpapi.com")
                .requestFactory(factory)
                .build();
    }
}

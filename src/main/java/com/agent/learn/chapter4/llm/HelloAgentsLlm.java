package com.agent.learn.chapter4.llm;

import com.agent.learn.chapter4.config.LlmProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;

@Slf4j
@Service
public class HelloAgentsLlm {

    private final LlmProperties props;
    private final RestClient restClient;

    public HelloAgentsLlm(LlmProperties props, @Qualifier("llmRestClient") RestClient restClient) {
        this.props = props;
        this.restClient = restClient;
        if (props.getModelId() == null || props.getModelId().isBlank()
                || props.getApiKey() == null || props.getApiKey().isBlank()
                || props.getBaseUrl() == null || props.getBaseUrl().isBlank()) {
            throw new IllegalStateException(
                    "LLM_MODEL_ID, LLM_API_KEY and LLM_BASE_URL must be provided via env or .env file.");
        }
    }

    public String think(List<ChatMessage> messages) {
        return think(messages, 0.0);
    }

    public String think(List<ChatMessage> messages, double temperature) {
        log.info("🧠 Calling model {}", props.getModelId());
        ChatCompletionRequest body = new ChatCompletionRequest(props.getModelId(), messages, temperature);
        try {
            ChatCompletionResponse resp = restClient.post()
                    .uri(props.getBaseUrl() + "/chat/completions")
                    .header("Authorization", "Bearer " + props.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(ChatCompletionResponse.class);
            if (resp == null || resp.getChoices() == null || resp.getChoices().isEmpty()) {
                log.warn("LLM returned an empty response");
                return null;
            }
            String content = resp.getChoices().get(0).getMessage().getContent();
            log.info("✅ LLM response received ({} chars)", content == null ? 0 : content.length());
            return content;
        } catch (RestClientException e) {
            log.error("❌ LLM API call failed: {}", e.getMessage(), e);
            return null;
        }
    }
}

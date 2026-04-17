package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.ChatMessage;
import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Planner {

    private static final String PROMPT_TEMPLATE = """
            You are a top AI planner. Decompose the user's complex question into a small set of simple steps.
            Each step must be an independent, executable sub-task, in strict logical order.
            Your output must be a JSON array of strings, one per sub-task.

            Question: {question}

            Output strictly in the following format. The ```json fences are required:
            ```json
            ["step 1", "step 2", "step 3"]
            ```
            """;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HelloAgentsLlm llm;

    public Planner(HelloAgentsLlm llm) {
        this.llm = llm;
    }

    public List<String> plan(String question) {
        log.info("--- Generating plan ---");
        String prompt = PROMPT_TEMPLATE.replace("{question}", question);
        String response = llm.think(List.of(ChatMessage.user(prompt)));
        if (response == null) {
            return List.of();
        }
        log.info("✅ Plan generated:\n{}", response);
        try {
            String json = extractJsonArray(response);
            return MAPPER.readValue(json, new TypeReference<>() {});
        } catch (JsonProcessingException | IllegalArgumentException e) {
            log.error("❌ Failed to parse plan: {}", e.getMessage());
            return List.of();
        }
    }

    private String extractJsonArray(String text) {
        int start = text.indexOf("```json");
        if (start >= 0) {
            int from = start + "```json".length();
            int end = text.indexOf("```", from);
            if (end > from) {
                return text.substring(from, end).strip();
            }
        }
        int bracket = text.indexOf('[');
        int closing = text.lastIndexOf(']');
        if (bracket >= 0 && closing > bracket) {
            return text.substring(bracket, closing + 1);
        }
        throw new IllegalArgumentException("No JSON array found in response");
    }
}

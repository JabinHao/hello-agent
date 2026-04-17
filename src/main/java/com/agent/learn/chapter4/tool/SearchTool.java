package com.agent.learn.chapter4.tool;

import com.agent.learn.chapter4.config.SerpApiProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class SearchTool {

    private static final ParameterizedTypeReference<Map<String, Object>> MAP_TYPE =
            new ParameterizedTypeReference<>() {};

    private final SerpApiProperties props;
    private final RestClient restClient;

    public SearchTool(SerpApiProperties props, @Qualifier("serpApiRestClient") RestClient restClient) {
        this.props = props;
        this.restClient = restClient;
    }

    public String search(String query) {
        log.info("🔍 SerpApi search: {}", query);
        if (props.getApiKey() == null || props.getApiKey().isBlank()) {
            return "Error: SERPAPI_API_KEY is not configured.";
        }
        try {
            Map<String, Object> results = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search.json")
                            .queryParam("engine", "google")
                            .queryParam("q", query)
                            .queryParam("api_key", props.getApiKey())
                            .queryParam("gl", "cn")
                            .queryParam("hl", "zh-cn")
                            .build())
                    .retrieve()
                    .body(MAP_TYPE);
            if (results == null) {
                return "Sorry, no results were returned for '" + query + "'.";
            }
            return parseResults(results, query);
        } catch (RestClientException e) {
            log.error("Search failed: {}", e.getMessage(), e);
            return "Search error: " + e.getMessage();
        }
    }

    @SuppressWarnings("unchecked")
    private String parseResults(Map<String, Object> results, String query) {
        Object answerBoxList = results.get("answer_box_list");
        if (answerBoxList instanceof List<?> list && !list.isEmpty()) {
            return list.stream().map(String::valueOf).collect(Collectors.joining("\n"));
        }
        Object answerBox = results.get("answer_box");
        if (answerBox instanceof Map<?, ?> box) {
            Object answer = ((Map<String, Object>) box).get("answer");
            if (answer != null) {
                return String.valueOf(answer);
            }
        }
        Object knowledgeGraph = results.get("knowledge_graph");
        if (knowledgeGraph instanceof Map<?, ?> kg) {
            Object description = ((Map<String, Object>) kg).get("description");
            if (description != null) {
                return String.valueOf(description);
            }
        }
        Object organic = results.get("organic_results");
        if (organic instanceof List<?> list && !list.isEmpty()) {
            List<Map<String, Object>> top = list.stream()
                    .limit(3)
                    .map(o -> (Map<String, Object>) o)
                    .toList();
            return IntStream.range(0, top.size())
                    .mapToObj(i -> {
                        Map<String, Object> r = top.get(i);
                        return "[" + (i + 1) + "] " + r.getOrDefault("title", "") + "\n"
                                + r.getOrDefault("snippet", "");
                    })
                    .collect(Collectors.joining("\n\n"));
        }
        return "Sorry, no information found for '" + query + "'.";
    }
}

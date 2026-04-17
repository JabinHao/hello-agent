package com.agent.learn.chapter4.tool;

import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class ToolExecutor {

    private final Map<String, Tool> tools = new LinkedHashMap<>();

    public void register(String name, String description, Function<String, String> func) {
        if (tools.containsKey(name)) {
            log.warn("Tool '{}' already exists and will be overwritten.", name);
        }
        tools.put(name, new Tool(name, description, func));
        log.info("Tool '{}' registered.", name);
    }

    public Tool get(String name) {
        return tools.get(name);
    }

    public String getAvailableTools() {
        return tools.values().stream()
                .map(t -> "- " + t.name() + ": " + t.description())
                .collect(Collectors.joining("\n"));
    }
}

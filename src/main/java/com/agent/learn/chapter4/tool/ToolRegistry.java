package com.agent.learn.chapter4.tool;

import com.agent.learn.chapter4.exception.ToolExecutionException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ToolRegistry {

    private final Map<String, Tool> tools;

    public ToolRegistry(List<Tool> tools) {
        this.tools = tools.stream().collect(Collectors.toMap(
                Tool::name,
                Function.identity(),
                (left, right) -> right,
                LinkedHashMap::new
        ));
    }

    public String describeAvailableTools() {
        return tools.values().stream()
                .map(t -> "- " + t.name() + ": " + t.description())
                .collect(Collectors.joining("\n"));
    }

    public String execute(String toolName, String input) {
        Tool tool = tools.get(toolName);
        if (tool == null) {
            throw new ToolExecutionException("TOOL_NOT_FOUND", "no tool named '" + toolName + "'");
        }
        try {
            return tool.function().apply(input);
        } catch (ToolExecutionException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new ToolExecutionException("TOOL_EXECUTION_FAILED", e.getMessage());
        }
    }
}

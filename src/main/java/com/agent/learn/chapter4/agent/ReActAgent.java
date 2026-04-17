package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.ChatMessage;
import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import com.agent.learn.chapter4.tool.SearchTool;
import com.agent.learn.chapter4.tool.Tool;
import com.agent.learn.chapter4.tool.ToolExecutor;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class ReActAgent {

    private static final String PROMPT_TEMPLATE = """
            You are an intelligent assistant that can call external tools.

            Available tools:
            {tools}

            Please respond strictly in the following format:

            Thought: your reasoning about the problem and next step.
            Action: your chosen action, one of:
            - `ToolName[tool_input]`: call one of the available tools.
            - `Finish[final_answer]`: when you have gathered enough info to answer.
            You MUST output `Finish[...]` after Action: once you can answer the user's question.

            Now solve the following problem:
            Question: {question}
            History: {history}
            """;

    private static final Pattern THOUGHT_PATTERN =
            Pattern.compile("Thought:\\s*(.*?)(?=\\nAction:|$)", Pattern.DOTALL);
    private static final Pattern ACTION_PATTERN =
            Pattern.compile("Action:\\s*(.*?)$", Pattern.DOTALL);
    private static final Pattern ACTION_CALL_PATTERN =
            Pattern.compile("(\\w+)\\[(.*)]", Pattern.DOTALL);

    private final HelloAgentsLlm llm;
    private final SearchTool searchTool;
    private final ToolExecutor toolExecutor = new ToolExecutor();
    private final int maxSteps = 5;

    public ReActAgent(HelloAgentsLlm llm, SearchTool searchTool) {
        this.llm = llm;
        this.searchTool = searchTool;
    }

    @PostConstruct
    void registerDefaultTools() {
        toolExecutor.register(
                "Search",
                "A web search engine. Use it when you need facts, current events, or info not in your knowledge base.",
                searchTool::search);
    }

    public String run(String question) {
        List<String> history = new ArrayList<>();
        for (int step = 1; step <= maxSteps; step++) {
            log.info("\n--- Step {} ---", step);

            String prompt = PROMPT_TEMPLATE
                    .replace("{tools}", toolExecutor.getAvailableTools())
                    .replace("{question}", question)
                    .replace("{history}", String.join("\n", history));

            String response = llm.think(List.of(ChatMessage.user(prompt)));
            if (response == null) {
                log.error("LLM returned no response; aborting.");
                return null;
            }

            String thought = extract(THOUGHT_PATTERN, response);
            String action = extract(ACTION_PATTERN, response);
            if (thought != null) log.info("🤔 Thought: {}", thought);
            if (action == null) {
                log.warn("Could not parse Action; aborting.");
                return null;
            }

            if (action.startsWith("Finish")) {
                String finalAnswer = parseActionInput(action);
                log.info("🎉 Final answer: {}", finalAnswer);
                return finalAnswer;
            }

            Matcher m = ACTION_CALL_PATTERN.matcher(action);
            if (!m.matches()) {
                history.add("Observation: invalid Action format; please retry.");
                continue;
            }
            String toolName = m.group(1);
            String toolInput = m.group(2);
            log.info("🎬 Action: {}[{}]", toolName, toolInput);

            Tool tool = toolExecutor.get(toolName);
            String observation = (tool == null)
                    ? "Error: no tool named '" + toolName + "'."
                    : tool.function().apply(toolInput);
            log.info("👀 Observation: {}", observation);

            history.add("Action: " + action);
            history.add("Observation: " + observation);
        }
        log.warn("Reached max steps without finishing.");
        return null;
    }

    private String extract(Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        return m.find() ? m.group(1).strip() : null;
    }

    private String parseActionInput(String action) {
        Matcher m = Pattern.compile("\\w+\\[(.*)]", Pattern.DOTALL).matcher(action);
        return m.matches() ? m.group(1) : "";
    }
}

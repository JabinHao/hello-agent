package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.ChatMessage;
import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class Executor {

    private static final String PROMPT_TEMPLATE = """
            You are a top AI executor. Solve the problem step by step, strictly following the given plan.
            You will receive the original question, the full plan, the steps already completed, and the current step.
            Focus only on the **current step** and output its final answer with no extra explanation.

            # Original question:
            {question}

            # Full plan:
            {plan}

            # Past steps and results:
            {history}

            # Current step:
            {current_step}

            Output the answer to the current step only:
            """;

    private final HelloAgentsLlm llm;

    public Executor(HelloAgentsLlm llm) {
        this.llm = llm;
    }

    public String execute(String question, List<String> plan) {
        log.info("\n--- Executing plan ---");
        StringBuilder history = new StringBuilder();
        String finalAnswer = "";
        for (int i = 0; i < plan.size(); i++) {
            String step = plan.get(i);
            log.info("\n-> Step {}/{}: {}", i + 1, plan.size(), step);

            String prompt = PROMPT_TEMPLATE
                    .replace("{question}", question)
                    .replace("{plan}", String.join("\n", plan))
                    .replace("{history}", history.length() == 0 ? "(none)" : history.toString())
                    .replace("{current_step}", step);

            String response = llm.think(List.of(ChatMessage.user(prompt)));
            if (response == null) response = "";

            history.append("Step ").append(i + 1).append(": ").append(step).append("\n")
                    .append("Result: ").append(response).append("\n\n");
            finalAnswer = response;
            log.info("✅ Step {} done: {}", i + 1, finalAnswer);
        }
        return finalAnswer;
    }
}

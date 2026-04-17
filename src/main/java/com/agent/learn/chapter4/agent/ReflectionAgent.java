package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.ChatMessage;
import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReflectionAgent {

    private static final String INITIAL_PROMPT = """
            You are a senior Python developer. Write a Python function that meets the following requirement.
            The code must include a complete signature, a docstring, and follow PEP 8.

            Requirement: {task}

            Output the code only, no extra explanation.
            """;

    private static final String REFLECT_PROMPT = """
            You are an extremely strict code reviewer and senior algorithm engineer with the highest standard for performance.
            Review the Python code below and focus on bottlenecks in **algorithmic efficiency**.

            # Original task:
            {task}

            # Code under review:
            ```python
            {code}
            ```

            Analyze the time complexity and judge whether a more efficient algorithm exists.
            If yes, point out the weaknesses and propose a concrete improvement
            (e.g. use the Sieve of Eratosthenes instead of trial division).
            Only answer "no need for improvement" if the algorithm is already optimal.
            Output the feedback only, no extra explanation.
            """;

    private static final String REFINE_PROMPT = """
            You are a senior Python developer. Refine your code based on the reviewer's feedback.

            # Original task:
            {task}

            # Your previous attempt:
            {last_code_attempt}

            # Reviewer feedback:
            {feedback}

            Produce an improved version that includes a complete signature, a docstring, and follows PEP 8.
            Output the refined code only, no extra explanation.
            """;

    private final HelloAgentsLlm llm;
    private final int maxIterations;

    public ReflectionAgent(HelloAgentsLlm llm) {
        this.llm = llm;
        this.maxIterations = 3;
    }

    public String run(String task) {
        log.info("\n--- Task ---\n{}", task);
        Memory memory = new Memory();

        log.info("\n--- Initial attempt ---");
        String initialCode = ask(INITIAL_PROMPT.replace("{task}", task));
        memory.add(MemoryRecord.Type.EXECUTION, initialCode);

        for (int i = 1; i <= maxIterations; i++) {
            log.info("\n--- Iteration {}/{} ---", i, maxIterations);

            log.info("-> Reflecting...");
            String lastCode = memory.getLastExecution();
            String feedback = ask(REFLECT_PROMPT
                    .replace("{task}", task)
                    .replace("{code}", lastCode));
            memory.add(MemoryRecord.Type.REFLECTION, feedback);

            if (feedback.contains("no need for improvement")
                    || feedback.contains("无需改进")) {
                log.info("✅ Reviewer says the code is already optimal.");
                break;
            }

            log.info("-> Refining...");
            String refined = ask(REFINE_PROMPT
                    .replace("{task}", task)
                    .replace("{last_code_attempt}", lastCode)
                    .replace("{feedback}", feedback));
            memory.add(MemoryRecord.Type.EXECUTION, refined);
        }

        String finalCode = memory.getLastExecution();
        log.info("\n--- Done ---\nFinal code:\n{}", finalCode);
        return finalCode;
    }

    private String ask(String prompt) {
        String response = llm.think(List.of(ChatMessage.user(prompt)));
        return response == null ? "" : response;
    }
}

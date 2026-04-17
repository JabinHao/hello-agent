package com.agent.learn.chapter4.agent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PlanAndSolveAgent {

    private final Planner planner;
    private final Executor executor;

    public PlanAndSolveAgent(Planner planner, Executor executor) {
        this.planner = planner;
        this.executor = executor;
    }

    public String run(String question) {
        log.info("\n--- Question ---\n{}", question);
        List<String> plan = planner.plan(question);
        if (plan.isEmpty()) {
            log.warn("--- Aborted --- could not produce a valid plan.");
            return null;
        }
        String finalAnswer = executor.execute(question, plan);
        log.info("\n--- Done --- final answer: {}", finalAnswer);
        return finalAnswer;
    }
}

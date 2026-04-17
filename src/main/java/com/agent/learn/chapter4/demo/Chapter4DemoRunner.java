package com.agent.learn.chapter4.demo;

import com.agent.learn.chapter4.agent.PlanAndSolveAgent;
import com.agent.learn.chapter4.agent.ReActAgent;
import com.agent.learn.chapter4.agent.ReflectionAgent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Chapter4DemoRunner implements CommandLineRunner {

    private final ReActAgent reActAgent;
    private final ReflectionAgent reflectionAgent;
    private final PlanAndSolveAgent planAndSolveAgent;

    public Chapter4DemoRunner(ReActAgent reActAgent,
                              ReflectionAgent reflectionAgent,
                              PlanAndSolveAgent planAndSolveAgent) {
        this.reActAgent = reActAgent;
        this.reflectionAgent = reflectionAgent;
        this.planAndSolveAgent = planAndSolveAgent;
    }

    @Override
    public void run(String... args) {
        if (args.length == 0) {
            return;
        }
        String which = args[0];
        switch (which) {
            case "react" -> reActAgent.run("What is Huawei's latest phone, and what are its main selling points?");
            case "reflection" -> reflectionAgent.run("Write a Python function that finds all prime numbers between 1 and n.");
            case "plan-and-solve" -> planAndSolveAgent.run(
                    "A fruit shop sold 15 apples on Monday, twice as many on Tuesday, and 5 fewer on Wednesday than on Tuesday. How many apples were sold over the three days?");
            default -> log.warn("Unknown demo '{}'. Use one of: react | reflection | plan-and-solve", which);
        }
    }
}

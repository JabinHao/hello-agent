package com.agent.learn.chapter4.web;

import com.agent.learn.chapter4.agent.PlanAndSolveAgent;
import com.agent.learn.chapter4.agent.ReActAgent;
import com.agent.learn.chapter4.agent.ReflectionAgent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chapter4")
public class Chapter4Controller {

    private final ReActAgent reActAgent;
    private final ReflectionAgent reflectionAgent;
    private final PlanAndSolveAgent planAndSolveAgent;

    public Chapter4Controller(ReActAgent reActAgent,
                              ReflectionAgent reflectionAgent,
                              PlanAndSolveAgent planAndSolveAgent) {
        this.reActAgent = reActAgent;
        this.reflectionAgent = reflectionAgent;
        this.planAndSolveAgent = planAndSolveAgent;
    }

    @PostMapping("/react")
    public AgentResponse react(@RequestBody AgentRequest req) {
        return new AgentResponse(reActAgent.run(req.getQuestion()));
    }

    @PostMapping("/reflection")
    public AgentResponse reflection(@RequestBody AgentRequest req) {
        return new AgentResponse(reflectionAgent.run(req.getTask()));
    }

    @PostMapping("/plan-and-solve")
    public AgentResponse planAndSolve(@RequestBody AgentRequest req) {
        return new AgentResponse(planAndSolveAgent.run(req.getQuestion()));
    }
}

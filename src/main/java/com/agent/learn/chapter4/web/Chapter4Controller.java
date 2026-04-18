package com.agent.learn.chapter4.web;

import com.agent.learn.chapter4.agent.PlanAndSolveAgent;
import com.agent.learn.chapter4.agent.ReActAgent;
import com.agent.learn.chapter4.agent.ReflectionAgent;
import com.agent.learn.chapter4.agent.AgentExecutionResult;
import com.agent.learn.chapter4.exception.AgentException;
import com.agent.learn.chapter4.web.dto.AgentAnswerData;
import com.agent.learn.chapter4.web.dto.ApiResponse;
import com.agent.learn.chapter4.web.dto.PlanAndSolveRequest;
import com.agent.learn.chapter4.web.dto.ReActRequest;
import com.agent.learn.chapter4.web.dto.ReflectionRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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
    public ApiResponse<AgentAnswerData> react(@Valid @RequestBody ReActRequest req) {
        return toApiResponse(reActAgent.run(req.question()));
    }

    @PostMapping("/reflection")
    public ApiResponse<AgentAnswerData> reflection(@Valid @RequestBody ReflectionRequest req) {
        return toApiResponse(reflectionAgent.run(req.task()));
    }

    @PostMapping("/plan-and-solve")
    public ApiResponse<AgentAnswerData> planAndSolve(@Valid @RequestBody PlanAndSolveRequest req) {
        return toApiResponse(planAndSolveAgent.run(req.question()));
    }

    private ApiResponse<AgentAnswerData> toApiResponse(AgentExecutionResult result) {
        if (!result.success()) {
            throw new AgentException(HttpStatus.BAD_GATEWAY, result.code(), result.message());
        }
        return ApiResponse.success(new AgentAnswerData(result.answer()));
    }
}

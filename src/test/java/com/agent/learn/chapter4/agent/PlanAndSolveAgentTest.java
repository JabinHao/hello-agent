package com.agent.learn.chapter4.agent;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class PlanAndSolveAgentTest {

    private final Planner planner = mock(Planner.class);
    private final Executor executor = mock(Executor.class);
    private final PlanAndSolveAgent agent = new PlanAndSolveAgent(planner, executor);

    @Test
    void runReturnsFailureWhenPlannerProducesNoSteps() {
        given(planner.plan("question")).willReturn(List.of());

        AgentExecutionResult result = agent.run("question");

        assertThat(result.success()).isFalse();
        assertThat(result.code()).isEqualTo("PLAN_GENERATION_FAILED");
        verifyNoInteractions(executor);
    }

    @Test
    void runReturnsSuccessWithExecutorAnswer() {
        List<String> plan = List.of("step 1", "step 2");
        given(planner.plan("question")).willReturn(plan);
        given(executor.execute("question", plan)).willReturn("42");

        AgentExecutionResult result = agent.run("question");

        assertThat(result.success()).isTrue();
        assertThat(result.answer()).isEqualTo("42");
    }
}

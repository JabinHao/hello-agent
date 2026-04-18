package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class PlannerTest {

    private final HelloAgentsLlm llm = mock(HelloAgentsLlm.class);
    private final Planner planner = new Planner(llm);

    @Test
    void planParsesJsonArrayInsideCodeFence() {
        given(llm.think(anyList())).willReturn("""
                ```json
                ["step 1", "step 2"]
                ```
                """);

        List<String> result = planner.plan("solve this");

        assertThat(result).containsExactly("step 1", "step 2");
    }

    @Test
    void planReturnsEmptyListWhenResponseCannotBeParsed() {
        given(llm.think(anyList())).willReturn("not a json array");

        List<String> result = planner.plan("solve this");

        assertThat(result).isEmpty();
    }
}

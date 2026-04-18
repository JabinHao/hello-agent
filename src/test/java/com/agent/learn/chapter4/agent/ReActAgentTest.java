package com.agent.learn.chapter4.agent;

import com.agent.learn.chapter4.llm.HelloAgentsLlm;
import com.agent.learn.chapter4.tool.ToolRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReActAgentTest {

    private final HelloAgentsLlm llm = mock(HelloAgentsLlm.class);
    private final ToolRegistry toolRegistry = mock(ToolRegistry.class);
    private final ReActAgent agent = new ReActAgent(llm, toolRegistry);

    @Test
    void runReturnsSuccessWhenModelFinishesImmediately() {
        given(toolRegistry.describeAvailableTools()).willReturn("- Search: search the web");
        given(llm.think(anyList())).willReturn("""
                Thought: I already know the answer.
                Action: Finish[final answer]
                """);

        AgentExecutionResult result = agent.run("question");

        assertThat(result.success()).isTrue();
        assertThat(result.answer()).isEqualTo("final answer");
    }

    @Test
    void runReturnsFailureWhenActionCannotBeParsed() {
        given(toolRegistry.describeAvailableTools()).willReturn("- Search: search the web");
        given(llm.think(anyList())).willReturn("Thought only");

        AgentExecutionResult result = agent.run("question");

        assertThat(result.success()).isFalse();
        assertThat(result.code()).isEqualTo("ACTION_PARSE_FAILED");
    }

    @Test
    void runUsesToolObservationBeforeFinishing() {
        given(toolRegistry.describeAvailableTools()).willReturn("- Search: search the web");
        given(llm.think(anyList()))
                .willReturn("""
                        Thought: I need search.
                        Action: Search[huawei latest phone]
                        """)
                .willReturn("""
                        Thought: I can answer now.
                        Action: Finish[tool informed answer]
                        """);
        given(toolRegistry.execute("Search", "huawei latest phone"))
                .willReturn("search result");

        AgentExecutionResult result = agent.run("question");

        assertThat(result.success()).isTrue();
        assertThat(result.answer()).isEqualTo("tool informed answer");
        verify(toolRegistry).execute("Search", "huawei latest phone");
    }
}

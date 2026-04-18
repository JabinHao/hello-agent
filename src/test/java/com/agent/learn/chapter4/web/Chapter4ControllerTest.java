package com.agent.learn.chapter4.web;

import com.agent.learn.chapter4.agent.AgentExecutionResult;
import com.agent.learn.chapter4.agent.PlanAndSolveAgent;
import com.agent.learn.chapter4.agent.ReActAgent;
import com.agent.learn.chapter4.agent.ReflectionAgent;
import com.agent.learn.chapter4.exception.AgentException;
import com.agent.learn.chapter4.web.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(Chapter4Controller.class)
@Import(GlobalExceptionHandler.class)
class Chapter4ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReActAgent reActAgent;

    @MockBean
    private ReflectionAgent reflectionAgent;

    @MockBean
    private PlanAndSolveAgent planAndSolveAgent;

    @Test
    void reactReturnsUnifiedSuccessResponse() throws Exception {
        given(reActAgent.run("where is the moon"))
                .willReturn(AgentExecutionResult.success("moon answer"));

        mockMvc.perform(post("/chapter4/react")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "question": "where is the moon"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.code").value("OK"))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.answer").value("moon answer"));
    }

    @Test
    void reactRejectsBlankQuestion() throws Exception {
        mockMvc.perform(post("/chapter4/react")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "question": ""
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    void reflectionMapsAgentExceptionToApiError() throws Exception {
        given(reflectionAgent.run(anyString()))
                .willThrow(new AgentException(
                        HttpStatus.BAD_GATEWAY,
                        "LLM_CALL_FAILED",
                        "model request failed"));

        mockMvc.perform(post("/chapter4/reflection")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                  "task": "write a prime finder"
                                }
                                """))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("LLM_CALL_FAILED"))
                .andExpect(jsonPath("$.message").value("model request failed"));
    }
}

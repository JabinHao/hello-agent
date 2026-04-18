package com.agent.learn.chapter4.web.dto;

import jakarta.validation.constraints.NotBlank;

public record PlanAndSolveRequest(
        @NotBlank(message = "question must not be blank")
        String question
) {
}

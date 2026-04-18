package com.agent.learn.chapter4.web.dto;

import jakarta.validation.constraints.NotBlank;

public record ReflectionRequest(
        @NotBlank(message = "task must not be blank")
        String task
) {
}

package com.agent.learn.chapter4.tool;

import java.util.function.Function;

public record Tool(String name, String description, Function<String, String> function) {
}

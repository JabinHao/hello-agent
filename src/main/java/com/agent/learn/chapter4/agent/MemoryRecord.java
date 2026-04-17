package com.agent.learn.chapter4.agent;

public record MemoryRecord(Type type, String content) {
    public enum Type {
        EXECUTION, REFLECTION
    }
}

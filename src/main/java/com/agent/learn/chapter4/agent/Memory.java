package com.agent.learn.chapter4.agent;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Memory {

    private final List<MemoryRecord> records = new ArrayList<>();

    public void add(MemoryRecord.Type type, String content) {
        records.add(new MemoryRecord(type, content));
        log.info("📝 Memory updated with a new '{}' record.", type);
    }

    public String getTrajectory() {
        StringBuilder sb = new StringBuilder();
        for (MemoryRecord r : records) {
            switch (r.type()) {
                case EXECUTION -> sb.append("--- Previous attempt (code) ---\n")
                        .append(r.content()).append("\n\n");
                case REFLECTION -> sb.append("--- Reviewer feedback ---\n")
                        .append(r.content()).append("\n\n");
            }
        }
        return sb.toString().strip();
    }

    public String getLastExecution() {
        for (int i = records.size() - 1; i >= 0; i--) {
            if (records.get(i).type() == MemoryRecord.Type.EXECUTION) {
                return records.get(i).content();
            }
        }
        return null;
    }
}

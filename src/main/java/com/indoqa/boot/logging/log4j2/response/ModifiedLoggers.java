package com.indoqa.boot.logging.log4j2.response;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class ModifiedLoggers {

    private final ModificationStatus status;
    private Map<String, ModifiedLogger> modified;

    public ModifiedLoggers(ModificationStatus status) {
        this.status = status;
    }

    public static ModifiedLoggers nonModified() {
        return new ModifiedLoggers(ModificationStatus.NON_MODIFIED);
    }

    public static ModifiedLoggers modified() {
        ModifiedLoggers result = new ModifiedLoggers(ModificationStatus.MODIFIED);

        result.setModified(new HashMap<>());

        return result;
    }

    public ModificationStatus getStatus() {
        return status;
    }

    public void add(String loggerName, String originalLevel, String currentLevel, long executionTime, Duration duration,
        String modificationKey) {
        this.modified.put(loggerName, ModifiedLogger.of(originalLevel, currentLevel, executionTime, duration, modificationKey));
    }

    public Map<String, ModifiedLogger> getModified() {
        return modified;
    }

    public void setModified(Map<String, ModifiedLogger> modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        if (ModificationStatus.NON_MODIFIED.equals(status)) {
            return "No modified loggers.";
        }

        StringBuilder responseBuilder = new StringBuilder();

        this.modified.entrySet().forEach(entry -> {
            ModifiedLogger value = entry.getValue();
            responseBuilder.append(entry.getKey());
            responseBuilder.append(" from '");
            responseBuilder.append(value.getOriginalLevel());
            responseBuilder.append("' to '");
            responseBuilder.append(value.getCurrentLevel());
            responseBuilder.append("' until ");
            responseBuilder.append(Instant.ofEpochMilli(value.getExecutionTime()));
            responseBuilder.append(" (in ");
            responseBuilder.append(value.getDuration());
            responseBuilder.append("); \n\tModification-Key: ");
            responseBuilder.append(value.getModificationKey());
            responseBuilder.append("\n");
        });

        return responseBuilder.toString();
    }
}

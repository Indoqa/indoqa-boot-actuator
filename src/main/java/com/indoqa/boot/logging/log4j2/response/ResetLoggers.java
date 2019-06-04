package com.indoqa.boot.logging.log4j2.response;

import static com.indoqa.boot.logging.log4j2.response.ResetStatus.*;

import java.util.HashMap;
import java.util.Map;

public class ResetLoggers {

    private final ResetStatus status;
    private Map<String, String> reset;

    public ResetLoggers(ResetStatus status) {
        this.status = status;
    }

    public static ResetLoggers nonReset() {
        ResetLoggers result = new ResetLoggers(NON_RESET);
        return result;
    }

    public static ResetLoggers reset() {
        ResetLoggers result = new ResetLoggers(RESET);
        result.setReset(new HashMap<>());
        return result;
    }

    public ResetStatus getStatus() {
        return status;
    }

    public Map<String, String> getReset() {
        return reset;
    }

    public void setReset(Map<String, String> reset) {
        this.reset = reset;
    }

    public void add(String logger, String level) {
        this.reset.put(logger, level);
    }

    @Override
    public String toString() {
        if (NON_RESET.equals(status)) {
            return "No loggers to reset.";
        }

        StringBuilder responseBuilder = new StringBuilder();
        if (ResetStatus.RESET.equals(status)) {
            responseBuilder.append("Reset the following loggers:\n");
            this.reset.entrySet().forEach(entry -> {
                responseBuilder.append(entry.getKey());
                responseBuilder.append(" to ");
                responseBuilder.append(entry.getValue());
                responseBuilder.append("\n");
            });
        }

        return responseBuilder.toString();
    }
}

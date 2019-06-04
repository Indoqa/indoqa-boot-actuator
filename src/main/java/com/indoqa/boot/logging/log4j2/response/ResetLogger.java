package com.indoqa.boot.logging.log4j2.response;

public class ResetLogger {

    private ResetStatus status;
    private String logger;
    private String level;

    public static ResetLogger notReset(String logger) {
        ResetLogger result = new ResetLogger();
        result.setStatus(ResetStatus.NON_RESET);
        result.setLogger(logger);
        return result;
    }

    public static ResetLogger reset(String logger, String level) {
        ResetLogger result = new ResetLogger();

        result.setStatus(ResetStatus.RESET);
        result.setLogger(logger);
        result.setLevel(level);

        return result;
    }

    public ResetStatus getStatus() {
        return status;
    }

    public void setStatus(ResetStatus status) {
        this.status = status;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public String toString() {
        if (ResetStatus.NON_RESET.equals(status)) {
            return "Cannot reset logger '" + logger + "'. Logger is not modified.";
        }

        return "Reset logger: '" + logger + "' to " + level;
    }
}

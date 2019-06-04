package com.indoqa.boot.logging.log4j2.response;

public class LogLevelResponse {

    private String logger;
    private String level;
    private final LoggerStatus loggerStatus;

    public LogLevelResponse(LoggerStatus loggerStatus) {
        this.loggerStatus = loggerStatus;
    }

    public static LogLevelResponse modified(String logger, String level) {
        LogLevelResponse result = new LogLevelResponse(LoggerStatus.MODIFIED);
        result.setLogger(logger);
        result.setLevel(level);
        return result;
    }

    public static LogLevelResponse original(String logger, String level) {
        LogLevelResponse result = new LogLevelResponse(LoggerStatus.ORIGINAL);
        result.setLogger(logger);
        result.setLevel(level);
        return result;
    }

    public static LogLevelResponse notExising(String logger) {
        LogLevelResponse result = new LogLevelResponse(LoggerStatus.NON_EXISTING);
        result.setLogger(logger);
        return result;
    }

    public LoggerStatus getLoggerStatus() {
        return loggerStatus;
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
        if (LoggerStatus.NON_EXISTING.equals(loggerStatus)) {
            return logger + ": does not exist.";
        }
        if (LoggerStatus.MODIFIED.equals(loggerStatus)) {
            return logger + " : modified to " + level;
        }
        return logger + " : " + level;
    }
}

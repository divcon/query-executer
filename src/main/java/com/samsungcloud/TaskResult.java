package com.samsungcloud;

public class TaskResult {
    private final boolean success;
    private final String message;

    public TaskResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

package com.ragentek.homeset.core.task;

public class TaskException extends RuntimeException {
    public TaskException() {}

    public TaskException(String detailMessage) {
        super(detailMessage);
    }

    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskException(Throwable cause) {
        super(cause);
    }
}

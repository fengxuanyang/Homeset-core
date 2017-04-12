package com.ragentek.homeset.core.task;

import com.ragentek.homeset.speech.domain.SpeechDomainType;

public class ForegroundTaskInfo {
    /** If set, stop all foreground task in stack and pop them out.
     * Then ,start the new task. */
    public static final int FLAG_CLEAR_ALL = 0x00000001;

    /**
     *  The high priority event will replace low priority event.
     *  The high one will prevent the low one to execution.
     * */
    public static final int PRIORITY_LOWEST = -19;
    public static final int PRIORITY_LOWER = -15;
    public static final int PRIORITY_NOMAL = -10;
    public static final int PRIORITY_HIGHER = -5;
    public static final int PRIORITY_HIGHEST = -1;

    /* Flag value */
    public int flags = 0;

    /* Priority value */
    public int priority = PRIORITY_NOMAL;

    /* Speech domain type */
    public SpeechDomainType[] domainTypes = {SpeechDomainType.NULL};

    /* It must be the subclass of ForegroundTask */
    public Class<?> className;

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        buffer.append("taskClass=").append(className.getSimpleName()).append(',');;
        buffer.append("flags=").append(flags).append(',');
        buffer.append("priority=").append(priority);
        buffer.append(']');
        return buffer.toString();
    }
}

package com.ragentek.homeset.core.task;

public class BackgroundTaskInfo {
    /* The background task will auto started when system boot up */
    public static final int FLAG_START_ON_BOOTUP = 0x00000001;

    /* Flag value */
    public int flags;

    /* It must be the subclass of BackgroundTask */
    public Class<?> className;

    @Override
    public String toString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append('[');
        buffer.append("taskClass=").append(className.getSimpleName()).append(',');;
        buffer.append("flags=").append(flags);
        buffer.append(']');
        return buffer.toString();
    }
}

package com.ragentek.homeset.core.task.event;

public class TaskEvent {

    /** Task event type */
    public enum TYPE {
        NULL,
        TOUCH,
        SPEECH,
    }
    private TYPE type = TYPE.NULL;

    /** The data is the class of SpeechBaseDomain if type is TYPE.SPEECH. */
    private Object data;

    public TaskEvent(TYPE type, Object data) {
        this.type = type;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public TYPE getType() {
        return type;
    }

    @Override
    public String toString() {
        String result = "[type=" + type.name() + ", data=" + data + "]";
        return result;
    }
}

package com.ragentek.homeset.audiocenter;


public class MediaServiceInitEvent {
    boolean bInitComplete = false;

    public MediaServiceInitEvent(boolean bInitComplete) {
        this.bInitComplete = bInitComplete;
    }

    @Override
    public String toString() {
        return "MediaServiceInitEvent{" +
                "bInitComplete=" + bInitComplete +
                '}';
    }
}

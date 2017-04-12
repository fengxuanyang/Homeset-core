package com.ragentek.homeset.core.base;

import android.content.Context;

public abstract class Engine {
    private String mName = null;

    /**
     * Construct function.
     * @param name engine name.
     */
    public Engine(String name) {
        mName = name;
    }

    /**
     * Get engine name.
     */
    public String getName() {
        return mName;
    }

    /**
     * It will be Called when engine initialized.
     */
    public interface InitListener {
        void onInit(Engine engine, boolean success);
    }

    /**
     * Initialize engine.
     * @param listener {@link InitListener}
     */
    abstract public void init(InitListener listener);

    /**
     * Release engine.
     */
    abstract public void release();
}

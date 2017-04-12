package com.ragentek.homeset.core.base;

import android.content.Context;

import com.ragentek.homeset.core.base.push.PushEngine;
import com.ragentek.homeset.core.utils.LogUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Homeset base engine context.
 */
public class EngineManager {
    private static final String TAG = EngineManager.class.getSimpleName();
    private static final boolean DEBUG = true;

    public static final String ENGINE_SPEECH = "speech";
    public static final String ENGINE_PUSH = "push";

    private Context mContext;
    private EngineInitListener mEngineInitListener;

    private InitListener mInitListener;
    private boolean mIsReady = false;

    public class EngineRecorder {
        public boolean isResponded;
        public boolean isReady;
        public Engine engine;
    }
    private HashMap<String, EngineRecorder> mEngineMap = new HashMap<String, EngineRecorder>();

    public interface InitListener {
        void onInit();
    }

    public EngineManager(Context context) {
        mContext = context;
        mEngineInitListener = new EngineInitListener();
    }

    public Context getContext() {
        return mContext;
    }

    /**
     * Init base context.
     * @param listener It will called when all engine initialized.
     */
    public void init(InitListener listener) {
        mInitListener = listener;

        registerEngines();
        initEngines();
    }

    private void registerEngines() {
        // TODO: add more engines here ...
        registerSpeechEngine();
        registerPushEngine();
    }

    private void registerSpeechEngine() {
        SpeechEngine speechEngine = new SpeechEngine(mContext, ENGINE_SPEECH);
        mEngineMap.put(speechEngine.getName(), createEngineRecorder(speechEngine));
    }

    private void registerPushEngine() {
        PushEngine pushEngine = new PushEngine(mContext, ENGINE_PUSH);
        mEngineMap.put(pushEngine.getName(), createEngineRecorder(pushEngine));
    }

    private EngineRecorder createEngineRecorder(Engine engine) {
        EngineRecorder recorder = new EngineRecorder();
        recorder.isResponded = false;
        recorder.isReady = false;
        recorder.engine = engine;

        return  recorder;
    }

    private void initEngines() {
        Iterator iterator = mEngineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, EngineRecorder> pair = (Map.Entry<String, EngineRecorder>) iterator.next();
            EngineRecorder recorder = pair.getValue();

            LogUtils.event(TAG, recorder.engine.getName() + " begin init...");
            recorder.engine.init(mEngineInitListener);
        }

        checkAllEngineInit();
    }

    class EngineInitListener implements Engine.InitListener {

        @Override
        public void onInit(Engine engine, boolean success) {
            printLog(engine.getName() + " init success=" + success);

            EngineRecorder recorder = mEngineMap.get(engine.getName());
            recorder.isResponded = true;
            recorder.isReady = success;

            checkAllEngineInit();
        }
    }

    private void checkAllEngineInit() {
        if (!isAllEngineResponded()) {
            return;
        }

        boolean success = isAllEngineSucceed();
        if (!success) {
            dump();
            throw new RuntimeException("EngineManager init fail!");
        }

        mIsReady = true;
        mInitListener.onInit();
    }

    private boolean isAllEngineResponded() {
        Iterator iterator = mEngineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry<String, EngineRecorder>) iterator.next();
            EngineRecorder recorder = (EngineRecorder) entry.getValue();

            if (!recorder.isResponded) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllEngineSucceed() {
        Iterator iterator = mEngineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry<String, EngineRecorder>) iterator.next();
            EngineRecorder recorder = (EngineRecorder) entry.getValue();

            if (!recorder.isReady) {
                return false;
            }
        }

        return true;
    }

    public void dump() {
        Iterator iterator = mEngineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry<String, EngineRecorder>) iterator.next();
            String engineString = entryToEngineString(entry);
            LogUtils.e(TAG, engineString);
        }
    }

    private String entryToEngineString(Map.Entry entry) {
        String engineName = (String) entry.getKey();
        EngineRecorder recorder = (EngineRecorder) entry.getValue();

        StringBuilder builder = new StringBuilder();
        builder.append('[');
        builder.append("name=").append(engineName).append(',');
        builder.append("isResponded=").append(recorder.isResponded).append(',');
        builder.append("isReady=").append(recorder.isReady);
        builder.append(']');

        return builder.toString();
    }

    /**
     * Get Specified engine by name.
     * @param name engine name, please refer to Engine names, for example ENGINE_TCPIP, ENGINE_SPEECH and so on.
     * @return Engine object.
     */
    public Engine getEngine(String name) {
        return mEngineMap.get(name).engine;
    }

    public boolean isReady() {
        return mIsReady;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /**
     * Release all engines.
     */
    public void release() {
        mInitListener = null;

        Iterator iterator = mEngineMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, EngineRecorder> pair = (Map.Entry<String, EngineRecorder>) iterator.next();
            EngineRecorder recorder = pair.getValue();
            recorder.engine.release();

            LogUtils.event(TAG, recorder.engine.getName() + " engine released");
        }

        mIsReady = false;
    }

    private void printLog(String message) {
        if (DEBUG) {
            LogUtils.d(TAG, message);
        }
    }
}

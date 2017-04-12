package com.ragentek.homeset.core.task.foreground;

import android.content.Context;
import android.os.RemoteException;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.base.SpeechEngine;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.ISpeechSynthesizerClient;
import com.ragentek.homeset.speech.ISynthesizerListener;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainType;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;
import com.ragentek.homeset.speech.domain.SpeechWeatherDomain;

import java.util.ArrayList;

public class TellWeatherTask extends ForegroundTask {
    private static final String TAG = TellWeatherTask.class.getSimpleName();

    private Context mContext;
    private ISpeechSynthesizerClient mSynthesizerClient;

    public TellWeatherTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);

        mContext = baseContext.getAndroidContext();

        SpeechEngine speechEngine = (SpeechEngine) baseContext.getEngine(EngineManager.ENGINE_SPEECH);
        mSynthesizerClient = speechEngine.getSynthesizerClient();
    }

    @Override
    public void onCreate() {

    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        // TODO: use for test
        if (event!=null && event.getType() != TaskEvent.TYPE.SPEECH) {
            finish();
            return;
        }

        SpeechBaseDomain baseDomain = (SpeechBaseDomain) event.getData();
        if (SpeechDomainType.WEATHER != SpeechDomainUtils.getDomainType(baseDomain)) {
            finish();
            return;
        }

        doTellWeather((SpeechWeatherDomain) baseDomain);
    }

    private void doTellWeather(SpeechWeatherDomain weatherDomain) {
        String city = getCity(weatherDomain);
        String day = getDay(weatherDomain);
        String weather = getWeather(weatherDomain);

        if (city ==null) {
            speakString(mContext.getString(R.string.weather_not_found_city));
            return;
        }

        if (day == null || weather == null) {
            speakString(mContext.getString(R.string.weather_not_found_day));
            return;
        }

        String result = city + ", " + day + ", " + weather;
        speakString(result);
        LogUtils.d(TAG, result);
    }

    private String getCity(SpeechWeatherDomain weatherDomain) {
        if (weatherDomain.semantic.slots.location.city.isEmpty()) {
            return null;
        }

        SpeechWeatherDomain.Result result = weatherDomain.data.result.get(0);
        if (result.city.isEmpty()) {
            return null;
        }

        return result.city;
    }

    private String getDay(SpeechWeatherDomain weatherDomain) {
        String date = weatherDomain.semantic.slots.datetime.date;
        String dateOrig = weatherDomain.semantic.slots.datetime.dateOrig;
        String timeOrig = weatherDomain.semantic.slots.datetime.timeOrig;

        if (date.isEmpty() || date.equals(SpeechWeatherDomain.CURRENT_DAY)) {
           return mContext.getResources().getString(R.string.today);
        }

        if (timeOrig.isEmpty()) {
            timeOrig = mContext.getString(R.string.day_night);
        }

        return dateOrig + timeOrig;
    }

    private String getWeather(SpeechWeatherDomain weatherDomain) {
        try {
            String date = weatherDomain.semantic.slots.datetime.date;
            ArrayList<SpeechWeatherDomain.Result> results =  weatherDomain.data.result;

            if (date.isEmpty() || date.equals(SpeechWeatherDomain.CURRENT_DAY)) {
                return compositeWeather(results.get(0));
            } else {
                for (SpeechWeatherDomain.Result item: results) {
                    if (item.date.equals(date)) {
                        return compositeWeather(item);
                    }
                }
            }
        } catch (Exception e) {}

        return null;
    }

    private String compositeWeather(SpeechWeatherDomain.Result result) {
        return result.weather + ", " + result.tempRange + ", " + result.wind;
    }


    private void speakString(String text) {
        try {
            mSynthesizerClient.startSpeak(text, new SynthesizerListener());
        } catch (RemoteException e) {
            finish();
        }
    }

    private class SynthesizerListener extends ISynthesizerListener.Stub {

        @Override
        public void onSpeakBegin() throws RemoteException {}

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) throws RemoteException {}

        @Override
        public void onCompleted(int errorCode, String message) throws RemoteException {
            finish();
        }
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    protected void onStop() {
        try {
            if (mSynthesizerClient.isSpeaking()) {
                mSynthesizerClient.stopSpeak();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

    }
}

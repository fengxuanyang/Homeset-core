package com.ragentek.homeset.core.task.foreground;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.ragentek.homeset.audiocenter.AudioPlayActivity;
import com.ragentek.homeset.audiocenter.AudioRecommendActivity;
import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.base.EngineManager;
import com.ragentek.homeset.core.base.push.PushEngine;
import com.ragentek.homeset.core.task.BaseContext;
import com.ragentek.homeset.core.task.ForegroundTask;
import com.ragentek.homeset.core.task.event.BackHomeEvent;
import com.ragentek.homeset.core.task.event.PushAudioFavEvent;
import com.ragentek.homeset.core.task.event.TaskEvent;
import com.ragentek.homeset.core.utils.LogUtils;
import com.ragentek.homeset.speech.domain.SpeechBaseDomain;
import com.ragentek.homeset.speech.domain.SpeechDomainType;
import com.ragentek.homeset.speech.domain.SpeechDomainUtils;
import com.ragentek.protocol.commons.audio.FavoriteVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.constants.MessageType;
import com.ragentek.protocol.messages.tcp.PushMessagePack;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class TingTask extends ForegroundTask {
    private static final String TAG = "TingTask";

    private BaseContext mBaseContext;
    private Context mContext;
    private boolean mRegListener = false;
    private PushEngine mPushEngine;
    private MessageReceiveListener mMessageReceiveListener;

    public TingTask(BaseContext baseContext, OnFinishListener listener) {
        super(baseContext, listener);
        mBaseContext = baseContext;
        mContext = baseContext.getAndroidContext();
        mPushEngine = (PushEngine) mBaseContext.getEngine(EngineManager.ENGINE_PUSH);
        mMessageReceiveListener = new MessageReceiveListener();
    }

    @Override
    public void onCreate() {
        LogUtils.d(TAG, "onCreate");
    }

    @Override
    protected void onStartCommand(TaskEvent event) {
        LogUtils.d(TAG, "onStartCommand, event=" + event + " mRegListener=" + mRegListener);
        if (!mRegListener) {
            EventBus.getDefault().register(this);
            registerPushMessage();
            mRegListener = true;
        }
        processStartCommand(event);
    }

    @Override
    public boolean canPause() {
        return true;
    }


    @Override
    protected void onPause() {
        LogUtils.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        LogUtils.d(TAG, "onResume");
    }

    @Override
    protected void onStop() {
        LogUtils.d(TAG, "onStop, mRegListener=" + mRegListener);
        if (mRegListener) {
            EventBus.getDefault().unregister(this);
            unRegisterPushMessage();
            mRegListener = false;
        }
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG, "onDestroy");
    }

    private void processStartCommand(TaskEvent event) {
        LogUtils.d(TAG, "processStartCommand, event=" + event);

        if (event != null) {
            TaskEvent.TYPE eventType = event.getType();
            Object eventData = event.getData();

            if (eventType == TaskEvent.TYPE.TOUCH) {
                if (eventData != null && eventData instanceof CategoryDetail) {
                    CategoryDetail cat = (CategoryDetail) eventData;
                    Intent intent = new Intent();
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (cat.getId() == -1) {
                        intent.setClass(mContext, AudioPlayActivity.class);
                        TagDetail tag = new TagDetail();
                        tag.setName(mContext.getResources().getString(R.string.my_favoriate));
                        tag.setCategoryID(Constants.CATEGORY_FAV);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.CATEGORY_TAG, tag);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    } else {
                        intent.setClass(mContext, AudioRecommendActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(Constants.CATEGORY, cat);
                        intent.putExtras(bundle);
                        mContext.startActivity(intent);
                    }
                }
            } else if (eventType == TaskEvent.TYPE.SPEECH) {
                SpeechBaseDomain speechDomain = (SpeechBaseDomain) event.getData();
                receiveSpeechEvent(speechDomain);
            }
        }
    }

    @Override
    protected boolean onSpeechEvent(SpeechBaseDomain speechDomain) {
        receiveSpeechEvent(speechDomain);
        return true;
    }

    private void receiveSpeechEvent(SpeechBaseDomain speechDomain) {
        LogUtils.d(TAG, "receiveSpeechEvent, speechDomain=" + speechDomain);

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setClass(mContext, AudioPlayActivity.class);
        Bundle bundle = new Bundle();

        int categoryId = -1;

        SpeechDomainType type = SpeechDomainUtils.getDomainType(speechDomain);

        if (type == SpeechDomainType.MUSIC) {
            categoryId = Category.ID.MUSIC;
        } else if (type == SpeechDomainType.HOMESET_CROSSTALK) {
            categoryId = Category.ID.CROSS_TALK;
        } else if (type == SpeechDomainType.HOMESET_FINACE) {
            categoryId = Category.ID.STOCK;
        } else if (type == SpeechDomainType.HOMESET_HEALTH) {
            categoryId = Category.ID.HEALTH;
        } else if (type == SpeechDomainType.HOMESET_HISTORY) {
            categoryId = Category.ID.HISTORY;
        } else if (type == SpeechDomainType.HOMESET_OPERA) {
            categoryId = Category.ID.CHINA_ART;
        } else if (type == SpeechDomainType.HOMESET_RADIO) {
            categoryId = Category.ID.RADIO;
        } else if (type == SpeechDomainType.HOMESET_STROY) {
            categoryId = Category.ID.STORYTELLING;
        } else if (type == SpeechDomainType.HOMESET_FAVORITE) {
            categoryId = -1;
        }
        LogUtils.d(TAG, "receiveSpeechEvent, categoryId=" + categoryId);
        TagDetail tag = new TagDetail();
        tag.setCategoryID(categoryId);
        bundle.putSerializable(Constants.CATEGORY_TAG, tag);
        bundle.putString(Constants.TASKEVENT_TYPE, Constants.TASKEVENT_TYPE_SPEECH);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Subscribe
    public void onEventBackHome(BackHomeEvent backhomeevent) {
        LogUtils.d(TAG, "onEventBackHome =" + backhomeevent);
        onStop();
        finish();
    }

    private void registerPushMessage() {
        mPushEngine.registerMessageListener(MessageType.AUDIO_FAVORITE_ADD, mMessageReceiveListener);
        mPushEngine.registerMessageListener(MessageType.AUDIO_FAVORITE_DELETE, mMessageReceiveListener);
    }

    private void unRegisterPushMessage() {
        mPushEngine.unregiterMessageListener(MessageType.AUDIO_FAVORITE_ADD);
        mPushEngine.unregiterMessageListener(MessageType.AUDIO_FAVORITE_DELETE);
    }

    private class MessageReceiveListener implements PushEngine.OnMessageReceiveListener {
        @Override
        public void onReceived(PushMessagePack messagePack) {
            LogUtils.d(TAG, "MessageReceiveListener, messagePack=" + messagePack);
            if (messagePack != null) {
                int msgType = messagePack.getMsgType();
                switch (msgType) {
                    case MessageType.AUDIO_FAVORITE_ADD:
                    case MessageType.AUDIO_FAVORITE_DELETE:
                        receiveAudioFavouritePushMsg(messagePack);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    private void receiveAudioFavouritePushMsg(PushMessagePack messagePack) {
        FavoriteVO favoriteVO = JSON.parseObject(messagePack.getMsg(), FavoriteVO.class);
        LogUtils.d(TAG, "receiveAudioFavouritePushMsg, favoriteVO=" + favoriteVO);

        PushAudioFavEvent pushAudioFavEvent = new PushAudioFavEvent();
        if (favoriteVO.getId() == null) {
            pushAudioFavEvent.setAction(0);
        } else {
            pushAudioFavEvent.setAction(1);
        }
        pushAudioFavEvent.setFavoriteVO(favoriteVO);
        EventBus.getDefault().post(pushAudioFavEvent);
    }
}

package com.ragentek.homeset.speech.test;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.speech.ISpeechManager;
import com.ragentek.homeset.speech.ISpeechSynthesizerClient;
import com.ragentek.homeset.speech.ISynthesizerListener;
import com.ragentek.homeset.speech.SpeechService;

public class SynthesizerTestFragment extends Fragment {
    private Activity mActivity;
    private TtsListener mTtsListener;

    private TextView mLogTextView;
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mPauseBtn;
    private Button mResumeBtn;
    private Button mStateBtn;

    private ISpeechSynthesizerClient mSynthesizerClient;
    private SpeechServiceConnection mSpeechServiceConnection = new SpeechServiceConnection();

    class SpeechServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            printLog("onServiceConnected");

            ISpeechManager speechManager = ISpeechManager.Stub.asInterface(service);
            try {
                mSynthesizerClient = speechManager.getSynthesizerClient();
                enableView();
            } catch (RemoteException e) {
                printLog("connect error=" + e.toString());
                disableView();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
            printLog("onServiceDisconnected");
            disableView();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mTtsListener = new TtsListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_synthesizer, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLogTextView = (TextView) view.findViewById(R.id.textLog);
        mLogTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        mStartBtn = (Button) view.findViewById(R.id.btnStartSpeak);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    String peakText = mActivity.getResources().getString(R.string.speak_text);
                    mSynthesizerClient.startSpeak(peakText, mTtsListener);
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mStopBtn = (Button) view.findViewById(R.id.btnStopSpeak);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mSynthesizerClient.stopSpeak();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mPauseBtn = (Button) view.findViewById(R.id.btnPauseSpeak);
        mPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSynthesizerClient.pauseSpeak();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mResumeBtn = (Button) view.findViewById(R.id.btnResumeSpeak);
        mResumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSynthesizerClient.resumeSpeak();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mStateBtn = (Button) view.findViewById(R.id.btnStateSpeak);
        mStateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    boolean isSpeaking = mSynthesizerClient.isSpeaking();
                    printLog("is speaking=" + isSpeaking);
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        disableView();
    }

    private class TtsListener extends ISynthesizerListener.Stub {

        @Override
        public void onSpeakBegin() throws RemoteException {
            printLog("onSpeakBegin");
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) throws RemoteException {
//            printLog("onSpeakProgress percent=" + percent + ", beginPos=" + beginPos + ", endPos=" + endPos);
        }

        @Override
        public void onCompleted(int errorCode, String message) throws RemoteException {
            printLog("onCompleted, errorCode=" + errorCode + ", message=" + message);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = getActivity();

        Intent intent = new Intent(mActivity, SpeechService.class);
        mActivity.bindService(intent, mSpeechServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        mActivity.unbindService(mSpeechServiceConnection);
    }

    private void enableView() {
        mStartBtn.setEnabled(true);
        mStopBtn.setEnabled(true);
        mPauseBtn.setEnabled(true);
        mResumeBtn.setEnabled(true);
        mStateBtn.setEnabled(true);
    }

    private void disableView() {
        mStartBtn.setEnabled(false);
        mStopBtn.setEnabled(false);
        mPauseBtn.setEnabled(false);
        mResumeBtn.setEnabled(false);
        mStateBtn.setEnabled(false);
    }

    private void printLog(final String message) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLogTextView.append("\n");
                mLogTextView.append(message);
                scrollToEnd();
            }
        });
    }

    private void scrollToEnd() {
        int offset = mLogTextView.getLineCount() * mLogTextView.getLineHeight();
        int textHeight = mLogTextView.getHeight();

        if (offset > textHeight) {
            mLogTextView.scrollTo(0, offset - textHeight);
        }
    }

}

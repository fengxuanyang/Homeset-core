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
import com.ragentek.homeset.speech.ISpeechWakeuperClient;
import com.ragentek.homeset.speech.IWakeuperListener;
import com.ragentek.homeset.speech.SpeechService;

public class VoiceWakeuperTestFragment extends Fragment {
    private Activity mActivity;
    private VwListener mVwListener;

    private TextView mLogTextView;
    private Button mStartBtn;
    private Button mStopBtn;

    private ISpeechWakeuperClient mWakeuperClient;
    private SpeechServiceConnection mSpeechServiceConnection = new SpeechServiceConnection();

    class SpeechServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            printLog("onServiceConnected");

            ISpeechManager speechManager = ISpeechManager.Stub.asInterface(service);
            try {
                mWakeuperClient = speechManager.getWakeuperClient();
            } catch (RemoteException e) {
                printLog("connect error=" + e.toString());
                disableView();
            }
            enableView();
        }

        public void onServiceDisconnected(ComponentName name) {
            printLog("onServiceDisconnected");
            disableView();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mVwListener = new VwListener();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wakeuper, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLogTextView = (TextView) view.findViewById(R.id.textLog);
        mLogTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        mStartBtn = (Button) view.findViewById(R.id.btnStartListening);
        mStartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    mWakeuperClient.startListening(mVwListener);
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });


        mStopBtn = (Button) view.findViewById(R.id.btnStopListening);
        mStopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mWakeuperClient.stopListening();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });
    }

    private class VwListener extends IWakeuperListener.Stub {

        @Override
        public void onResult(String result) throws RemoteException {
            printLog("onResult, result=" + result);
        }

        @Override
        public void onError(int errorCode, String message) throws RemoteException {
            printLog("onError, errorCode=" + errorCode + ", message=" + message);
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
    }

    private void disableView() {
        mStartBtn.setEnabled(false);
        mStopBtn.setEnabled(false);
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

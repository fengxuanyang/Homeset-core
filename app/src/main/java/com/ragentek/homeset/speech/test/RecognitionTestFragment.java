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
import com.ragentek.homeset.speech.IRecognitionListener;
import com.ragentek.homeset.speech.ISpeechManager;
import com.ragentek.homeset.speech.ISpeechRecognitionClient;
import com.ragentek.homeset.speech.SpeechService;

public class RecognitionTestFragment extends Fragment {
    private Activity mActivity;
    private AsrListener mAsrListener;

    private TextView mLogTextView;
    private Button mStartRecognitionButton;
    private Button mStopRecognitionButton;
    private Button mCancelRecognitionButton;

    private ISpeechRecognitionClient mRecognitionClient;
    private SpeechServiceConnection mSpeechServiceConnection = new SpeechServiceConnection();

    class SpeechServiceConnection implements ServiceConnection {
        public void onServiceConnected(ComponentName name, IBinder service) {
            printLog("onServiceConnected");

            ISpeechManager speechManager = ISpeechManager.Stub.asInterface(service);
            try {
                mRecognitionClient = speechManager.getRecognitionClient();
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

        mAsrListener = new AsrListener();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recognition, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mLogTextView = (TextView) view.findViewById(R.id.textLog);
        mLogTextView.setMovementMethod(ScrollingMovementMethod.getInstance());

        mStartRecognitionButton = (Button) view.findViewById(R.id.btnStartRecognize);
        mStartRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mRecognitionClient.startRecognize(mAsrListener);
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mStopRecognitionButton = (Button) view.findViewById(R.id.btnStopRecognize);
        mStopRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mRecognitionClient.stopRecognize();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        mCancelRecognitionButton = (Button) view.findViewById(R.id.btnCancelRecognize);
        mCancelRecognitionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mRecognitionClient.cancelRecognize();
                } catch (RemoteException e) {
                    printLog(e.toString());
                }
            }
        });

        disableView();
    }

    private class AsrListener extends IRecognitionListener.Stub {

        @Override
        public void onBeginOfSpeech() throws RemoteException {
            printLog("onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() throws RemoteException {
            printLog("onEndOfSpeech");
        }

        @Override
        public void onVolumeChanged(int value) throws RemoteException {
            printLog("onVolumeChanged, value=" + value);
        }

        @Override
        public void onSpeechEnd() throws RemoteException {
            printLog("onSpeechEnd");
        }

        @Override
        public void onRecordEnd() throws RemoteException {
            printLog("onRecordEnd");
        }

        @Override
        public void onResult(String result) throws RemoteException {
            printLog("onResult result, result=" + result);
        }

        @Override
        public void onError(int errCode, String message) throws RemoteException {
            printLog("errCode=" + errCode + ", message=" + message);
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
        mStartRecognitionButton.setEnabled(true);
        mStopRecognitionButton.setEnabled(true);
        mCancelRecognitionButton.setEnabled(true);
    }

    private void disableView() {
        mStartRecognitionButton.setEnabled(false);
        mStopRecognitionButton.setEnabled(false);
        mCancelRecognitionButton.setEnabled(false);
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

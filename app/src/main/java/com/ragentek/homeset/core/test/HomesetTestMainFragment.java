package com.ragentek.homeset.core.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ragentek.homeset.core.HomesetService;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.speech.test.SpeechTestActivity;

import java.util.ArrayList;
import java.util.List;

public class HomesetTestMainFragment extends ListFragment {

    private static final int CASE_START_SERVICE = 1;
    private static final int CASE_STOP_SERVICE = 2;
    private static final int CASE_DUMP_SERVICE = 3;
    private static final int CASE_START_RECOGNITION = 4;
    private static final int CASE_SHOW_FORE_TASK = 5;
    private static final int CASE_SHOW_BACK_TASK = 6;
    private static final int CASE_SHOW_SPEECH_TEST = 7;

    private Activity mActivity;

    private static final SampleConfig[] samplesConfig = new SampleConfig[] {
            new SampleConfig(CASE_START_SERVICE, "Start service", null),
            new SampleConfig(CASE_STOP_SERVICE, "Stop service", null),
            new SampleConfig(CASE_DUMP_SERVICE, "Dump service", null),
            new SampleConfig(CASE_START_RECOGNITION, "Start speech recognition", null),
            new SampleConfig(CASE_SHOW_FORE_TASK, "Show foreground task", ForeTaskListFragment.class),
            new SampleConfig(CASE_SHOW_BACK_TASK, "Show background task", BackTaskListFragment.class),
            new SampleConfig(CASE_SHOW_SPEECH_TEST, "Show speech test", null),
    };

    private static class SampleConfig {
        final int caseId;
        final String title;
        final Class targetClass;

        SampleConfig(int caseId, String title, Class targetClass) {
            this.caseId = caseId;
            this.targetClass = targetClass;
            this.title = title;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = getActivity();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, getTitlesList()));
        view.setBackgroundColor(Color.WHITE);
    }

    private List<String> getTitlesList() {
        List<String> titles = new ArrayList<String>();
        for (SampleConfig config : samplesConfig) {
            titles.add(config.title);
        }
        return titles;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        if (position < 0 && position >= samplesConfig.length) {
            return;
        }

        int caseId = samplesConfig[position].caseId;
        switch (caseId) {
            case CASE_START_SERVICE:
                starService();
                break;
            case CASE_STOP_SERVICE:
                stopService();
                break;
            case CASE_DUMP_SERVICE:
                dumpService();
                break;
            case CASE_START_RECOGNITION:
                startRecognition();
                break;
            case CASE_SHOW_FORE_TASK:
            case CASE_SHOW_BACK_TASK:
                startFragment(samplesConfig[position].targetClass);
                break;
            case CASE_SHOW_SPEECH_TEST:
                showSpeechTest();
                break;
        }
    }

    private void starService() {
        Intent intent = new Intent(mActivity, HomesetService.class);
        intent.setAction(HomesetService.ACTION_START);
        mActivity.startService(intent);
    }

    private void stopService() {
        Intent intent = new Intent(mActivity, HomesetService.class);
        intent.setAction(HomesetService.ACTION_STOP);
        mActivity.startService(intent);
    }

    private void dumpService() {
        Intent intent = new Intent(mActivity, HomesetService.class);
        intent.setAction(HomesetService.ACTION_DUMP);
        mActivity.startService(intent);
    }

    private void startRecognition() {
//        Intent intent = new Intent(mActivity, HomesetService.class);
//        intent.setAction(HomesetService.ACTION_DEBUG);
//        intent.putExtra(HomesetService.DEBUG_INT_EXTRA_CASE, HomesetService.CASE_START_RECOGNITION);
//        mActivity.startService(intent);
//
        Intent intent = new Intent();
        intent.setAction("ragentek.intent.action.START_RECOGNITION");
        mActivity.sendBroadcast(intent);
    }

    private void startFragment(Class<?> fragmentClass) {
        try {

            Fragment newFragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fm = mActivity.getFragmentManager();
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(R.id.fragment_content, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();

        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void showSpeechTest() {
        Intent intent = new Intent(mActivity, SpeechTestActivity.class);
        mActivity.startActivity(intent);
    }

}

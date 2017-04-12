package com.ragentek.homeset.speech.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ragentek.homeset.core.R;

import java.util.ArrayList;
import java.util.List;

public class SpeechTestFragment extends ListFragment {
    private static final int CASE_RECOGNITION = 1;
    private static final int CASE_SYNTHESIZER = 2;
    private static final int CASE_VOICEWAKEUP = 3;


    private Activity mActivity;

    private static final SampleConfig[] samplesConfig = new SampleConfig[] {
            new SampleConfig(CASE_RECOGNITION, "Recognize", RecognitionTestFragment.class),
            new SampleConfig(CASE_SYNTHESIZER, "Synthesizer", SynthesizerTestFragment.class),
            new SampleConfig(CASE_VOICEWAKEUP, "Voice wakeup", VoiceWakeuperTestFragment.class),
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

        startFragment(samplesConfig[position].targetClass);
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
}

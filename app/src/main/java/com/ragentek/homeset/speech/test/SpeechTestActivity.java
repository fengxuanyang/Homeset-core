package com.ragentek.homeset.speech.test;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.ragentek.homeset.core.R;

public class SpeechTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFragment(SpeechTestFragment.class);
    }

    private void startFragment(Class<?> fragmentClass) {
        try {

            Fragment newFragment = (Fragment) fragmentClass.newInstance();
            FragmentManager fm = this.getFragmentManager();
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

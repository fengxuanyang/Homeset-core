package com.ragentek.homeset.core.test;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ragentek.homeset.core.HomesetService;
import com.ragentek.homeset.core.task.foreground.LauncherTask;
import com.ragentek.homeset.core.task.foreground.TellWeatherTask;
import com.ragentek.homeset.core.task.foreground.TingTask;
import com.ragentek.homeset.core.task.foreground.WechatTask;

import java.util.ArrayList;
import java.util.List;

public class ForeTaskListFragment extends ListFragment {
    private static final int LAUNCHER_TASK = 1;
    private static final int TING_TASK = 2;
    private static final int WECHAT_TASK= 3;
    private static final int TELLWEATHER_TASK= 3;

    private Activity mActivity;

    private static final SampleConfig[] samplesConfig = new SampleConfig[] {
            new SampleConfig(LAUNCHER_TASK, "LauncherTask", LauncherTask.class),
            new SampleConfig(TING_TASK, "TingTask", TingTask.class),
            new SampleConfig(WECHAT_TASK, "WechatTask", WechatTask.class),
            new SampleConfig(TELLWEATHER_TASK, "TellWeatherTask", TellWeatherTask.class),
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

        String className = samplesConfig[position].targetClass.getName();

        Intent intent = new Intent(mActivity, HomesetService.class);
        intent.setAction(HomesetService.ACTION_DEBUG);
        intent.putExtra(HomesetService.DEBUG_INT_EXTRA_CASE, HomesetService.CASE_START_FORE_TASK);
        intent.putExtra(HomesetService.DEBUG_STRING_EXTRA_TASK_NAME, className);
        mActivity.startService(intent);
    }
}

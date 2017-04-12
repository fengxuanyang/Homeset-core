package com.ragentek.homeset.ui.launcher.fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.ui.launcher.adapter.CategoryAdapter;
import com.ragentek.homeset.ui.launcher.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.ui.launcher.adapter.SpacesItemDecoration;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.homeset.ui.utils.SettingActivity;
import com.ragentek.protocol.commons.audio.CategoryVO;

import java.util.ArrayList;
import java.util.List;

public class KanCategoryFragment extends Fragment {
    private static final String TAG = "KanCategoryFragment";

    private static final int CATEGORY_KAN_MOVIE = 1001;
    private static final int CATEGORY_KAN_DANCE = 1002;
    private static final int CATEGORY_KAN_SONG = 1003;
    private static final int CATEGORY_KAN_FOOD = 1004;
    private static final int CATEGORY_KAN_LIFE = 1005;
    private static final int CATEGORY_KAN_PIC = 1006;
    private static final int CATEGORY_KAN_SET = 1007;

    private CategoryAdapter mVideoAdapter;
    private RecyclerView mVideoRV;
    private List<CategoryDetail> mVideoCategorys = new ArrayList<CategoryDetail>();
    private SpacesItemDecoration mSpacesItemDecoration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kancategory, container, false);

        mVideoRV = (RecyclerView) view.findViewById(R.id.rv_video);
        mSpacesItemDecoration = new SpacesItemDecoration(10);

        LogUtils.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d(TAG, "onCreate: ");
    }

    @Override
    public void onStart() {
        super.onStart();

        initVidoRecycleView();

        LogUtils.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        mVideoRV.removeItemDecoration(mSpacesItemDecoration);

        LogUtils.d(TAG, "onStop");
    }

    private void initVidoRecycleView() {
        mVideoAdapter = new CategoryAdapter(getActivity());
        mVideoAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryDetail categoryDetail = mVideoCategorys.get(position);
                LogUtils.d(TAG, "onItemClick, position=" + position + " category=" + categoryDetail);
                processVideoCategoryClick(categoryDetail);
            }
        });
        mVideoRV.setAdapter(mVideoAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 14);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                CategoryDetail bean = mVideoCategorys.get(position);
                return bean.getSize();

            }
        });
        mVideoRV.setLayoutManager(manager);
        mVideoRV.addItemDecoration(mSpacesItemDecoration);
        mVideoRV.setHasFixedSize(true);

        getVideoCategoryData();
    }

    private void getVideoCategoryData() {
        CategoryVO c;
        CategoryDetail cd;

        mVideoCategorys.clear();

        c = new CategoryVO((long) CATEGORY_KAN_MOVIE, "电影");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_DANCE, "广场舞");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_SONG, "K歌");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_FOOD, "食谱");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_LIFE, "养生");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_PIC, "照片");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        c = new CategoryVO((long) CATEGORY_KAN_SET, "设置");
        cd = decoratorCategory(c);
        mVideoCategorys.add(cd);

        mVideoAdapter.setDatas(mVideoCategorys);
    }

    private CategoryDetail decoratorCategory(CategoryVO catov) {
        CategoryDetail cat = new CategoryDetail();
        cat.setId(catov.getId().intValue());
        cat.setName(catov.getName());
        switch (cat.getId()) {
            case CATEGORY_KAN_MOVIE:
                cat.setIcon(R.drawable.kan_video);
                cat.setSize(4);
                break;
            case CATEGORY_KAN_DANCE:
                cat.setIcon(R.drawable.kan_dance);
                cat.setSize(4);
                break;
            case CATEGORY_KAN_SONG:
                cat.setIcon(R.drawable.kan_song);
                cat.setSize(6);
                break;
            case CATEGORY_KAN_FOOD:
                cat.setIcon(R.drawable.kan_food);
                cat.setSize(3);
                break;
            case CATEGORY_KAN_LIFE:
                cat.setIcon(R.drawable.kan_life);
                cat.setSize(3);
                break;
            case CATEGORY_KAN_PIC:
                cat.setIcon(R.drawable.kan_pic);
                cat.setSize(3);
                break;
            case CATEGORY_KAN_SET:
                cat.setIcon(R.drawable.kan_set);
                cat.setSize(5);
                break;
        }
        return cat;
    }

    private void processVideoCategoryClick(CategoryDetail categoryDetail) {
        LogUtils.d(TAG, "processVideoCategoryClick, categoryDetail=" + categoryDetail);

        Intent intent = new Intent();
        Uri uri;

        switch (categoryDetail.getId()) {
            case CATEGORY_KAN_MOVIE:
                intent.setClassName("com.qiyi.video.pad", "com.qiyi.video.pad.WelcomeActivity");
                break;
            case CATEGORY_KAN_DANCE:
                intent.setClassName("cc.laowantong.gcw", "cc.laowantong.gcw.activity.WelcomeActivity");
                break;
            case CATEGORY_KAN_SONG:
                intent.setClassName("com.tencent.karaoke", "com.tencent.karaoke.module.splash.ui.SplashBaseActivity");
                break;
            case CATEGORY_KAN_FOOD:
                intent.setAction(Intent.ACTION_VIEW);
                uri = Uri.parse("http://v.baidu.com/show/14338.html");
                intent.setData(uri);
                break;
            case CATEGORY_KAN_LIFE:
                intent.setAction(Intent.ACTION_VIEW);
                uri = Uri.parse("http://www.iqiyi.com/a_19rrgubrh1.html");
                intent.setData(uri);
                break;
            case CATEGORY_KAN_PIC:
                intent.setClassName("com.android.gallery3d", "com.android.gallery3d.app.GalleryActivity");
                break;
            case CATEGORY_KAN_SET:
                intent.setClass(getActivity(), SettingActivity.class);
                break;
        }

        try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }
}
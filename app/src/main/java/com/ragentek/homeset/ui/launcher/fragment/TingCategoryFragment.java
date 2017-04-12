package com.ragentek.homeset.ui.launcher.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.net.http.HttpManager;
import com.ragentek.homeset.core.net.http.api.AudioApi;
import com.ragentek.homeset.core.utils.DeviceUtils;
import com.ragentek.homeset.ui.launcher.adapter.CategoryAdapter;
import com.ragentek.homeset.ui.launcher.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.ui.launcher.adapter.SpacesItemDecoration;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.protocol.commons.audio.CategoryVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.messages.http.APIResultVO;
import com.ragentek.protocol.messages.http.audio.CategoryResultVO;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.schedulers.Schedulers;


public class TingCategoryFragment extends Fragment {
    private static final String TAG = "TingCategoryFragment";

    private AudioApi mAudioApi;
    private DeviceUtils mDeviceUtils;

    private CategoryAdapter mAudioAdapter;
    private RecyclerView mAudioRV;
    private List<CategoryDetail> mAudioCategorys = new ArrayList<CategoryDetail>();
    private SpacesItemDecoration mSpacesItemDecoration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tingcategory, container, false);

        mAudioRV = (RecyclerView) view.findViewById(R.id.rv_audio);
        mSpacesItemDecoration = new SpacesItemDecoration(10);

        LogUtils.d(TAG, "onCreateView");

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAudioApi = HttpManager.createService(AudioApi.class);

        mDeviceUtils = new DeviceUtils(getActivity().getApplicationContext());

        LogUtils.d(TAG, "onCreate: ");
    }

    @Override
    public void onStart() {
        super.onStart();

        initAudioRecycleView();

        LogUtils.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();

        mAudioRV.removeItemDecoration(mSpacesItemDecoration);

        LogUtils.d(TAG, "onStop");
    }

    private void initAudioRecycleView() {
        mAudioAdapter = new CategoryAdapter(getActivity());
        mAudioAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                LogUtils.d(TAG, "onItemClick, position=" + position + " category=" + mAudioCategorys.get(position).toString());
                EventBus.getDefault().post(mAudioCategorys.get(position));
            }
        });
        mAudioRV.setAdapter(mAudioAdapter);

        GridLayoutManager manager = new GridLayoutManager(getActivity(), 14);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                CategoryDetail bean = mAudioCategorys.get(position);
                return bean.getSize();

            }
        });
        mAudioRV.setLayoutManager(manager);
        mAudioRV.addItemDecoration(mSpacesItemDecoration);
        mAudioRV.setHasFixedSize(true);

        getAudioCategoryData();
    }

    private void getAudioCategoryData() {
        long uid = mDeviceUtils.getLUid();
        long did = mDeviceUtils.getLDid();
        String atoken = mDeviceUtils.getAccessToken();

        mAudioApi.queryAudioCategory(uid, did, atoken)
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<APIResultVO>() {
                    @Override
                    public void onCompleted() {
                        LogUtils.d(TAG, "getAudioCategoryData, onCompleted");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mAudioAdapter.setDatas(mAudioCategorys);
                            }
                        });
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.d(TAG, "getAudioCategoryData, onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(APIResultVO apiResultVO) {
                        LogUtils.d(TAG, "getAudioCategoryData, onNext, apiResultVO=" + apiResultVO);
                        if (apiResultVO != null) {
                            int resCode = apiResultVO.getRes_code();
                            if (resCode == 0) {
                                String objString = JSON.toJSONString(apiResultVO.getRes_msg());
                                CategoryResultVO categoryResultVO = JSON.parseObject(objString, CategoryResultVO.class);
                                LogUtils.d(TAG, "getAudioCategoryData, onNext, categoryResultVO=" + categoryResultVO);

                                mAudioCategorys.clear();

                                if (categoryResultVO != null) {
                                    for (CategoryVO category : categoryResultVO.getCategories()) {
                                        CategoryDetail cat = decoratorCategory(category);
                                        mAudioCategorys.add(cat);
                                    }
                                    CategoryDetail favcat = new CategoryDetail();
                                    favcat.setIcon(R.drawable.favorite);
                                    favcat.setSize(4);
                                    favcat.setName(getResources().getString(R.string.my_favoriate));
                                    mAudioCategorys.add(favcat);
                                }
                            }
                        }
                    }
                });
    }

    private CategoryDetail decoratorCategory(CategoryVO catov) {
        CategoryDetail cat = new CategoryDetail();
        cat.setId(catov.getId().intValue());
        cat.setName(catov.getName());
        switch (cat.getId()) {
            case Category.ID.CROSS_TALK:
                cat.setIcon(R.drawable.cross_talk);
                cat.setSize(4);
                break;
            case Category.ID.CHINA_ART:
                cat.setIcon(R.drawable.china_art);
                cat.setSize(4);
                break;
            case Category.ID.HEALTH:
                cat.setIcon(R.drawable.health);
                cat.setSize(6);
                break;
            case Category.ID.STORYTELLING:
                cat.setIcon(R.drawable.storytelling);
                cat.setSize(6);
                break;
            case Category.ID.MUSIC:
                cat.setIcon(R.drawable.music);
                cat.setSize(4);
                break;
            case Category.ID.STOCK:
                cat.setIcon(R.drawable.stock);
                cat.setSize(4);
                break;
            case Category.ID.HISTORY:
                cat.setIcon(R.drawable.history);
                cat.setSize(4);
                break;
            case Category.ID.RADIO:
                cat.setIcon(R.drawable.radio);
                cat.setSize(6);
                break;
        }
        return cat;
    }
}
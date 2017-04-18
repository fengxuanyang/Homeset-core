package com.ragentek.homeset.audiocenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.ragentek.homeset.audiocenter.adapter.CategoryAdapter;
import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.audiocenter.view.widget.SpacesItemDecoration;
import com.ragentek.homeset.core.R;
import com.ragentek.protocol.commons.audio.CategoryVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.messages.http.audio.CategoryResultVO;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscriber;

/**
 * Created by xuanyang.feng on 2017/3/15.
 */

public class CategoryActivity extends AudioCenterBaseActivity {
    private List<CategoryDetail> mcategorys = new ArrayList<CategoryDetail>();
    CategoryAdapter audioAdapter;
    private final Long FAV_ID = -1L;
    @BindView(R.id.rv_audio)
    RecyclerView audioRV;
    @BindView(R.id.rv_video)
    RecyclerView videoRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioconter_activity_category);
        ButterKnife.bind(this);
        LogUtil.d(TAG, "onCreate: ");
        initRecycleView();
    }

    private void initRecycleView() {
        audioAdapter = new CategoryAdapter(this);
        audioAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                CategoryDetail mCategoryDetail = mcategorys.get(position);

                if (mCategoryDetail.getId() == FAV_ID) {
                    Intent intent = new Intent(CategoryActivity.this, AudioPlayActivityV2.class);
                    TagDetail detail = new TagDetail();
                    detail.setCategoryID(Constants.CATEGORY_FAV);
                    detail.setName(getResources().getString(R.string.my_favoriate));
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.CATEGORY_TAG, detail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(CategoryActivity.this, AudioRecommendActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(Constants.CATEGORY, mCategoryDetail);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }

            }
        });
        audioRV.setAdapter(audioAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                CategoryDetail bean = mcategorys.get(position);
                return bean.getSize();

            }
        });

        audioRV.setLayoutManager(manager);
        audioRV.addItemDecoration(new SpacesItemDecoration(10));
        audioRV.setHasFixedSize(true);
        getData();
    }

    private void getData() {
        Subscriber getCategory = new Subscriber<CategoryResultVO>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.e(TAG, "onError:" + e.getMessage());

            }

            @Override
            public void onNext(CategoryResultVO categoriesResult) {
                LogUtil.d(TAG, "onNext: categoryVOs:" + categoriesResult);

                if (categoriesResult != null) {
                    for (CategoryVO category : categoriesResult.getCategories()) {
                        LogUtil.d(TAG, category.getName());
                        CategoryDetail cat = decoratorCategory(category);
                        mcategorys.add(cat);
                    }
                    CategoryDetail favcat = new CategoryDetail();
                    favcat.setIcon(R.drawable.favorite);
                    favcat.setName(getResources().getString(R.string.my_favoriate));
                    mcategorys.add(favcat);
                    audioAdapter.addDatas(mcategorys);
                }
            }
        };
        AudioCenterHttpManager.getInstance(this).getCategory(getCategory);
    }

    private CategoryDetail decoratorCategory(CategoryVO catov) {
        CategoryDetail cat = new CategoryDetail();
        cat.setId(catov.getId().intValue());
        cat.setName(catov.getName());
        switch (cat.getId()) {
            case Category.ID.CROSS_TALK:
                cat.setIcon(R.drawable.cross_talk);
                cat.setSize(2);
                break;
            case Category.ID.CHINA_ART:
                cat.setIcon(R.drawable.china_art);
                break;
            case Category.ID.HEALTH:
                cat.setIcon(R.drawable.health);
                break;
            case Category.ID.STORYTELLING:
                cat.setIcon(R.drawable.storytelling);
                cat.setSize(2);

                break;
            case Category.ID.STOCK:
                cat.setIcon(R.drawable.stock);

                break;
            case Category.ID.HISTORY:
                cat.setIcon(R.drawable.history);

                break;
            case Category.ID.RADIO:
                cat.setIcon(R.drawable.radio);
                break;
            case Category.ID.MUSIC:
                cat.setIcon(R.drawable.music);
                cat.setSize(2);
                break;
            case Category.ID.OTHER:
                cat.setIcon(R.drawable.favorite);
                cat.setSize(1);
                break;
        }

        return cat;
    }

}

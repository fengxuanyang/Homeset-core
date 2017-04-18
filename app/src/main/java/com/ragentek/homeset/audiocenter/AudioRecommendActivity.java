package com.ragentek.homeset.audiocenter;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ragentek.homeset.audiocenter.adapter.ListItemBaseAdapter;
import com.ragentek.homeset.audiocenter.adapter.RecommendAdapter;
import com.ragentek.homeset.audiocenter.model.bean.CategoryDetail;
import com.ragentek.homeset.audiocenter.model.bean.TagDetail;
import com.ragentek.homeset.audiocenter.net.AudioCenterHttpManager;
import com.ragentek.homeset.audiocenter.utils.CacheUtils;
import com.ragentek.homeset.audiocenter.utils.Constants;
import com.ragentek.homeset.audiocenter.utils.LogUtil;
import com.ragentek.homeset.core.HomesetApp;
import com.ragentek.homeset.core.R;
import com.ragentek.homeset.core.task.event.BackHomeEvent;
import com.ragentek.protocol.commons.audio.TagVO;
import com.ragentek.protocol.constants.Category;
import com.ragentek.protocol.messages.http.audio.TagResultVO;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by xuanyang.feng on 2017/3/9.
 */

public class AudioRecommendActivity extends AudioCenterBaseActivity {
    private List<TagDetail> mTags = new ArrayList<>();
    private CategoryDetail currentCategory;
    private RecommendAdapter mRecommendAdapter;
    @BindView(R.id.rv_index)
    RecyclerView mRecyclerView;
    @BindView(R.id.top_progressbar)
    ProgressBar mProgressBar;

    @BindView(R.id.tag_name)
    TextView tagName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.audioenter_activity_audiorecommend);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        LogUtil.d(TAG, "onCreate bundle:" + bundle);
        if (bundle != null) {
            currentCategory = (CategoryDetail) bundle.getSerializable(Constants.CATEGORY);
            LogUtil.d(TAG, "onCreate currentCategory:" + currentCategory);
            if (currentCategory != null) {
                LogUtil.d(TAG, "onCreate currentCategory:" + currentCategory.getName());
            }
        }
        initView();
    }

    private void initView() {
        tagName.setText(currentCategory.getName());
        mProgressBar.setVisibility(View.VISIBLE);
        mRecommendAdapter = new RecommendAdapter(this);
        mRecommendAdapter.setOnItemClickListener(new ListItemBaseAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(AudioRecommendActivity.this, AudioPlayActivityV2.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constants.CATEGORY_TAG, mRecommendAdapter.getData().get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        mRecyclerView.setAdapter(mRecommendAdapter);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);
        if (currentCategory.getId() == Category.ID.RADIO) {

            for (int i = 0; i < Constants.Radio.values().length; i++) {
                TagDetail item = new TagDetail();
                item.setName(Constants.Radio.values()[i].getName());
                item.setRadioType(Constants.Radio.values()[i].getType());
                item.setIcon(getCategoryIcon(currentCategory.getId()));
                //TODO
                item.setCategoryID(Category.ID.RADIO);
                mTags.add(item);

            }
            updateView();

        } else {
            Observable.just(mTags = CacheUtils.readbean(this, CacheUtils.DATACACHE_CATEGORY_TAG, currentCategory.getName()))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<List<TagDetail>>() {
                        @Override
                        public void call(List<TagDetail> subjectsBeen) {
                            LogUtil.d(TAG, "call  subjectsBeen: " + subjectsBeen);
                            if (mTags != null) {
                                updateView();
                            } else {
                                loadData();
                            }
                        }
                    });

            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                                  @Override
                                                  public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                                                      super.onScrollStateChanged(recyclerView, newState);
                                                  }

                                                  @Override
                                                  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                      super.onScrolled(recyclerView, dx, dy);
                                                      LogUtil.d(TAG, "onScrolled: ");
                                                      if (!recyclerView.canScrollVertically(1) && HomesetApp.isNetworkAvailable()) {
                                                          updateData();
                                                      }
                                                  }
                                              }
            );
        }

    }

    //TODO
    private void updateData() {
        LogUtil.d(TAG, "updateData ");


    }

    private void updateView() {
        LogUtil.d(TAG, "updateData ");
        mRecommendAdapter.addDatas(mTags);
        mProgressBar.setVisibility(View.GONE);

    }

    private void loadData() {
        LogUtil.d(TAG, "loadData: ");
        Subscriber mloadDataSubscriber = new Subscriber<TagResultVO>() {
            @Override
            public void onCompleted() {
                LogUtil.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                LogUtil.d(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(TagResultVO tagResult) {
                LogUtil.d(TAG, "tagResult:" + tagResult);

                if (tagResult != null) {
                    for (TagVO tagov : tagResult.getTags()) {
                        LogUtil.d(TAG, tagov.getName());
                    }
                    Observable.just(tagResult)
                            .subscribeOn(Schedulers.io())
                            .unsubscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(new Func1<TagResultVO, List<TagDetail>>() {
                                @Override
                                public List<TagDetail> call(TagResultVO tagResult) {
                                    LogUtil.d(TAG, "call");
                                    List<TagDetail> data = new ArrayList<TagDetail>();
                                    for (TagVO ov : tagResult.getTags()) {
                                        TagDetail detail = new TagDetail();
                                        detail.setCategoryID(currentCategory.getId());
                                        detail.setName(ov.getName());
                                        detail.setIcon(getCategoryIcon(currentCategory.getId()));
                                        data.add(detail);
                                    }
                                    return data;
                                }
                            })
                            .subscribe(new Action1<List<TagDetail>>() {
                                @Override
                                public void call(List<TagDetail> tagDetails) {
                                    mTags = tagDetails;
                                    updateView();

                                }
                            });


                }
            }

        };
        AudioCenterHttpManager.getInstance(this).getTag(mloadDataSubscriber, currentCategory.getId());
    }

    @OnClick(R.id.iv_backhome)
    void backHome() {
        finishActivity();
    }

    @Override
    public void onBackPressed() {
        finishActivity();
    }

    private void finishActivity() {
        EventBus.getDefault().post(new BackHomeEvent());
        finish();
    }

    @Override
    protected void onDestroy() {
        LogUtil.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    private int getCategoryIcon(int categoryId) {
        int redId = R.drawable.placeholder_disk;
        switch (categoryId) {
            case Category.ID.CROSS_TALK:
                redId = R.drawable.cross_talk;
                break;
            case Category.ID.CHINA_ART:
                redId = R.drawable.china_art;
                break;
            case Category.ID.HEALTH:
                redId = R.drawable.health;
                break;
            case Category.ID.STORYTELLING:
                redId = R.drawable.storytelling;
                break;
            case Category.ID.STOCK:
                redId = R.drawable.stock;
                break;
            case Category.ID.HISTORY:
                redId = R.drawable.history;
                break;
            case Category.ID.RADIO:
                redId = R.drawable.radio;
                break;
            case Category.ID.MUSIC:
                redId = R.drawable.music;
                break;
        }
        return redId;
    }
}

package com.ragentek.homeset.ui.launcher.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ragentek.homeset.core.R;
import com.ragentek.homeset.ui.launcher.adapter.WeChatAdapter;
import com.ragentek.homeset.ui.utils.LogUtils;
import com.ragentek.homeset.wechat.WeChatHelper;
import com.ragentek.homeset.wechat.domain.WeChatException;
import com.ragentek.homeset.wechat.domain.WeChatInfo;

import java.util.List;

public class WeChatFragment extends Fragment {

    private static final String TAG = "WeChatFragment";

    private static final int WECHAT_GET_DATA = 100;
    private static final int WECHAT_VIEW_REFRESH = 1000;

    private RecyclerView mWeChatConactsView;
    private GridLayoutManager mLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WeChatAdapter mWeChatAdapter;
    private RecyclerItemListener mClickListener;

    private List<WeChatInfo> mWeChatInfos;
    private HandlerThread mHanlerThread;
    private Handler mWeChatHandler;

    private WeChatHelper mWeChatHelper;

    private class WeChatHandler extends Handler{
        private Context context;

        public WeChatHandler(Context context, Looper looper){
            super(looper);
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WECHAT_GET_DATA:
                    Log.d(TAG, "WECHAT_REFRESH_DATA, setAdapterData");
                    if(mWeChatHelper != null){
                        try {
                            Log.i(TAG, "getConacts start");
                            mWeChatHelper.open();
                            mWeChatInfos = mWeChatHelper.selectConact();
                            mWeChatHelper.close();
                            Log.i(TAG, "getConacts end");
                        } catch (WeChatException e) {
                            e.printStackTrace();
                        }
                    }
                    mHandler.sendEmptyMessage(WECHAT_VIEW_REFRESH);
                    break;
            }
        }
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WECHAT_VIEW_REFRESH:
                    setAdapterData();
                    swipeRefreshLayout.setRefreshing(false);
                    break;
            }
        }
    };

    private class RecyclerItemListener implements WeChatAdapter.OnRecyclerViewItemClickListener{

        @Override
        public void onItemClick(View view) {
            WeChatAdapter.ItemViewHolder itemViewHolder = (WeChatAdapter.ItemViewHolder)mWeChatConactsView.getChildViewHolder(view);
            if(itemViewHolder != null && itemViewHolder.getUserName() != null ){
                if(mWeChatHelper != null){
                    mWeChatHelper.startVoip(itemViewHolder.getUserName());
                }
            }
        }

        @Override
        public void onItemLongClick(View view) {
            //TODO long click do something
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wechat, container, false);

        mWeChatConactsView = (RecyclerView) view.findViewById(R.id.wx_conact);
        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.grid_swipe_refresh) ;

        initView();
        LogUtils.i(TAG, "onCreateView");
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i(TAG, "onCreate");

        init();
    }

    @Override
    public void onStart() {
        super.onStart();
        swipeRefreshLayout.setRefreshing(true);
        mWeChatHandler.sendEmptyMessage(WECHAT_GET_DATA);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if(null != mWeChatHelper) {
            mWeChatHelper.close();
            mWeChatHelper = null;
        }

        if(mHanlerThread != null && mHanlerThread.isAlive()){
            mHanlerThread.interrupt();
            mHanlerThread = null;
        }

        mWeChatHandler = null;
    }

    private void init(){
        mWeChatHelper = new WeChatHelper(getContext());

        mHanlerThread = new HandlerThread("WeChatRefresh");
        mHanlerThread.start();
        mWeChatHandler = new WeChatHandler(getContext(), mHanlerThread.getLooper());
    }

    private void initView(){
        mClickListener = new RecyclerItemListener();

        mWeChatAdapter = new WeChatAdapter(getContext());
        mWeChatAdapter.setOnItemClickListener(mClickListener);

        mLayoutManager = new GridLayoutManager(getActivity(),5,GridLayoutManager.VERTICAL,false);//设置为一个5列的纵向网格布局
        mWeChatConactsView.setLayoutManager(mLayoutManager);
        mWeChatConactsView.setAdapter(mWeChatAdapter);

        //调整SwipeRefreshLayout的位置
        swipeRefreshLayout.setProgressViewOffset(false, 0,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources().getDisplayMetrics()));
        //swipeRefreshLayout刷新监听
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                mWeChatHandler.sendEmptyMessage(WECHAT_GET_DATA);
            }
        });
    }

    private void setAdapterData(){
        mWeChatAdapter.setData(mWeChatInfos);
    }
}

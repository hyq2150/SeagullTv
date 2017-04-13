package com.victor.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.victor.adapter.ProgramAdapter;
import com.victor.adapter.ScaleInAnimatorAdapter;
import com.victor.adapter.SlideInLeftAnimatorAdapter;
import com.victor.adapter.SlideInRightAnimatorAdapter;
import com.victor.adapter.SwingBottomInAnimationAdapter;
import com.victor.data.CategoryData;
import com.victor.data.MeiPaiContentData;
import com.victor.data.ProgramData;
import com.victor.module.DataObservable;
import com.victor.module.HttpRequestHelper;
import com.victor.seagull.LiveEpgActivity;
import com.victor.seagull.ProgramDetailActivity;
import com.victor.seagull.R;
import com.victor.util.Constant;
import com.victor.util.SharePreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import butterknife.Bind;

public class ProgramFragment extends BaseFragment implements AdapterView.OnItemClickListener,SwipeRefreshLayout.OnRefreshListener,Observer {
    private String TAG = "ProgramFragment";
    @Bind(R.id.srl_fresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.rv_program)
    RecyclerView mRvProgram;

    private Context mContext;
    private List<ProgramData> programDatas = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private ProgramAdapter programAdapter;
    private HttpRequestHelper mHttpRequestHelper;

    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.Msg.REQUEST_SUCCESS:
                    swipeRefreshLayout.setRefreshing(false);
                    showCategory((List<ProgramData>) msg.obj);
                    break;
                case Constant.Msg.REQUEST_SUCCESS_NO_DATA:
                    swipeRefreshLayout.setRefreshing(false);
                    if (mContext != null) {
                        Toast.makeText(mContext,"服务器没有数据！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.Msg.REQUEST_FAILED:
                    swipeRefreshLayout.setRefreshing(false);
                    if (mContext != null) {
                        Toast.makeText(mContext,"访问服务器失败！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.Msg.PARSING_EXCEPTION:
                    swipeRefreshLayout.setRefreshing(false);
                    if (mContext != null) {
                        Toast.makeText(mContext,"数据解析异常！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.Msg.NETWORK_ERROR:
                    swipeRefreshLayout.setRefreshing(false);
                    if (mContext != null) {
                        Toast.makeText(mContext,"网络错误，请检查网络是否连接！",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Constant.Msg.SOCKET_TIME_OUT:
                    swipeRefreshLayout.setRefreshing(false);
                    if (mContext != null) {
                        Toast.makeText(mContext,"访问服务器超时，请重试！",Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    protected int getLayoutResource() {
        return R.layout.frag_program;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initialize();
        initData();
    }

    private void initialize () {
        mContext = getActivity();
        DataObservable.getInstance().addObserver(this);

        //设置 进度条的颜色变化，最多可以设置4种颜色
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_purple, android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(this);

        gridLayoutManager = new GridLayoutManager(getActivity(),2);//这里用线性宫格显示 类似于瀑布流
        mRvProgram.setLayoutManager(gridLayoutManager);
        programAdapter = new ProgramAdapter(getContext(),this);
        programAdapter.setProgramDatas(programDatas);
        programAdapter.setHeaderVisible(false);
        programAdapter.setFooterVisible(false);
//        mRvProgram.setAdapter(programAdapter);

//        ScaleInAnimatorAdapter animatorAdapter = new ScaleInAnimatorAdapter(programAdapter, mRvProgram);
//        AlphaAnimatorAdapter animatorAdapter = new AlphaAnimatorAdapter(programAdapter, mRvProgram);
//        SlideInBottomAnimatorAdapter animatorAdapter = new SlideInBottomAnimatorAdapter(programAdapter, mRvProgram);
        SlideInLeftAnimatorAdapter animatorAdapter = new SlideInLeftAnimatorAdapter(programAdapter, mRvProgram);
//        SlideInRightAnimatorAdapter animatorAdapter = new SlideInRightAnimatorAdapter(programAdapter, mRvProgram);
//        SwingBottomInAnimationAdapter animatorAdapter = new SwingBottomInAnimationAdapter(programAdapter, mRvProgram);
        mRvProgram.setAdapter(animatorAdapter);

        mHttpRequestHelper = new HttpRequestHelper(getActivity());

        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                } else if (position % 5 == 3) {
                    return 2;
                } else {
                    return 1;
                }
            }
        });
    }

    private void initData () {
        swipeRefreshLayout.setRefreshing(true);
        mHttpRequestHelper.sendRequest(Constant.Msg.CATEGORY_REQUEST);
    }

    private void showCategory (List<ProgramData> datas) {
        if (datas != null && datas.size() > 0) {
            programDatas.addAll(datas);
            programAdapter.notifyDataSetChanged();
        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (programDatas == null || programDatas.size() == 0) {
            if (mContext != null) {
                Toast.makeText(mContext,"正在初始化，请稍候...",Toast.LENGTH_SHORT).show();
            }
            return;
        }
        if (position < programDatas.size()) {
            SharePreferencesUtil.putString(mContext,Constant.HEAD_IMG_URL,programDatas.get(position).img);
            SharePreferencesUtil.putString(mContext,Constant.CURRENT_MEI_PAI_URL,programDatas.get(position).url);
        }
        Intent intent = new Intent(getActivity(),ProgramDetailActivity.class);
        if (position == 0) {
            intent = new Intent(getActivity(), LiveEpgActivity.class);
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                view.findViewById(R.id.iv_img), getString(R.string.transition_album_img));
        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
    }

    @Override
    public void update(Observable observable, Object data) {
        if (data instanceof CategoryData) {
            CategoryData info = (CategoryData) data;
            int status = info.status;
            Message msg = new Message();
            switch (status) {
                case Constant.Msg.REQUEST_SUCCESS:
                    msg.what = Constant.Msg.REQUEST_SUCCESS;
                    msg.obj = info.programDatas;
                    break;
                case Constant.Msg.REQUEST_SUCCESS_NO_DATA:
                    msg.what = Constant.Msg.REQUEST_SUCCESS_NO_DATA;
                    break;
                case Constant.Msg.REQUEST_FAILED:
                    msg.what = Constant.Msg.REQUEST_FAILED;
                    break;
                case Constant.Msg.PARSING_EXCEPTION:
                    msg.what = Constant.Msg.PARSING_EXCEPTION;
                    break;
                case Constant.Msg.NETWORK_ERROR:
                    msg.what = Constant.Msg.NETWORK_ERROR;
                    break;
                case Constant.Msg.SOCKET_TIME_OUT:
                    msg.what = Constant.Msg.SOCKET_TIME_OUT;
                    break;
            }
            mHandler.sendMessage(msg);
        }
    }

    @Override
    public void onDestroyView() {
        DataObservable.getInstance().deleteObserver(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        initData();
    }
}
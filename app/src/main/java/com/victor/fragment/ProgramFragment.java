package com.victor.fragment;import android.content.Context;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.v4.app.ActivityCompat;import android.support.v4.app.ActivityOptionsCompat;import android.support.v7.widget.OrientationHelper;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.StaggeredGridLayoutManager;import android.view.View;import android.widget.AdapterView;import android.widget.Toast;import com.victor.adapter.ProgramAdapter;import com.victor.data.CategoryData;import com.victor.data.ProgramData;import com.victor.module.DataObservable;import com.victor.module.HttpRequestHelper;import com.victor.seagull.LiveEpgActivity;import com.victor.seagull.ProgramDetailActivity;import com.victor.seagull.R;import com.victor.util.Constant;import java.util.ArrayList;import java.util.List;import java.util.Observable;import java.util.Observer;import butterknife.Bind;public class ProgramFragment extends BaseFragment implements AdapterView.OnItemClickListener,Observer {    private String TAG = "ProgramFragment";    @Bind(R.id.rv_program)    RecyclerView mRvProgram;    private Context mContext;    private List<ProgramData> programDatas = new ArrayList<>();    private StaggeredGridLayoutManager staggeredGridLayoutManager;    private ProgramAdapter programAdapter;    private HttpRequestHelper mHttpRequestHelper;    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case Constant.Msg.UPDATE_CATEGORY:                    showCategory((List<ProgramData>) msg.obj);                    break;            }        }    };    @Override    protected int getLayoutResource() {        return R.layout.frag_program;    }    @Override    public void onActivityCreated(Bundle savedInstanceState) {        super.onActivityCreated(savedInstanceState);        initialize();        initData();    }    private void initialize () {        mContext = getActivity();        DataObservable.getInstance().addObserver(this);        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);//这里用线性宫格显示 类似于瀑布流        mRvProgram.setLayoutManager(staggeredGridLayoutManager);        programAdapter = new ProgramAdapter(getContext(),this);        programAdapter.setProgramDatas(programDatas);        programAdapter.setHeaderVisible(false);        programAdapter.setFooterVisible(false);        mRvProgram.setAdapter(programAdapter);        mHttpRequestHelper = new HttpRequestHelper(getActivity());    }    private void initData () {        mHttpRequestHelper.sendRequest(Constant.Msg.CATEGORY_REQUEST);    }    private void showCategory (List<ProgramData> datas) {        if (datas != null && datas.size() > 0) {            programDatas.addAll(datas);            programAdapter.notifyDataSetChanged();        }    }    @Override    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {        if (programDatas == null || programDatas.size() == 0) {            if (mContext != null) {                Toast.makeText(mContext,"正在初始化，请稍候...",Toast.LENGTH_SHORT).show();            }            return;        }        Intent intent = new Intent(getActivity(),ProgramDetailActivity.class);        if (position == 0) {            intent = new Intent(getActivity(), LiveEpgActivity.class);        }        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),                        view.findViewById(R.id.iv_img), getString(R.string.transition_album_img));        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());    }    @Override    public void update(Observable observable, Object data) {        if (data instanceof CategoryData) {            CategoryData info = (CategoryData) data;            Message msg = new Message();            msg.what = Constant.Msg.UPDATE_CATEGORY;            msg.obj = info.programDatas;            mHandler.sendMessage(msg);        }    }    @Override    public void onDestroyView() {        DataObservable.getInstance().deleteObserver(this);        super.onDestroyView();    }}
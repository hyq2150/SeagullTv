package com.victor.fragment;import android.content.Intent;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.v4.app.ActivityCompat;import android.support.v4.app.ActivityOptionsCompat;import android.support.v4.app.ListFragment;import android.support.v7.widget.OrientationHelper;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.StaggeredGridLayoutManager;import android.view.SurfaceView;import android.view.View;import android.widget.AdapterView;import com.victor.adapter.ProgramAdapter;import com.victor.data.ProgramData;import com.victor.module.Player;import com.victor.seagull.LiveActivity;import com.victor.seagull.LiveEpgActivity;import com.victor.seagull.MainActivity;import com.victor.seagull.ProgramDetailActivity;import com.victor.seagull.R;import com.victor.util.Constant;import java.util.ArrayList;import java.util.List;import butterknife.Bind;public class ProgramFragment extends BaseFragment implements AdapterView.OnItemClickListener {    private String TAG = "ProgramFragment";    @Bind(R.id.rv_program)    RecyclerView mRvProgram;    private List<ProgramData> programDatas = new ArrayList<>();    private StaggeredGridLayoutManager staggeredGridLayoutManager;    private ProgramAdapter programAdapter;    @Override    protected int getLayoutResource() {        return R.layout.frag_program;    }    @Override    public void onActivityCreated(Bundle savedInstanceState) {        super.onActivityCreated(savedInstanceState);        initialize();        initData();    }    private void initialize () {        staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);//这里用线性宫格显示 类似于瀑布流        mRvProgram.setLayoutManager(staggeredGridLayoutManager);        programAdapter = new ProgramAdapter(getContext(),this);        programAdapter.setProgramDatas(programDatas);        programAdapter.setHeaderVisible(false);        programAdapter.setFooterVisible(false);        mRvProgram.setAdapter(programAdapter);    }    private void initData () {        String[] titles = getResources().getStringArray(R.array.program_list);        for (int i=0;i<titles.length;i++) {            ProgramData info = new ProgramData();            info.title = titles[i];            if (i % 2 == 0) {                info.imgId = R.mipmap.default_img;            } else {                info.imgId = R.mipmap.img_header;            }            programDatas.add(info);        }        programAdapter.notifyDataSetChanged();    }    @Override    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {        Intent intent = new Intent(getActivity(),ProgramDetailActivity.class);        if (position == 0) {            intent = new Intent(getActivity(), LiveEpgActivity.class);        }        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),                        view.findViewById(R.id.iv_img), getString(R.string.transition_album_img));        ActivityCompat.startActivity(getActivity(), intent, options.toBundle());    }}
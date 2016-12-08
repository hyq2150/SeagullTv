package com.victor.seagull;import android.content.Context;import android.content.Intent;import android.graphics.drawable.AnimationDrawable;import android.media.AudioManager;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;import android.text.TextUtils;import android.util.Log;import android.view.Display;import android.view.GestureDetector;import android.view.KeyEvent;import android.view.MotionEvent;import android.view.SurfaceView;import android.view.View;import android.view.WindowManager;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.view.animation.LinearInterpolator;import android.widget.AdapterView;import android.widget.FrameLayout;import android.widget.ImageView;import android.widget.LinearLayout;import android.widget.RelativeLayout;import com.victor.adapter.CategoryAdapter;import com.victor.adapter.ChannelAdapter;import com.victor.adapter.ScreenScaleAdapter;import com.victor.adapter.WheelTextAdapter;import com.victor.data.LiveCategory;import com.victor.data.Channel;import com.victor.data.EpgData;import com.victor.data.EpgInfo;import com.victor.data.ScaleData;import com.victor.interfaces.OnWheelScrollListener;import com.victor.module.DataObservable;import com.victor.module.HttpRequestHelper;import com.victor.module.Player;import com.victor.util.Constant;import com.victor.util.EpgUtil;import com.victor.util.Loger;import com.victor.util.SharePreferencesUtil;import com.victor.util.TVLiveUtils;import com.victor.view.CircleProgressBar;import com.victor.view.TipView;import com.victor.view.WheelView;import java.util.ArrayList;import java.util.List;import java.util.Observable;import java.util.Observer;import butterknife.Bind;import butterknife.ButterKnife;import butterknife.OnClick;public class LiveActivity extends BaseActivity implements AdapterView.OnItemClickListener,        View.OnTouchListener,Observer {    private String TAG = "LiveActivity";    @Bind(R.id.sv_live)    SurfaceView svLive;    @Bind(R.id.iv_maintenance)    ImageView mIvMaintenance;    @Bind(R.id.iv_run_loading)    ImageView mIvRunLoading;    @Bind(R.id.iv_run_fold)    ImageView mIvRunFold;    @Bind(R.id.rv_channels)    RecyclerView mRvChannels;    @Bind(R.id.wv_category)    WheelView mWvCategory;    @Bind(R.id.wv_scale)    WheelView mWvScales;    @Bind(R.id.tv_tip)    TipView mTvTip;    @Bind(R.id.ll_channels)    LinearLayout mLayoutChannel;    @Bind(R.id.iv_voice_bright)    ImageView mIvVoiiceBright;    @Bind(R.id.cpb_voice_bright)    CircleProgressBar mCpbVoiiceBright;    @Bind(R.id.fl_voice_bright)    FrameLayout mLayoutVoiceBright;    @Bind(R.id.fl_epg_tip)    FrameLayout mLayoutEpgTip;    @Bind(R.id.rl_gentrue_tip)    RelativeLayout mLayoutGentrueTip;    private LinearLayoutManager mLlmChannel,mLlmCategory;//    private CategoryAdapter categoryAdapter;    private WheelTextAdapter wheelTextAdapter;    private ScreenScaleAdapter screenScaleAdapter;    private ChannelAdapter channelAdapter;    private List<LiveCategory> categoryList = new ArrayList<>();    private List<ScaleData> scaleDatas = new ArrayList<>();    private List<Channel> channelList = new ArrayList<>();    private HttpRequestHelper mHttpRequestHelper;    private Player mPlayer;    private int currentCategoryPosition = -1;    private int currentPosition = -1;//当前播放界面位置    private int livePlayPosition;//当前播放直播源地址位置    private Animation mAnimShowChannels,mAnimHideChannels,mAnimRotate,mAnimAlpha;    private AnimationDrawable runLoadingAnim;    private GestureDetector mGestureDetector;    private AudioManager mAudioManager;    private int mMaxVolume;    private int mVolume = -1;    private float mBrightness = -1f;    private boolean isPlayNext;//是否播放下一个节目    private boolean isPlayPrev;//是否播放上一个节目    private boolean isSourceNext;//是否切换下一个源    private boolean isSourcePrev;//是否切换上一个源    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case Player.PLAYER_PREPARING:                    startLoadingAnim();                    mIvMaintenance.setVisibility(View.GONE);                    mHandler.removeMessages(Constant.Msg.DRAW_HEART_BUBBLE);                    mHandler.sendEmptyMessageDelayed(Constant.Msg.DRAW_HEART_BUBBLE,200);                    break;                case Player.PLAYER_PREPARED:                    stopLoadingAnim();                    mIvMaintenance.setVisibility(View.GONE);                    hiddenViewAnim();                    break;                case Player.PLAYER_ERROR:                    stopLoadingAnim();                    livePlayPosition++;                    play(false);                    break;                case Player.PLAYER_BUFFERING_START:                    startLoadingAnim();                    mHandler.removeMessages(Constant.Msg.DRAW_HEART_BUBBLE);                    mHandler.sendEmptyMessageDelayed(Constant.Msg.DRAW_HEART_BUBBLE,200);                    break;                case Player.PLAYER_BUFFERING_END:                    stopLoadingAnim();                    mIvMaintenance.setVisibility(View.GONE);                    break;                case Player.PLAYER_PROGRESS_INFO:                    break;                case Player.PLAYER_COMPLETE:                    break;                case Constant.Msg.HIDE_PLAY_CTRL_VIEW:                    hiddenViewAnim();                    break;                case Constant.Msg.SHOW_LIVE_EPG:                    List<String> tips = new ArrayList<>();                    String lastPlayName = SharePreferencesUtil.getString(getApplicationContext(),Constant.CURRENT_PLAY_NAME,"湖北卫视HD");                    tips.add(lastPlayName);                    if (currentPosition != -1) {                        tips.clear();                        int currentSource = livePlayPosition + 1;                        tips.add(channelList.get(currentPosition).name + "（源" + currentSource + "/" + channelList.get(currentPosition).playUrls.length + "）");                    }                    List<EpgInfo> epgs = (List<EpgInfo>) msg.obj;                    if (epgs != null) {                        if (epgs.size() == 1) {                            tips.add("正在播放：" + epgs.get(0).epg);                        } else if (epgs.size() == 2) {                            tips.add("正在播放：" + epgs.get(0).epg);                            tips.add("即将播放：" + epgs.get(1).epg);                        }                    }                    mLayoutEpgTip.setVisibility(View.VISIBLE);                    mTvTip.setTipList(tips);                    break;            }        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏        setContentView(R.layout.activity_live);        ButterKnife.bind(this);        initialize();        initLoadingAnimi();        initData();    }    private void initialize () {        DataObservable.getInstance().addObserver(this);        mTvTip.setIconVisible(false);        mLlmChannel = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false);        mRvChannels.setLayoutManager(mLlmChannel);        channelAdapter = new ChannelAdapter(this,this);        channelAdapter.setDatas(channelList);        channelAdapter.setHeaderVisible(false);        channelAdapter.setFooterVisible(false);        mRvChannels.setAdapter(channelAdapter);        svLive.setOnTouchListener(this);        mLlmCategory = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);        wheelTextAdapter = new WheelTextAdapter(this);        wheelTextAdapter.setDatas(categoryList);        mWvCategory.setViewAdapter(wheelTextAdapter);        mWvCategory.setCyclic(true);        mWvCategory.setCurrentItem(0);        mWvCategory.addScrollingListener(new mOnWheelScrollListener(false));        screenScaleAdapter = new ScreenScaleAdapter(this);        screenScaleAdapter.setDatas(scaleDatas);        mWvScales.setViewAdapter(screenScaleAdapter);        mWvScales.setCyclic(true);        mWvScales.setCurrentItem(0);        mWvScales.addScrollingListener(new mOnWheelScrollListener(true));        mHttpRequestHelper = new HttpRequestHelper(this);        mGestureDetector = new GestureDetector(this, new MyGestureListener());        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);        mPlayer = new Player(this,svLive,mHandler,null);        play(true);    }    private void initData () {        boolean isShowGentrueTip  = SharePreferencesUtil.getBoolean(this,Constant.IS_SHOW_GENTRUE_TIP);        if (!isShowGentrueTip) {            mLayoutGentrueTip.setVisibility(View.VISIBLE);            SharePreferencesUtil.putBoolean(this,Constant.IS_SHOW_GENTRUE_TIP,true);        }        String[] scales = getResources().getStringArray(R.array.scale_list);        for (int i=0;i<scales.length;i++) {            ScaleData info = new ScaleData();            info.scale = i;            info.scaleName = scales[i];            scaleDatas.add(info);        }        screenScaleAdapter.notifyDataChangedEvent();        mCpbVoiiceBright.setProgress(50);        Intent intent = getIntent();        if (intent != null) {            List<LiveCategory> categories = (List<LiveCategory>) intent.getSerializableExtra(Constant.CHANNEL_DATA_KEY);            if (categories != null && categories.size() > 0) {                categoryList.clear();                categoryList.addAll(categories);                wheelTextAdapter.notifyDataChangedEvent();                List<Channel> channels = categoryList.get(0).channels;                if (channels != null && channels.size() > 0) {                    channelList.clear();                    channelList.addAll(channels);                    channelAdapter.notifyDataSetChanged();                }            }        }    }    private void initLoadingAnimi(){        mAnimRotate = AnimationUtils.loadAnimation(this, R.anim.animi_rotate);        mAnimRotate.setInterpolator(new LinearInterpolator());//重复播放不停顿        mAnimRotate.setFillAfter(true);//停在最后        runLoadingAnim = (AnimationDrawable) mIvRunLoading.getBackground();        //显示View动画        mAnimShowChannels = AnimationUtils.loadAnimation(this, R.anim.channel_translateback);        mAnimShowChannels.setFillAfter(true);        //隐藏View动画        mAnimHideChannels = AnimationUtils.loadAnimation(this, R.anim.channel_translate);        mAnimHideChannels.setFillAfter(true);        //隐藏音量亮度view动画        mAnimAlpha = AnimationUtils.loadAnimation(this, R.anim.animi_alpha);        mAnimAlpha.setFillAfter(true);    }    private void startLoadingAnim () {        mIvRunFold.startAnimation(mAnimRotate);        runLoadingAnim.start();    }    private void stopLoadingAnim () {        mIvRunFold.clearAnimation();        runLoadingAnim.stop();    }    /**     * 播放从下向上移动动画显示view     */    private void showViewAnim() {        if (mLayoutChannel.getVisibility() == View.VISIBLE) {            return;        }        mLayoutChannel.setVisibility(View.VISIBLE);        mLayoutChannel.startAnimation(mAnimShowChannels);        mHandler.removeMessages(Constant.Msg.HIDE_PLAY_CTRL_VIEW);        mHandler.sendEmptyMessageDelayed(Constant.Msg.HIDE_PLAY_CTRL_VIEW, 5000);    }    /**     * 播放从上向下移动动画隐藏view     */    private void hiddenViewAnim() {        if (mLayoutChannel.getVisibility() == View.GONE) {            return;        }        mLayoutChannel.startAnimation(mAnimHideChannels);        mLayoutChannel.setVisibility(View.GONE);        mLayoutEpgTip.setVisibility(View.GONE);    }    private void play (boolean isPlayLast) {        if (isPlayLast || channelList.size() == 0) {//首次播放默认频道或最后一次播放的频道            String lastPlayUrl = SharePreferencesUtil.getString(this,Constant.CURRENT_PLAY_URL,Constant.DEFAULT_PLAY_URL);            String lastPlayEpg = SharePreferencesUtil.getString(this,Constant.CURRENT_PLAY_EPG,Constant.DEFAULT_EPG_URL);            mPlayer.playUrl(lastPlayUrl,true);            mHttpRequestHelper.sendRequestWithParms(Constant.Msg.REQUEST_LIVE_EPG,lastPlayEpg);            return;        }        if (currentPosition < 0) {            currentPosition = channelList.size() - 1;        }        if (currentPosition >= channelList.size()) {            currentPosition = 0;        }        String[] playUrls = channelList.get(currentPosition).playUrls;        if (playUrls == null || playUrls.length == 0) {            livePlayPosition = 0;            mIvMaintenance.setVisibility(View.VISIBLE);            return;        }        if (livePlayPosition < 0) {            livePlayPosition = playUrls.length - 1;        }        if (livePlayPosition >= playUrls.length) {            livePlayPosition = 0;        }        SharePreferencesUtil.putString(this,Constant.CURRENT_PLAY_NAME,channelList.get(currentPosition).name);        String url = channelList.get(currentPosition).playUrls[livePlayPosition];        SharePreferencesUtil.putString(this,Constant.CURRENT_PLAY_URL,url);        mPlayer.playUrl(url,true);        String epg = channelList.get(currentPosition).epg;        if (!TextUtils.isEmpty(epg)) {            mHttpRequestHelper.sendRequestWithParms(Constant.Msg.REQUEST_LIVE_EPG,epg);            SharePreferencesUtil.putString(this,Constant.CURRENT_PLAY_EPG,epg);        }    }    @Override    public void update(Observable observable, Object data) {        if (data instanceof EpgData) {            EpgData epgData = (EpgData) data;            List<EpgInfo> epgs = EpgUtil.getEpgByTime(epgData);            if (epgData.status == Constant.Msg.REQUEST_SUCCESS) {                if (epgs != null && epgs.size() > 0) {                    Message msg = new Message();                    msg.what = Constant.Msg.SHOW_LIVE_EPG;                    msg.obj = epgs;                    mHandler.sendMessage(msg);                }            }        }    }    @Override    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {        int action = (int) l;        switch (action) {            case Constant.Action.ON_CATEGORY_ITEM_CLICK:                break;            case Constant.Action.ON_CHANNEL_ITEM_CLICK:                currentPosition = i;                if (i < channelList.size()) {                    play(false);                }                break;        }    }    @Override    protected void onDestroy() {        DataObservable.getInstance().deleteObserver(this);        if(mPlayer != null){            mPlayer.stop();            mPlayer.close();        }        super.onDestroy();    }    private void onVolumeSlide(float percent) {        if (mVolume == -1) {            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);            if (mVolume < 0) {                mVolume = 0;            }            mIvVoiiceBright.setImageResource(R.mipmap.ic_voice);            mLayoutVoiceBright.setVisibility(View.VISIBLE);        }        int progress = (int) (percent * mMaxVolume) + mVolume;        if (progress > mMaxVolume) {            progress = mMaxVolume;        } else if (progress < 0) {            progress = 0;        }        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);        mCpbVoiiceBright.setProgress(progress * 100 / mMaxVolume);    }    private void onBrightnessSlide(float percent) {        if (mBrightness < 0) {            mBrightness = getWindow().getAttributes().screenBrightness;            if (mBrightness <= 0.00f) {                mBrightness = 0.50f;            }            if (mBrightness < 0.01f) {                mBrightness = 0.01f;            }            mIvVoiiceBright.setImageResource(R.mipmap.ic_bright);            mLayoutVoiceBright.setVisibility(View.VISIBLE);        }        WindowManager.LayoutParams lpa = getWindow().getAttributes();        lpa.screenBrightness = mBrightness + percent;        if (lpa.screenBrightness > 1.0f) {            lpa.screenBrightness = 1.0f;        } else if (lpa.screenBrightness < 0.01f) {            lpa.screenBrightness = 0.01f;        }        getWindow().setAttributes(lpa);        double currentBrightness = lpa.screenBrightness * 100;        mCpbVoiiceBright.setProgress((float) currentBrightness);    }    @Override    public boolean onTouch(View view, MotionEvent motionEvent) {        if (mGestureDetector.onTouchEvent(motionEvent)){            return true;        }        switch (motionEvent.getAction()) {            case MotionEvent.ACTION_DOWN:                break;            case MotionEvent.ACTION_MOVE:                break;            case MotionEvent.ACTION_UP:                endGesture();                break;            default:                break;        }        return super.onTouchEvent(motionEvent);    }    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {        @Override        public boolean onScroll(MotionEvent e1, MotionEvent e2,                                float distanceX, float distanceY) {            isPlayNext = false;            isPlayPrev = false;            isSourceNext = false;            isSourcePrev = false;            float mOldX = e1.getX(), mOldY = e1.getY();            int x = (int) e2.getRawX();            int y = (int) e2.getRawY();            Display disp = getWindowManager().getDefaultDisplay();            int windowWidth = disp.getWidth();            int windowHeight = disp.getHeight();            if (mOldX > windowWidth * 2.0 / 3) {//如果是在离屏幕左侧 2/3（即 离屏幕右侧1/3）区域上下滑动则改变音量                onVolumeSlide((mOldY - y) / windowHeight);            } else if (mOldX < windowWidth * 1.0 / 3) {//如果是在离屏幕左侧 1/3（区域上下滑动则改变亮度                onBrightnessSlide((mOldY - y) / windowHeight);            } else if (mOldX > windowWidth * 1.0 / 3 && mOldX < windowWidth * 2.0 / 3) {//如果是在离屏幕左侧 1/3到2/3区域（即屏幕中间）上下滑动则切换频道                double sideWidth = Math.abs(mOldX - x);                double sideHeight = Math.abs(mOldY - y);                double distance = sideWidth - sideHeight;                if (distance > 100) {//水平滑动                    if (mOldX > x) {                        isSourcePrev = true;                        isSourceNext = false;                        isPlayNext = false;                        isPlayPrev = false;                    } else {                        isSourceNext = true;                        isSourcePrev = false;                        isPlayNext = false;                        isPlayPrev = false;                    }                } else if (distance < -100){//垂直滑动                    if (mOldY > y) {                        isPlayNext = true;                        isPlayPrev = false;                        isSourceNext = false;                        isSourcePrev = false;                    } else {                        isPlayPrev = true;                        isPlayNext = false;                        isSourceNext = false;                        isSourcePrev = false;                    }                }            }            return super.onScroll(e1, e2, distanceX, distanceY);        }    }    class mOnWheelScrollListener implements OnWheelScrollListener {        private boolean isScaleChange;        public mOnWheelScrollListener (boolean isScaleChange) {            this.isScaleChange = isScaleChange;        }        @Override        public void onScrollingStarted(WheelView wheel) {        }        @Override        public void onScrollingFinished(WheelView wheel) {            if (isScaleChange) {//切换画面比例                int index = wheel.getCurrentItem();                if (index < scaleDatas.size()) {                    TVLiveUtils.setScreenScales(getApplicationContext(),svLive,scaleDatas.get(index).scale);                }            } else {//切换直播列表分类                currentCategoryPosition = wheel.getCurrentItem();                if (currentCategoryPosition < categoryList.size()) {                    channelList.clear();                    channelList.addAll(categoryList.get(currentCategoryPosition).channels);                    channelAdapter.notifyDataSetChanged();                }            }        }    }    @OnClick({R.id.sv_live})    public void onClick (View view) {        switch (view.getId()) {            case R.id.sv_live:                showViewAnim();                break;        }    }    private void endGesture() {        mVolume = -1;        mBrightness = -1f;        mLayoutVoiceBright.setVisibility(View.GONE);        mLayoutGentrueTip.setVisibility(View.GONE);        if (isPlayNext) {            currentPosition++;            play(false);        } else if (isPlayPrev) {            currentPosition--;            play(false);        } else if (isSourceNext) {            livePlayPosition++;            play(false);        } else if (isSourcePrev) {            livePlayPosition--;            play(false);        }    }    @Override    public boolean onKeyDown(int keyCode, KeyEvent event) {        switch (keyCode) {            case KeyEvent.KEYCODE_VOLUME_UP: {                mIvVoiiceBright.setImageResource(R.mipmap.ic_voice);                mLayoutVoiceBright.setVisibility(View.VISIBLE);                int progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);                progress ++;                if (progress > mMaxVolume) {                    progress = mMaxVolume;                } else if (progress < 0) {                    progress = 0;                }                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);                mCpbVoiiceBright.setProgress(progress * 100 / mMaxVolume);                return true;            }            case KeyEvent.KEYCODE_VOLUME_DOWN:{                mIvVoiiceBright.setImageResource(R.mipmap.ic_voice);                mLayoutVoiceBright.setVisibility(View.VISIBLE);                int progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);                progress --;                if (progress > mMaxVolume) {                    progress = mMaxVolume;                } else if (progress < 0) {                    progress = 0;                }                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);                mCpbVoiiceBright.setProgress(progress * 100 / mMaxVolume);                return true;            }        }        return super.onKeyDown(keyCode, event);    }    @Override    public boolean onKeyUp(int keyCode, KeyEvent event) {        switch (keyCode) {            case KeyEvent.KEYCODE_VOLUME_UP: {                mLayoutVoiceBright.setVisibility(View.GONE);                return true;            }            case KeyEvent.KEYCODE_VOLUME_DOWN:{                mLayoutVoiceBright.setVisibility(View.GONE);                return true;            }        }        return super.onKeyUp(keyCode, event);    }}
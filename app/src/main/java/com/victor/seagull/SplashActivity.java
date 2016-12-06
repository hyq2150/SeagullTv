package com.victor.seagull;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.victor.module.Player;
import com.victor.util.Constant;
import com.victor.util.SharePreferencesUtil;

public class SplashActivity extends BaseActivity implements MediaPlayer.OnCompletionListener{

    private View view;
    private VideoView mVvPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = getLayoutInflater().from(this).inflate(R.layout.activity_splash,null);
        setContentView(view);
        initialize();
        initData();
    }

    private void initialize () {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        mVvPlay = (VideoView) findViewById(R.id.vv_play);
        mVvPlay.setOnCompletionListener(this);
    }

    private void initData () {
        boolean isPlayWelcome  = SharePreferencesUtil.getBoolean(this,Constant.isPlayWelcome);
        if (!isPlayWelcome) {
            SharePreferencesUtil.putBoolean(this,Constant.isPlayWelcome,true);
            Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.welcome);
            mVvPlay.setVideoURI(uri);
            mVvPlay.start();
           return;
        }

        Intent intent = new Intent(SplashActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();



    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Intent intent = new Intent(SplashActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
}

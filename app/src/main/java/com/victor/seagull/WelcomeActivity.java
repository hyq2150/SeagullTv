package com.victor.seagull;import android.content.Intent;import android.os.Build;import android.os.Handler;import android.os.Message;import android.support.v7.app.AppCompatActivity;import android.os.Bundle;import android.view.View;import android.view.Window;import android.view.WindowManager;import android.view.animation.Animation;import android.view.animation.AnimationUtils;import android.widget.ImageView;public class WelcomeActivity extends BaseActivity {    private ImageView mIveEntry;    private View view;    Handler mHandler = new Handler() {        @Override        public void handleMessage(Message msg) {            switch (msg.what) {                case 100:                    startActivity(new Intent(WelcomeActivity.this, MainActivity.class));                    finish();                    break;            }        }    };    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏        view = getLayoutInflater().from(this).inflate(R.layout.activity_welcome,null);        setContentView(view);        mIveEntry = (ImageView) findViewById(R.id.iv_entry);        Animation animation = AnimationUtils.loadAnimation(this, R.anim.scale);        mIveEntry.startAnimation(animation);        mHandler.sendEmptyMessageDelayed(100, 2000);    }}
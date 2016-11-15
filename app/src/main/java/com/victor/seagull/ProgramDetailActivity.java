/* * Copyright (C) 2015 The Android Open Source Project * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *      http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.victor.seagull;import android.annotation.TargetApi;import android.content.Intent;import android.graphics.Color;import android.os.Build;import android.os.Bundle;import android.os.Handler;import android.os.Message;import android.support.design.widget.CollapsingToolbarLayout;import android.support.v4.app.ActivityCompat;import android.support.v4.app.ActivityOptionsCompat;import android.support.v7.app.AppCompatActivity;import android.support.v7.widget.LinearLayoutManager;import android.support.v7.widget.RecyclerView;import android.support.v7.widget.Toolbar;import android.view.MenuItem;import android.view.View;import android.view.Window;import android.widget.ImageView;import com.bumptech.glide.Glide;public class ProgramDetailActivity extends BaseActivity  {    private String TAG = "ProgramDetailActivity";    private RecyclerView mRecyclerView;    private LinearLayoutManager linearLayoutManager;    ImageView imageView;    Handler mHandler = new Handler(){        @Override        public void handleMessage(Message msg) {            switch (msg.what) {            }        }    };    @Override    public void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_detail);        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);        setSupportActionBar(toolbar);        getSupportActionBar().setDisplayHomeAsUpEnabled(true);        CollapsingToolbarLayout collapsingToolbar =                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);        collapsingToolbar.setTitle("从你的全世界飘过");        loadBackdrop();    }    private void loadBackdrop() {        imageView = (ImageView) findViewById(R.id.backdrop);        Glide.with(getApplicationContext()).load(R.mipmap.default_img).fitCenter().into(imageView);    }    @Override    public boolean onOptionsItemSelected(MenuItem item) {        switch (item.getItemId()) {            case android.R.id.home:                onBackPressed();                return true;            default:                return super.onOptionsItemSelected(item);        }    }    @Override    protected void onDestroy() {        super.onDestroy();    }    @TargetApi(Build.VERSION_CODES.LOLLIPOP)    @Override    protected void onResume() {        super.onResume();        Window window = getWindow();        window.setStatusBarColor(Color.TRANSPARENT);    }}
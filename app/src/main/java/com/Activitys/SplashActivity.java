package com.Activitys;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import cn.bmob.v3.Bmob;
import huti.material.R;


public class SplashActivity extends Activity {
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private ImageView splash_img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splash_img = (ImageView) findViewById(R.id.splash_img);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.enlarge);
        animation.setDuration(3000);
        splash_img.startAnimation(animation);
        //初始化BmobSDK
        Bmob.initialize(this, "9f3de44e7ac60b71e285765949717b64");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this , LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);



    }

}
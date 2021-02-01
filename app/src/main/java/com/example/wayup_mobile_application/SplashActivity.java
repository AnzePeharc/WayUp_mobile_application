package com.example.wayup_mobile_application;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;



public class SplashActivity extends AppCompatActivity {
    Handler handler;

    //Animation variables
    Animation start_animation;

    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        // Initialize the animation variable
        start_animation = AnimationUtils.loadAnimation(this, R.anim.splash_screen_animation);
        // Initialize the ImageView variable
        logo = findViewById(R.id.logo_id);
        // set the animation
        logo.setAnimation(start_animation);

        handler= new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1500);

    }
}

package com.playzelo.jackpot.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpot.R;
import com.playzelo.jackpot.databinding.ActivitySplashBinding;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new Handler().postDelayed(() ->{
            startActivity(new Intent(SplashActivity.this, com.playzelo.jackpotmodule.activity.SplashActivity.class));
            finish();
        },4000);

        // Animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
        binding.appName.setVisibility(TextView.VISIBLE);
        binding.appName.startAnimation(animation);

    }

}
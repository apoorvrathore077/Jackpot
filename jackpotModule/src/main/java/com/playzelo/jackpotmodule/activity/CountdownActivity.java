package com.playzelo.jackpotmodule.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.databinding.ActivityJackpotCountdownBinding;

public class CountdownActivity extends AppCompatActivity {

    private MediaPlayer tickSound;

    private ActivityJackpotCountdownBinding binding;

    private String userId, username, authToken, entryFee;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJackpotCountdownBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Shader textShader = new LinearGradient(
                0, 0, 0, binding.tvStartingGame.getTextSize(),
                new int[]{
                        Color.parseColor("#FFD700"),  // Gold
                        Color.parseColor("#FF8C00")   // Orange
                }, null, Shader.TileMode.CLAMP);
        binding.tvStartingGame.getPaint().setShader(textShader);

        // 🔊 Setup tick sound
        tickSound = MediaPlayer.create(this, R.raw.tick); // Place tick.mp3 in res/raw/
        startTickingSound(10); // 6 ticks

         entryFee = getIntent().getStringExtra("entry_fee");
         userId = getIntent().getStringExtra("userId");
         username = getIntent().getStringExtra("username");
         authToken = getIntent().getStringExtra("auth_token");

        binding.lottieView.setRepeatCount(0);
        binding.lottieView.setAnimation(R.raw.countdown); // your renamed JSON file
        binding.lottieView.playAnimation();

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(CountdownActivity.this, GameActivity.class);
            intent.putExtra("openFragment", "wallet");

            intent.putExtra("entry_fee", entryFee);
            intent.putExtra("userId", userId);
            intent.putExtra("username", username);
            intent.putExtra("auth_token", authToken);
            startActivity(intent);
            finish();
        }, 10000);
    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            );
        }
    }

    private void startTickingSound(int seconds) {
        Handler handler = new Handler();
        for (int i = 0; i < seconds; i++) {
            handler.postDelayed(() -> {
                if (tickSound != null) {
                    tickSound.start();
                }
            }, i * 1000);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tickSound != null) {
            tickSound.release();
        }
    }
}


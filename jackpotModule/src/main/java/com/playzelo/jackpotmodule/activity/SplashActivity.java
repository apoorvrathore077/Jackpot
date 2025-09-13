package com.playzelo.jackpotmodule.activity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.databinding.ActivityJackpotSplashBinding;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    private ActivityJackpotSplashBinding binding;
    private String userId, username, auth_token;
    private final String LOG_TAG = "SplashJackpot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJackpotSplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                userId = extras.getString("userId", "");
                auth_token = extras.getString("auth_token", "");
                username = extras.getString("username", "");

                Log.d(LOG_TAG, "userId=" + userId + " auth_token=" + auth_token + " username=" + username);
            } else {
                Log.d(LOG_TAG, "No extras received!");
            }
        }

        Log.d(LOG_TAG, "UserId: " + userId + "Username: " + username);

        final Intent intentToStartDashboard = new Intent(SplashActivity.this, DashboardActivity.class);
        intentToStartDashboard.putExtra("userId", userId);
        intentToStartDashboard.putExtra("auth_token", auth_token);
        intentToStartDashboard.putExtra("username", username);

        new Handler().postDelayed(() -> {
            startActivity(intentToStartDashboard);
            finish();
        }, 4000);

        // Animation
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
        binding.appName.setVisibility(TextView.VISIBLE);
        binding.appName.startAnimation(animation);
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

}

//package com.playzelo.jackpotmodule.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.playzelo.jackpotmodule.R;
//import com.playzelo.jackpotmodule.databinding.ActivityJackpotSplashBinding;
//import com.playzelo.jackpotmodule.preferencemanager.SharedPrefManager;
//
//@SuppressLint("CustomSplashScreen")
//public class SplashActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        ActivityJackpotSplashBinding binding = ActivityJackpotSplashBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//
//        // SharedPreferences se token check karo
//        String token = SharedPrefManager.getInstance(this).getToken();
//
//        String LOG_TAG = "SplashJackpot";
//        Log.d(LOG_TAG, "Saved Token = " + token);
//
//        new Handler().postDelayed(() -> {
//            if (token != null) {
//                // Token hai -> direct Dashboard pe bhej
//                Intent dashboardIntent = new Intent(SplashActivity.this, DashboardActivity.class);
//                startActivity(dashboardIntent);
//                finish();
//            } else {
//                // Token nahi hai -> Login pe bhej
//                Intent loginIntent = new Intent(SplashActivity.this, LoginActivity.class);
//                startActivity(loginIntent);
//                finish();
//            }
//        }, 3000);
//
//        // Animation
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.slide_in_down);
//        binding.appName.setVisibility(TextView.VISIBLE);
//        binding.appName.startAnimation(animation);
//    }
//
//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            getWindow().getDecorView().setSystemUiVisibility(
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//            );
//        }
//    }
//}

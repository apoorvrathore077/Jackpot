package com.playzelo.jackpotmodule.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.databinding.ActivityJackpotDashboardBinding;
import com.playzelo.jackpotmodule.fragments.GamesFragment;

public class DashboardActivity extends AppCompatActivity {

    private String LOG_TAG = "DashboardActivity";
    private String userId, auth_token, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityJackpotDashboardBinding binding = ActivityJackpotDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                userId = extras.getString("userId", "");
                auth_token = extras.getString("auth_token", "");
                username = extras.getString("username", "");

                Log.d(LOG_TAG, "userId=" + userId + " auth_token=" + auth_token + " username=" + username);
            }else{
                Log.d(LOG_TAG, "No extras received!");
            }

            GamesFragment gamesFragment = GamesFragment.newInstance(userId, auth_token, username);


            // Load the GamesFragment directly
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, gamesFragment)
                    .commit();
        }
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

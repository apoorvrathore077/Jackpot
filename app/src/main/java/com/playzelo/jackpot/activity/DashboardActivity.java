package com.playzelo.jackpot.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpot.R;
import com.playzelo.jackpot.databinding.ActivityDashboardBinding;
import com.playzelo.jackpot.fragments.GamesFragment;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        ActivityDashboardBinding binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load the GamesFragment directly
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, new GamesFragment())
                .commit();
    }
}
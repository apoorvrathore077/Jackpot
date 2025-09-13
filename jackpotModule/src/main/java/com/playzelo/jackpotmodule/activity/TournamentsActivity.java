package com.playzelo.jackpotmodule.activity;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.databinding.ActivityJackpotTournamentsBinding;
import com.playzelo.jackpotmodule.fragments.ConfirmPaymentBottomSheet;

import java.util.ArrayList;

public class TournamentsActivity extends AppCompatActivity {


    private ActivityJackpotTournamentsBinding binding;
    private String userId, username, authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJackpotTournamentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.tabAll.setOnClickListener(v -> showToast("All selected"));
        binding.tabRegular.setOnClickListener(v -> showToast("Regular selected"));

        loadRecommendedTournaments();
        loadOtherTournaments();

        userId = getIntent().getStringExtra("userId");
        authToken = getIntent().getStringExtra("auth_token");
        username = getIntent().getStringExtra("username");

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    findViewById(R.id.fragmentContainer).setVisibility(View.GONE);
                    findViewById(R.id.mainContentLayout).setVisibility(View.VISIBLE);
                } else {
                    finish();
                }
            }
        });
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

    @SuppressLint("SetTextI18n")
    private void loadRecommendedTournaments() {
        ArrayList<Tournament> recommended = new ArrayList<>();
        recommended.add(new Tournament("₹3", "₹5", 9000));
        recommended.add(new Tournament("₹5", "₹10", 8000));
        recommended.add(new Tournament("₹10", "₹20", 7000));

        binding.layoutRecommendedTournaments.removeAllViews();

        TextView title = new TextView(this);
        title.setText("Recommended Tournaments");
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(16, 16, 16, 8);
        binding.layoutRecommendedTournaments.addView(title);

        for (Tournament t : recommended) {
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.item_jackpot_tournament_card, null);

            TextView tvPrize = view.findViewById(R.id.tvPrizePool);
            TextView tvEntry = view.findViewById(R.id.tvEntryFee);
            TextView tvTimer = view.findViewById(R.id.tvCountdown);

            tvPrize.setText(t.getPrizePool());
            tvEntry.setText(t.getEntryFee());

            startCountdown(tvTimer, t.getCountdownTime());

            tvEntry.setOnClickListener(v -> {
                String entryFee = tvEntry.getText().toString();
                ConfirmPaymentBottomSheet bottomSheet = ConfirmPaymentBottomSheet.newInstance(
                        entryFee,
                        userId,      // TournamentsActivity se jo set kiya tha
                        username,
                        authToken
                );                bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dpToPx(12));
            view.setLayoutParams(params);

            binding.layoutRecommendedTournaments.addView(view);
        }
    }

    @SuppressLint("SetTextI18n")
    private void loadOtherTournaments() {
        ArrayList<Tournament> others = new ArrayList<>();
        others.add(new Tournament("₹100", "₹50", 13000));
        others.add(new Tournament("₹75", "₹30", 7000));
        others.add(new Tournament("₹60", "₹20", 5000));

        binding.layoutOtherTournaments.removeAllViews();

        TextView title = new TextView(this);
        title.setText("Other Tournaments");
        title.setTextSize(16);
        title.setTypeface(null, Typeface.BOLD);
        title.setPadding(16, 16, 16, 8);
        binding.layoutOtherTournaments.addView(title);

        for (Tournament t : others) {
            @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.item_jackpot_other_tournament, null);

            TextView tvPrize = view.findViewById(R.id.tvPrizePool);
            TextView tvEntry = view.findViewById(R.id.tvEntryFee);
            TextView tvTimer = view.findViewById(R.id.tvCountdown);

            tvPrize.setText(t.getPrizePool());
            tvEntry.setText(t.getEntryFee());

            startCountdown(tvTimer, t.getCountdownTime());


            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, dpToPx(12));
            view.setLayoutParams(params);

            binding.layoutOtherTournaments.addView(view);
        }
    }

    private void startCountdown(TextView timerView, long time) {
        new CountDownTimer(time, 1000) {
            @SuppressLint("DefaultLocale")
            public void onTick(long millisUntilFinished) {
                long seconds = millisUntilFinished / 1000;
                timerView.setText(String.format("00m %02ds", seconds));
            }

            @SuppressLint("SetTextI18n")
            public void onFinish() {
                timerView.setText("00m 00s");
            }
        }.start();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private void loadFragment(Fragment fragment) {
        findViewById(R.id.mainContentLayout).setVisibility(View.GONE); // Hide main content
        findViewById(R.id.fragmentContainer).setVisibility(View.VISIBLE); // Show fragment

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    public static class Tournament {
        private final String prizePool;
        private final String entryFee;
        private final long countdownTime;

        public Tournament(String prizePool, String entryFee, long countdownTime) {
            this.prizePool = prizePool;
            this.entryFee = entryFee;
            this.countdownTime = countdownTime;
        }

        public String getPrizePool() {
            return prizePool;
        }

        public String getEntryFee() {
            return entryFee;
        }

        public long getCountdownTime() {
            return countdownTime;
        }
    }


}
package com.playzelo.jackpotmodule.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.apiservices.JackpotAPIClient;
import com.playzelo.jackpotmodule.apiservices.JackpotAPIServices;
import com.playzelo.jackpotmodule.databinding.ActivityJackpotGameBinding;
import com.playzelo.jackpotmodule.fragments.BetSelectionDialogFragment;
import com.playzelo.jackpotmodule.model.JackpotGameModels;
import com.playzelo.jackpotmodule.widget.Wheel3DView;

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity implements BetSelectionDialogFragment.BetSelectionListener {

    private ActivityJackpotGameBinding binding;

    private MediaPlayer spinSoundPlayer;

    private JackpotAPIServices apiService;
    private String currentGameId = null;
    private String userId, username, authToken, openFragment, entryFee;

    private double balance = 1000.0;
    private double currentBet = 3.0;

    private Wheel3DView slot1, slot2, slot3;
    private final int[] slotImages = {R.drawable.ic_slot1, R.drawable.ic_slot2, R.drawable.ic_slot3, R.drawable.ic_slot4, R.drawable.ic_slot5};

    // Spin coordination fields
    private int reelsRemaining = 0;
    private int[] lastSlotResults = new int[3];
    private double lastWinnings = 0.0;
    private double lastBalance = 0.0;
    private String lastResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityJackpotGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        handler.post(checkRunnable);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);

        spinSoundPlayer = MediaPlayer.create(this, R.raw.spin_sound_1);

        entryFee = getIntent().getStringExtra("entry_fee");
        userId = getIntent().getStringExtra("userId");
        username = getIntent().getStringExtra("username");
        authToken = getIntent().getStringExtra("auth_token");
        openFragment = getIntent().getStringExtra("openFragment");

        initializeGameRoom();
        initUI();
        setupListeners();
        Log.d("Userid: ", userId);
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

    private void playSpinSound() {
        if (spinSoundPlayer == null) {
            spinSoundPlayer = MediaPlayer.create(this, R.raw.spin_sound);
            spinSoundPlayer.setLooping(true);
        }
        if (spinSoundPlayer != null) {
            if (spinSoundPlayer.isPlaying()) {
                spinSoundPlayer.seekTo(0);
            } else {
                spinSoundPlayer.start();
            }
        }
    }

    private void stopSpinSound() {
        if (spinSoundPlayer != null) {
            spinSoundPlayer.pause();
            spinSoundPlayer.seekTo(0);

        }
    }

    @SuppressLint("SetTextI18n")
    private void initUI() {
        updateBalance();

        assert binding.betAmount != null;
        binding.betAmount.setText("₹ " + currentBet);
        assert binding.winAmount != null;
        binding.winAmount.setText("₹ 0.000");

        if (slot1 != null) {
            slot1.setItems(slotImages);
            slot1.setCurrentItem(0);
        }
        if (slot2 != null) {
            slot2.setItems(slotImages);
            slot2.setCurrentItem(0);
        }
        if (slot3 != null) {
            slot3.setItems(slotImages);
            slot3.setCurrentItem(0);
        }

    }

    private void setupListeners() {
        assert binding.spinButton != null;
        binding.spinButton.setOnClickListener(v -> {
            if (balance < currentBet) {
                openDialogFragment();
                return;
            }
            balance -= currentBet;
            updateBalance();
            playSpinSound();
            startSpin();
        });

        assert binding.btnAdd != null;
        binding.btnAdd.setOnClickListener(v -> openBetSelectionDialog());
    }

    @SuppressLint("SetTextI18n")
    private void startSpin() {
        userId = getIntent().getStringExtra("userId");
        if (userId == null || currentGameId == null) {
            binding.resultText.setText("session expired");
            Log.e("GameActivity", "No userId or gameId found");
            return;
        }

        binding.spinButton.setEnabled(false);
        binding.spinButton.setText("");
        binding.resultText.setText("Spinning...");
        playSpinSound();

        Log.d("GameActivity", "Calling spin API for gameId: " + currentGameId);

        Call<JackpotGameModels.SpinGameResponse> call = apiService.spinGame(new JackpotGameModels.SpinGameRequest(userId, currentGameId));
        call.enqueue(new Callback<>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<JackpotGameModels.SpinGameResponse> call, @NonNull Response<JackpotGameModels.SpinGameResponse> response) {
                stopSpinSound();

                if (response.isSuccessful() && response.body() != null) {
                    JackpotGameModels.SpinGameResponse spin = response.body();

                    Log.d("GameActivity", "Spin API Success!");
                    Log.d("GameActivity", "Symbols received:");
                    for (int i = 0; i < spin.symbols.length; i++) {
                        Log.d("GameActivity", "Slot " + (i + 1) + ": " + spin.symbols[i].name);
                    }
                    Log.d("GameActivity", "Result: " + spin.result);
                    Log.d("GameActivity", "Winnings: " + spin.winnings);
                    Log.d("GameActivity", "Updated Balance: " + spin.balance);

                    // Map symbols to slot indices
                    int[] slotResults = new int[3];
                    for (int i = 0; i < 3; i++) {
                        slotResults[i] = symbolToIndex(spin.symbols[i]);
                    }

                    // Animate reels and wait for completion before showing results
                    reelsRemaining = 3;
                    lastSlotResults = slotResults;
                    lastWinnings = spin.winnings;
                    lastBalance = spin.balance;
                    lastResult = spin.result;

                    slot1.setOnSpinCompleteListener(finalIndex -> onReelStopped());
                    slot2.setOnSpinCompleteListener(finalIndex -> onReelStopped());
                    slot3.setOnSpinCompleteListener(finalIndex -> onReelStopped());

                    slot1.spin(slotResults[0]);
                    slot2.spin(slotResults[1]);
                    slot3.spin(slotResults[2]);

                } else {
                    String errorJson = "{}";
                    try {
                        if (response.errorBody() != null) {
                            errorJson = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("GameActivity", "Error reading error body", e);
                    }

                    Log.e("GameActivity", "Spin API Error - Code: " + response.code() + ", JSON: " + errorJson);
                    binding.resultText.setText("Error: " + errorJson);
                }

                // Re-enable after reels finish in onAllReelsStopped
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<JackpotGameModels.SpinGameResponse> call, @NonNull Throwable t) {
                stopSpinSound();
                Log.e("GameActivity", "Spin API Failure: " + t.getLocalizedMessage(), t);
                binding.resultText.setText("Connection Failed: " + t.getLocalizedMessage());
                binding.spinButton.setEnabled(true);
                binding.spinButton.setText(R.string.spin);
            }
        });
    }

    private int symbolToIndex(JackpotGameModels.Symbol symbol) {
        switch (symbol.name) {
            case "Slot1":
                return 0;
            case "Slot2":
                return 1;
            case "Slot3":
                return 2;
            case "Slot4":
                return 3;
            case "Slot5":
                return 4;
            default:
                return 0;
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void simulateSpinResult(int result1, int result2, int result3) {
        int actualResult1 = slot1 != null ? slot1.getCurrentItem() : result1;
        int actualResult2 = slot2 != null ? slot2.getCurrentItem() : result2;
        int actualResult3 = slot3 != null ? slot3.getCurrentItem() : result3;

        double winAmount = 0.0;

        // WIN CONDITION: All 4 columns must have exactly same icon index
        if (actualResult1 == actualResult2 && actualResult2 == actualResult3) {

            double multiplier;

            // Determine multiplier based on the winning icon index
            if (actualResult1 == 0) { // ic_slot1
                multiplier = 100.0;
            } else if (actualResult1 == 1) { // ic_slot3
                multiplier = 50.0;
            } else if (actualResult1 == 2) {
                multiplier = 25.0;
            } else if (actualResult1 == 3) {
                multiplier = 20.0;
            } else {
                // Default win multiplier for other matching symbols
                multiplier = 10.0;
            }

            // Calculate win amount with the multiplier
            winAmount = currentBet * multiplier;
            balance += winAmount;

            // Call a method to visually highlight the winning multiplier
            assert binding.resultText != null;
            binding.resultText.setText("JACKPOT! " + (int) multiplier + "x Multiplier!");
        } else {
            // Loss Logic - Not all 4 same
            assert binding.resultText != null;
            binding.resultText.setText("Better Luck Next Time - Not All Match");
        }
        checkResultAndHighlight();

        updateBalance();
        assert binding.winAmount != null;
        binding.winAmount.setText("₹ " + String.format("%.3f", winAmount));
        assert binding.spinButton != null;
        binding.spinButton.setEnabled(true);
        binding.spinButton.setText(R.string.spin);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void updateBalance() {
        assert binding.balanceAmount != null;
        binding.balanceAmount.setText("₹ " + String.format("%.3f", balance));
    }

    @Override
    protected void onDestroy() {
        if (spinSoundPlayer != null) {
            spinSoundPlayer.release();
            spinSoundPlayer = null;
        }
        super.onDestroy();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBetSelected(double newBetAmount) {
        currentBet = newBetAmount;
        assert binding.betAmount != null;
        binding.betAmount.setText("₹ " + currentBet);
    }

    private void checkResultAndHighlight() {
        int finalSlot1 = slot1.getCurrentItem();
        int finalSlot2 = slot2.getCurrentItem();
        int finalSlot3 = slot3.getCurrentItem();

        resetMultiplierHighlight();

        if (finalSlot1 == finalSlot2 && finalSlot2 == finalSlot3) {
            switch (finalSlot1) {
                case 0:
                    assert binding.x100Multiplier != null;
                    highLightTextView(binding.x100Multiplier);
                    double winAmount100 = currentBet * 10;
                    balance += winAmount100;
                    break;
                case 1:
                    assert binding.x50Multiplier != null;
                    highLightTextView(binding.x50Multiplier);
                    double winAmount50 = currentBet * 8;
                    balance += winAmount50;
                    break;
                case 2:
                    assert binding.x25Multiplier != null;
                    highLightTextView(binding.x25Multiplier);
                    double winAmount25 = currentBet * 6;
                    balance += winAmount25;
                    break;
                case 3:
                    assert binding.x20Multiplier != null;
                    highLightTextView(binding.x20Multiplier);
                    double winAmount20 = currentBet * 4;
                    balance += winAmount20;
                    break;
                case 4:
                    assert binding.x10Multiplier != null;
                    highLightTextView(binding.x10Multiplier);
                    double winAmount10 = currentBet * 2;
                    balance += winAmount10;
                    break;
            }
        }
        updateBalance();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void onReelStopped() {
        reelsRemaining--;
        if (reelsRemaining > 0) {
            return;
        }

        // All reels stopped
        stopSpinSound();

        // Update balance and win amount only now
        balance = lastBalance;
        updateBalance();
        binding.winAmount.setText("₹ " + String.format("%.3f", lastWinnings));

        resetMultiplierHighlight();
        if ("win".equals(lastResult)) {
            switch (lastSlotResults[0]) {
                case 0:
                    highLightTextView(binding.x100Multiplier);
                    break;
                case 1:
                    highLightTextView(binding.x50Multiplier);
                    break;
                case 2:
                    highLightTextView(binding.x25Multiplier);
                    break;
                case 3:
                    highLightTextView(binding.x20Multiplier);
                    break;
                case 4:
                    highLightTextView(binding.x10Multiplier);
                    break;
            }
            binding.resultText.setText("You Win!");
        } else {
            binding.resultText.setText("Better Luck Next Time");
        }

        binding.spinButton.setEnabled(true);
        binding.spinButton.setText(R.string.spin);
    }

    private void initializeGameRoom() {
        apiService = JackpotAPIClient.getClient().create(JackpotAPIServices.class);
        createInitialGame(userId);
    }

    @SuppressLint("SetTextI18n")
    private void createInitialGame(String userId) {
        if (userId == null) {
            binding.resultText.setText("No userId from parent app");
            Log.e("GameActivity", "UserId is null, cannot create game");
            return;
        }

        Log.d("GameActivity", "UserId: " + userId);
        Log.d("GameActivity", "Current Bet: " + currentBet);

        // Show temporary loading text
        binding.resultText.setText("Creating game...");

        JackpotGameModels.CreateGameRequest request = new JackpotGameModels.CreateGameRequest(userId, currentBet);
        Call<JackpotGameModels.CreateGameResponse> call = apiService.createGame(request);

        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<JackpotGameModels.CreateGameResponse> call, @NonNull Response<JackpotGameModels.CreateGameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentGameId = response.body().gameId;
                    balance = response.body().balance;

                    // Update balance on UI
                    updateBalance();

                    // Detailed logging
                    Log.d("GameActivity", "✅ Game Created Successfully");
                    Log.d("GameActivity", "Game ID: " + currentGameId);
                    Log.d("GameActivity", "Balance: " + balance);
                    Log.d("GameActivity", "Full Response: " + new Gson().toJson(response.body()));

                    binding.resultText.setText("Game Ready! Press Spin to win");
                } else {
                    // Log API error with possible error body
                    try {
                        String errorMsg = response.errorBody() != null ? response.errorBody().string() : "No error body";
                        Log.e("GameActivity", "API Error: Code=" + response.code() + " Message=" + errorMsg);
                    } catch (IOException e) {
                        Log.e("GameActivity", "Error reading API error body", e);
                    }
                    binding.resultText.setText("Failed to create game. Try again!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<JackpotGameModels.CreateGameResponse> call, @NonNull Throwable t) {
                Log.e("GameActivity", "Network Error: " + t.getLocalizedMessage(), t);
                binding.resultText.setText("Network error. Check your connection!");
            }
        });
    }

    private void highLightTextView(TextView texView) {
        texView.setTextColor(Color.RED);
    }

    private void resetMultiplierHighlight() {
        assert binding.x100Multiplier != null;
        binding.x100Multiplier.setTextColor(Color.WHITE);
        assert binding.x50Multiplier != null;
        binding.x50Multiplier.setTextColor(Color.WHITE);
        assert binding.x25Multiplier != null;
        binding.x25Multiplier.setTextColor(Color.WHITE);
        assert binding.x20Multiplier != null;
        binding.x20Multiplier.setTextColor(Color.WHITE);
        assert binding.x10Multiplier != null;
        binding.x10Multiplier.setTextColor(Color.WHITE);
    }

    private void openDialogFragment() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insufficient_balance);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        Button buttonOk = dialog.findViewById(R.id.btn_ok);
        buttonOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void openBetSelectionDialog() {
        if (getSupportFragmentManager().findFragmentByTag("BetSelectionDialog") != null) {
            return;
        }
        BetSelectionDialogFragment dialogFragment = new BetSelectionDialogFragment();
        dialogFragment.setOnBetSelectedListener(selectedBet -> {
            currentBet += selectedBet;
            assert binding.betAmount != null;
            binding.betAmount.setText("₹ " + currentBet);
        });
        dialogFragment.show(getSupportFragmentManager(), "BetSelectionDialog");
    }
}

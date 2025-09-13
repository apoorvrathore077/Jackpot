package com.playzelo.jackpot.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpot.R;
import com.playzelo.jackpot.databinding.ActivityGameBinding;
import com.playzelo.jackpot.fragments.BetSelectionDialogFragment;
import com.playzelo.jackpot.helper.LotteryHelper;
import com.playzelo.jackpot.widget.Wheel3DView;

import java.util.Objects;
import java.util.Random;

public class GameActivity extends AppCompatActivity implements BetSelectionDialogFragment.BetSelectionListener {

    private ActivityGameBinding binding;

    private MediaPlayer spinSoundPlayer;

    private final Random random = new Random();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private double balance = 1000.0;
    private double currentBet = 3.0;

    private Wheel3DView slot1, slot2, slot3, slot4;
    private final int[] slotImages = {R.drawable.ic_slot1, R.drawable.ic_slot2, R.drawable.ic_slot3, R.drawable.ic_slot4, R.drawable.ic_slot5};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        handler.post(checkRunnable);
        slot1 = findViewById(R.id.slot1);
        slot2 = findViewById(R.id.slot2);
        slot3 = findViewById(R.id.slot3);
        slot4 = findViewById(R.id.slot4);

        spinSoundPlayer = MediaPlayer.create(this, R.raw.spin_sound);


        initUI();
        setupListeners();
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
        if (slot4 != null) {
            slot4.setItems(slotImages);
            slot4.setCurrentItem(0);
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
    private void openTicket() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_lottery_dialog);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.show();
        TextView txtTicketNumber = dialog.findViewById(R.id.txtTicketNumber);
        ImageView betIcon = dialog.findViewById(R.id.betIcon);

        txtTicketNumber.setText(LotteryHelper.generateTicketNumber());
        betIcon.setImageResource(LotteryHelper.getRandomSlotIcon());
    }


    @SuppressLint("SetTextI18n")
    private void startSpin() {
        assert binding.spinButton != null;
        assert binding.resultText != null;
        binding.spinButton.setEnabled(false);
        binding.spinButton.setText("");
        binding.resultText.setText("Spinning...");

        int result1, result2, result3, result4;
        boolean shouldWin = random.nextBoolean();
        if (shouldWin) {
            int winningIcon = random.nextInt(slotImages.length);
            result1 = winningIcon;
            result2 = winningIcon;
            result3 = winningIcon;
            result4 = winningIcon;
        } else {
            result1 = random.nextInt(slotImages.length);
            result2 = random.nextInt(slotImages.length);
            result3 = random.nextInt(slotImages.length);
            result4 = random.nextInt(slotImages.length);
            if (result1 == result2 && result2 == result3 && result3 == result4) {
                do {
                    result4 = random.nextInt(slotImages.length);
                } while (result4 == result1);
            }
        }
        final int finalResult1 = result1;
        final int finalResult2 = result2;
        final int finalResult3 = result3;
        final int finalResult4 = result4;
        slot1.spin(result1);
        slot2.spin(result2);
        slot3.spin(result3);
        slot4.spin(result4);

        handler.postDelayed(() -> {
            stopSpinSound();
            simulateSpinResult(finalResult1, finalResult2, finalResult3, finalResult4);
        }, 3000);

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void simulateSpinResult(int result1, int result2, int result3, int result4) {
        // Get actual final positions from slots (after animation)
        int actualResult1 = slot1 != null ? slot1.getCurrentItem() : result1;
        int actualResult2 = slot2 != null ? slot2.getCurrentItem() : result2;
        int actualResult3 = slot3 != null ? slot3.getCurrentItem() : result3;
        int actualResult4 = slot4 != null ? slot4.getCurrentItem() : result4;

        double winAmount = 0.0;

        // WIN CONDITION: All 4 columns must have exactly same icon index
        if (actualResult1 == actualResult2 && actualResult2 == actualResult3 && actualResult3 == actualResult4) {

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
            binding.resultText.setText("🎉 JACKPOT! " + (int) multiplier + "x Multiplier! 🎉");
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
//        handler.removeCallbacks(checkRunnable);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBetSelected(double newBetAmount) {
        currentBet = newBetAmount;
        assert binding.betAmount != null;
        binding.betAmount.setText("₹ " + currentBet);
    }

    //    private final Runnable checkRunnable = new Runnable() {
//        @Override
//        public void run() {
//            Calendar calendar = Calendar.getInstance();
//
//            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));
//            int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
//            int currentMin = calendar.get(Calendar.MINUTE);
//
//            if (currentHour >= 18 && currentMin >= 23 && currentMin < 25) {
//                if (binding != null && binding.imgLock != null) {
//                    binding.imgLock.setVisibility(View.GONE);
//                    binding.spinButton.setEnabled(true);
//                }
//            } else {
//                if (binding != null && binding.imgLock != null) {
//                    binding.imgLock.setVisibility(View.VISIBLE);
//                    binding.spinButton.setEnabled(false);
//                }
//            }
//            handler.postDelayed(this, 1000);
//
//
//        }
//    };
    private void checkResultAndHighlight() {
        int finalSlot1 = slot1.getCurrentItem();
        int finalSlot2 = slot2.getCurrentItem();
        int finalSlot3 = slot3.getCurrentItem();
        int finalSlot4 = slot4.getCurrentItem();

        resetMultiplierHighlight();

        if (finalSlot1 == finalSlot2 && finalSlot2 == finalSlot3 && finalSlot3 == finalSlot4) {
            switch (finalSlot1) {
                case 0:
                    assert binding.x100Multiplier != null;
                    highLightTextView(binding.x100Multiplier, Color.RED);
                    double winAmount100 = currentBet * 100;
                    balance += winAmount100;
                    break;
                case 1:
                    assert binding.x50Multiplier != null;
                    highLightTextView(binding.x50Multiplier, Color.RED);
                    double winAmount50 = currentBet * 50;
                    balance += winAmount50;
                    break;
                case 2:
                    assert binding.x25Multiplier != null;
                    highLightTextView(binding.x25Multiplier, Color.RED);
                    double winAmount25 = currentBet * 25;
                    balance += winAmount25;
                    break;
                case 3:
                    assert binding.x20Multiplier != null;
                    highLightTextView(binding.x20Multiplier, Color.RED);
                    double winAmount20 = currentBet * 20;
                    balance += winAmount20;
                    break;
                case 4:
                    assert binding.x10Multiplier != null;
                    highLightTextView(binding.x10Multiplier, Color.RED);
                    double winAmount10 = currentBet * 10;
                    balance += winAmount10;
                    break;
            }
        }
        updateBalance();
    }

    private void highLightTextView(TextView texView, int color) {
        texView.setTextColor(color);
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

// You will also need to add this method to your GameActivity class


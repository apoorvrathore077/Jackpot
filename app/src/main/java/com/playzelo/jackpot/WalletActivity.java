package com.playzelo.jackpot;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.playzelo.jackpot.databinding.ActivityWalletBinding;


public class WalletActivity extends AppCompatActivity {
    private ActivityWalletBinding binding;
    EditText etAmount,etWithdrawAmount;
    AlertDialog dialog;
    private int walletBalance = 2500;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.txtBalanceAmount.setText("₹ 12050");
        onClickListener();
        updateWalletBalanceUI();
    }

    @SuppressLint("SetTextI18n")
    private void updateWalletBalanceUI() {
        binding.txtBalanceAmount.setText("₹"+walletBalance);
    }


    private void onClickListener() {
        binding.btnAddMoney.setOnClickListener(view -> addMoney());
        binding.btnWithdrawMoney.setOnClickListener(view -> withdrawMoney());
    }

    private void withdrawMoney() {
        showWithdrawDialog();
    }

    private void showWithdrawDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogueView = getLayoutInflater().inflate(R.layout.withdraw_money_dialog,null);
        builder.setView(dialogueView);

        etWithdrawAmount = dialogueView.findViewById(R.id.etWithdrawAmount);
        Button btnWithdraw = dialogueView.findViewById(R.id.btnWithdraw);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_orange_dark);
        dialog.show();

        btnWithdraw.setOnClickListener(view -> withdrawFromWallet());
    }

    private void withdrawFromWallet() {
        String withDrawAmount = etWithdrawAmount.getText().toString().trim();
        if (withDrawAmount.isEmpty()){
            etWithdrawAmount.setError("Enter Amount");
            return;
        }
        int amount = Integer.parseInt(withDrawAmount);
        if (amount < 1000){
            etWithdrawAmount.setError("Minimum ₹1000 require to withdraw");
            return;
        }
        if(amount > walletBalance){
           showToast("Insufficient Balance");
        }
        walletBalance -= amount;
        updateWalletBalanceUI();
        showToast("₹"+amount+"Withdrawal requested");
        dialog.dismiss();
    }

    private void addMoney() {
        showAddMoneyDialogue();
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void showAddMoneyDialogue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogueView = getLayoutInflater().inflate(R.layout.add_money_dialogue,null);
        builder.setView(dialogueView);

        etAmount = dialogueView.findViewById(R.id.etAmount);
        Button btnProceed = dialogueView.findViewById(R.id.btnProceed);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.holo_orange_dark);
        dialog.show();

        btnProceed.setOnClickListener(view -> addMoneyToWallet());

    }

    @SuppressLint("SetTextI18n")
    private void addMoneyToWallet() {
        String amount = etAmount.getText().toString().trim();
        if (!amount.isEmpty()){
            showToast("₹"+amount+"added to wallet");
            binding.txtBalanceAmount.setText("₹"+amount);
            dialog.dismiss();
        }else {
            etAmount.setError("Enter Amount");
        }
    }

}
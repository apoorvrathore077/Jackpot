package com.playzelo.jackpotmodule.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.playzelo.jackpotmodule.R;

public class BetSelectionDialogFragment extends DialogFragment {


    private EditText customBetInput;

    public interface BetSelectionListener {
        void onBetSelected(double betAmount);
    }

    private BetSelectionListener listener;

    public void setOnBetSelectedListener(BetSelectionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_bet_selection, null);
        builder.setView(view);

        Button bet1Button = view.findViewById(R.id.bet_1);
        Button bet2Button = view.findViewById(R.id.bet_2);
        Button bet3Button = view.findViewById(R.id.bet_3);
        Button bet4Button = view.findViewById(R.id.bet_4);
        Button cancelButton = view.findViewById(R.id.btnCancel);
        Button customConfirmButton = view.findViewById(R.id.custom_bet_confirm);
        customBetInput = view.findViewById(R.id.custom_bet_input);

        // Fixed bet buttons
        bet1Button.setOnClickListener(v -> handleBetSelection(100));
        bet2Button.setOnClickListener(v -> handleBetSelection(200));
        bet3Button.setOnClickListener(v -> handleBetSelection(500));
        bet4Button.setOnClickListener(v -> handleBetSelection(1000));

        // Custom bet button
        customConfirmButton.setOnClickListener(v -> {
            String input = customBetInput.getText().toString().trim();
            if (validateCustomBet(input)) {
                double customBet = Double.parseDouble(input);
                handleBetSelection(customBet);
            }
        });

        // Cancel button
        cancelButton.setOnClickListener(v -> dismiss());

        return builder.create();
    }

    private boolean validateCustomBet(String input) {
        if (TextUtils.isEmpty(input)) {
            customBetInput.setError("Please Enter Valid Amount");
            return false;
        }
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 100) {
                customBetInput.setError("Amount must be greater than ₹100");
                return false;
            }
        } catch (NumberFormatException e) {
            customBetInput.setError("Invalid Input");
            return false;
        }
        return true;
    }

    private void handleBetSelection(double betAmount) {
        if (listener != null) {
            listener.onBetSelected(betAmount);
        }
        dismiss();
    }
}

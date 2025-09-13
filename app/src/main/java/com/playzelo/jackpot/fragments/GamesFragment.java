package com.playzelo.jackpot.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.playzelo.jackpot.activity.GameActivity;
import com.playzelo.jackpotmodule.databinding.FragmentJackpotGamesBinding;

public class GamesFragment extends Fragment {

    private FragmentJackpotGamesBinding binding;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJackpotGamesBinding.inflate(inflater, container, false);
        binding.button.setOnClickListener(v -> openGameActivity());
        return binding.getRoot();
    }


    private void openGameActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), GameActivity.class);
            startActivity(intent);
        }
    }
}
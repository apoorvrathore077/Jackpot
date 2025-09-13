package com.playzelo.jackpotmodule.fragments;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.playzelo.jackpotmodule.R;
import com.playzelo.jackpotmodule.activity.TournamentsActivity;
import com.playzelo.jackpotmodule.databinding.FragmentJackpotGamesBinding;

public class GamesFragment extends Fragment {

    private static final String ARG_USER_ID = "userId";
    private static final String ARG_AUTH_TOKEN = "auth_token";

    private static final String ARG_USERNAME = "username";

    private FragmentJackpotGamesBinding binding;
    private int[] frames;
    private int frameIndex = 0;
    private MediaPlayer gameSound;
    private boolean isAnimating = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentJackpotGamesBinding.inflate(inflater, container, false);
        initializeSound();
        binding.button.setOnClickListener(v -> openTournamentsActivity());
        Animation pulse = AnimationUtils.loadAnimation(requireContext(), R.anim.pulse_scale);
        binding.button.startAnimation(pulse);
        playSound();
        return binding.getRoot();
    }


    private void openTournamentsActivity() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), TournamentsActivity.class);
            if (getArguments() != null) {
                intent.putExtra(ARG_USER_ID, getArguments().getString(ARG_USER_ID));
                intent.putExtra(ARG_AUTH_TOKEN, getArguments().getString(ARG_AUTH_TOKEN));
                intent.putExtra(ARG_USERNAME, getArguments().getString(ARG_USERNAME));
            }
            startActivity(intent);
        }
    }


    private void initializeSound() {
        if (gameSound != null) {
            gameSound = MediaPlayer.create(requireContext(), R.raw.game_sound);
        }
    }

    private void playSound() {
        if (gameSound != null && !gameSound.isPlaying()) {
            gameSound.start();
        }
    }

    public static GamesFragment newInstance(String userId, String authToken, String username) {
        GamesFragment fragment = new GamesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putString(ARG_AUTH_TOKEN, authToken);
        args.putString(ARG_USERNAME, username);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (gameSound != null && gameSound.isPlaying()) {
            gameSound.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gameSound != null && !gameSound.isPlaying()) {
            gameSound.seekTo(0);
            gameSound.start();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (gameSound != null) {
            gameSound.release();
            gameSound = null;
        }

    }
}
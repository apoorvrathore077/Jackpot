package com.playzelo.jackpot.helper;

import com.playzelo.jackpot.R;

import java.util.Random;


public class LotteryHelper {

    private static final int[] SLOT_ICONS = {
            R.drawable.ic_slot1,
            R.drawable.ic_slot2,
            R.drawable.ic_slot3,
            R.drawable.ic_slot4,
            R.drawable.ic_slot5
    };

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateTicketNumber() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static int getRandomSlotIcon() {
        Random random = new Random();
        return SLOT_ICONS[random.nextInt(SLOT_ICONS.length)];
    }
}


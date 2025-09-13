package com.playzelo.jackpotmodule.model;


import com.google.gson.annotations.SerializedName;

public class JackpotGameModels {

    public static class CreateGameRequest {
        public String userId;
        public double bet;
        public CreateGameRequest(String userId,double bet) {
            this.userId = userId;
            this.bet = bet;
        }
    }

    public static class CreateGameResponse {
        public String gameId;
        public double balance;
    }

    public static class SpinGameResponse {
        public String gameId;
        public Symbol[] symbols;
        public String result;
        public double winnings;
        @SerializedName("currentBalance")
        public double balance;
    }

    public static class Symbol {
        public String name;
        public String image;
    }
    public static class GameStatusResponse{

    }
    public static class SpinGameRequest{
        private String userId;
        private String gameId;

        public SpinGameRequest(String userId, String gameId) {
            this.userId = userId;
            this.gameId = gameId;
        }
    }
}


package com.playzelo.jackpotmodule.apiservices;

public class RequestModel {
    public static class SignupRequest {
        private String username;
        private String email;
        private String password;

        public SignupRequest(String username, String email, String password) {
            this.username = username;
            this.email = email;
            this.password = password;
        }
    }

    public static class LoginRequest {
        private String username;
        private String password;

        public LoginRequest(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

}

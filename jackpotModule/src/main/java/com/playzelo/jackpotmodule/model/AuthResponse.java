package com.playzelo.jackpotmodule.model;

public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private User user;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public User getUser() { return user; }

    public class User {
        private String id;
        private String username;
        private String email;

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }
}


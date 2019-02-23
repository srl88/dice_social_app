package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

public class Player {
    // Attributes
    private String name;
    private int token;

    // Constructor
    public Player(String name, int token) {
        this.name = name;
        this.token = token;
    }

    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }
}
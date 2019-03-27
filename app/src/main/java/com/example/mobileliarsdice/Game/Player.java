package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

public class Player {
    // Attributes
    private String name;

    // Constructor
    public Player(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public String toString() {
        return "name: " + name;
    }
    
    
}
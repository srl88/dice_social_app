package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

public class Die {
    // Attributes
    private int face;
    
    // Constructor
    public Die(int face) {
        this.face = face;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    // Set face value to random number between 1 and 6
    public void roll() {
        this.face = (int) (Math.random()*(6)) + 1;
    }

    // To string
    public String toString() {
        return Integer.toString(face);
    }
}
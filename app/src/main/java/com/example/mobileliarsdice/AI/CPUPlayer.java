package com.example.mobileliarsdice.AI;

public abstract class CPUPlayer {

    public CPUPlayer() {}

    public String toString() {
        return this.getClass().getSimpleName();
    }
    public abstract String bid(int yourId, int[] diceEachPlayerHas, int[] yourDice, String[] bids);
}
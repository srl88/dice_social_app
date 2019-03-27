package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

import java.util.ArrayList;

public class Cup {
    // Attributes
    private ArrayList<Die> cup;
    private int diceNumber

    // Constructor: put five dice in a cup
    public Cup() {
        cup = new ArrayList<Die>();
        for (int i = 0; i < 5; i++) {
            Die die = new Die(1);
            cup.add(die);
        }
        diceNumber = 5;
    }

    public ArrayList<Die> getCup() {
        return cup;
    }
    
    public PokerDiceHand getHand() {
    	PokerDiceHand hand = new PokerDiceHand(cup.get(0), cup.get(1), cup.get(2), cup.get(3), cup.get(4));
    	return hand;
    }

    public int getDiceNumber() {
        return diceNumber;
    }

    // Roll n number of dice inside the cup
    public void roll(boolean first, boolean second, boolean third, boolean fourth, boolean fifth) {
        if(first) {
            cup.get(0).roll();
        }
        if(second) {
            cup.get(1).roll();
        }
        if(third) {
            cup.get(2).roll();
        }
        if(fourth) {
            cup.get(3).roll();
        }
        if(fifth) {
            cup.get(4).roll();
        }
    }

    // Roll all dice inside the cup
    public void shake() {
    	for (int i = 0; i < cup.size(); i++) {
    		cup.get(i).roll();
        }
    }

    // Take out a die from the cup
    public void takeOut() {
        if (cup.size() > 0) {
            cup.remove(0);
        }
        diceNumber-=1;
    }

    // To string
    public String toString() {
        return cup.toString();
    }
}
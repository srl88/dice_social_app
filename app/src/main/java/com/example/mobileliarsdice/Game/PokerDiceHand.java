package com.example.mobileliarsdice.Game;

import java.util.ArrayList;

/**
 * Created by Sung Won Caleb Bhyun
 */

public class PokerDiceHand {
    // Attributes
    private ArrayList<Die> fiveDice;
    private ArrayList<Die> orderedDice;
    private String hand;
    private int rank;


    // Constructor for getting a hand for a cup
    public PokerDiceHand(Cup cup) {
        fiveDice = new ArrayList<Die>();
        fiveDice.add(cup.getCup().get(0));
        fiveDice.add(cup.getCup().get(1));
        fiveDice.add(cup.getCup().get(2));
        fiveDice.add(cup.getCup().get(3));
        fiveDice.add(cup.getCup().get(4));
        getHand();
    }

    // Constructor for getting a hand without a cup
    public PokerDiceHand(Die first, Die second, Die third, Die fourth, Die fifth) {
        fiveDice = new ArrayList<Die>();
        fiveDice.add(first);
        fiveDice.add(second);
        fiveDice.add(third);
        fiveDice.add(fourth);
        fiveDice.add(fifth);
        getHand();
    }

    // Constructor for getting a hand string with rank
    public PokerDiceHand(int rank) {
        switch(rank) {
            case 1:
                hand = "Five of a kind";
                break;
            case 2:
                hand = "Four of a kind";
                break;
            case 3:
                hand = "Full house";
                break;
            case 4:
                hand = "High straight";
                break;
            case 5:
                hand = "Low straight";
                break;
            case 6:
                hand = "Three of a kind";
                break;
            case 7:
                hand = "Two pair";
                break;
            case 8:
                hand = "One pair";
                break;
            case 9:
                hand = "6 high";
                break;
            case 10:
                hand = "5 high";
                break;
            case 11:
                hand = "4 high";
                break;
            case 12:
                hand = "3 high";
                break;
            case 13:
                hand = "2 high";
                break;
            case 14:
                hand = "1 high";
                break;
        }
    }

    public String toString() {
        return hand;
    }

    public String getHand() {
        orderByFace();
        if (isFiveOfAKind() == true) {
            hand = "Five of a kind";
            rank = 1;
        } else if (isFourOfAKind() == true) {
            hand = "Four of a kind";
            rank = 2;
        } else if (isFullHouse() == true) {
            hand = "Full house";
            rank = 3;
        } else if (isHighStraight() == true) {
            hand = "High straight";
            rank = 4;
        } else if (isLowStraight() == true) {
            hand = "Low straight";
            rank = 5;
        } else if (isThreeOfAKind() == true) {
            hand = "Three of a kind";
            rank = 6;
        } else if (isTwoPair() == true) {
            hand = "Two pair";
            rank = 7;
        } else if (isOnePair() == true) {
            hand = "One pair";
            rank = 8;
        } else {
            hand = Integer.toString(orderedDice.get(4).getFace()) + " high";
            rank = 9 + (6 - orderedDice.get(4).getFace());
        }
        return hand;
    }

    public int getRank() {
        return rank;
    }



    public void orderByFace() {
        orderedDice = new ArrayList<Die>();
        int current = 0;
        int index;
        while(orderedDice.size() != 5) {
            Die die = new Die(fiveDice.get(current).getFace());
            index = 0;
            for (int i = 0; i < orderedDice.size(); i++) {
                if (die.getFace() > (orderedDice.get(index).getFace())) {
                    index++;
                }
            }
            orderedDice.add(index, die);
            current++;
        }
    }

    // Check if Five of a kind
    public boolean isFiveOfAKind() {
        if (orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace()) {
            return true;
        } else {
            return false;
        }
    }

    // Check if Four of a kind
    public boolean isFourOfAKind() {
        if ((orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace())
                || (orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())) {
            return true;
        } else {
            return false;
        }
    }

    // Check if Full house
    public boolean isFullHouse() {
        if ((orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())
                || (orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())) {
            return true;
        } else {
            return false;
        }
    }

    // Check if High straight
    public boolean isHighStraight() {
        if (orderedDice.get(0).getFace() == 2
                && orderedDice.get(1).getFace() == 3
                && orderedDice.get(2).getFace() == 4
                && orderedDice.get(3).getFace() == 5
                && orderedDice.get(4).getFace() == 6) {
            return true;
        } else {
            return false;
        }
    }

    // Check if Low straight
    public boolean isLowStraight() {
        if (orderedDice.get(0).getFace() == 1
                && orderedDice.get(1).getFace() == 2
                && orderedDice.get(2).getFace() == 3
                && orderedDice.get(3).getFace() == 4
                && orderedDice.get(4).getFace() == 5) {
            return true;
        } else {
            return false;
        }
    }

    // Check if Three of a kind
    public boolean isThreeOfAKind() {
        if ((orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(1).getFace() == orderedDice.get(2).getFace())
                || (orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace())
                || (orderedDice.get(2).getFace() == orderedDice.get(3).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())) {
            return true;
        } else {
            return false;
        }
    }

    // Check if Two pair
    public boolean isTwoPair() {
        if ((orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(2).getFace() == orderedDice.get(3).getFace())
                ||(orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())
                ||(orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                && orderedDice.get(3).getFace() == orderedDice.get(4).getFace())) {
            return true;
        } else {
            return false;
        }
    }

    // Check if One pair
    public boolean isOnePair() {
        if (orderedDice.get(0).getFace() == orderedDice.get(1).getFace()
                || orderedDice.get(1).getFace() == orderedDice.get(2).getFace()
                || orderedDice.get(2).getFace() == orderedDice.get(3).getFace()
                || orderedDice.get(3).getFace() == orderedDice.get(4).getFace()) {
            return true;
        } else {
            return false;
        }
    }
}

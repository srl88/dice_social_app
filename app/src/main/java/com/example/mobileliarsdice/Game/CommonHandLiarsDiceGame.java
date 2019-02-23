package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

import java.util.ArrayList;

public class CommonHandLiarsDiceGame {
    // Attributes
    private int numberOfPlayers;
    private int pot;
    private int announcement;
    private ArrayList<Player> players;
    private ArrayList<Integer> tokens;
    private ArrayList<Cup> cups;
    private Cup gameCup;
    private PokerDiceHand gameCupHand;

    private Player firstPlayer;
    private Player turn;

    // Constructor
    public CommonHandLiarsDiceGame(ArrayList<Player> players, int ante) {
        this.players = players;
        this.numberOfPlayers = players.size();
        this.tokens = new ArrayList<Integer>();
        for (int i = 0; i < numberOfPlayers; i++) {
            tokens.add(ante);
        }
        this.cups = new ArrayList<Cup>();
        for (int i = 0; i < numberOfPlayers; i++) {
            cups.add(new Cup());
        }
        pot = 0;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }

    // Start the game
    // Choose the first player by each player rolling their five dice.
    // The player with the highest ranking poker dice hand becomes the first player.
    public void start() {
        announcement = 100;
        // Check if the players have token
        for (int i = 0; i < numberOfPlayers; i++) {
            if (tokens.get(i) == 0) {
                players.remove(i);
                tokens.remove(i);
                cups.remove(i);
            }
        }
        numberOfPlayers = players.size();

        // Roll all five dice inside the cup for each player
        for (int i = 0; i < numberOfPlayers; i++) {
            cups.get(i).shake();
        }
        // Get the player with the highest poker dice hand
        firstPlayer = players.get(0);
        for (int i = 1; i < numberOfPlayers; i++) {
            PokerDiceHand current = new PokerDiceHand(cups.get(players.indexOf(firstPlayer)));
            PokerDiceHand next = new PokerDiceHand(cups.get(i));
            if (current.getRank() > next.getRank()) {
                firstPlayer = players.get(i);
            }
        }

        // First player's turn
        turn = firstPlayer;
        gameCup = new Cup();
        gameCup.shake();
        gameCupHand = new PokerDiceHand(gameCup);
    }

    public void endTurn() {
        turn = players.get((players.indexOf(turn) + 1) % numberOfPlayers);
    }

    public void roll(boolean first, boolean second, boolean third, boolean fourth, boolean fifth) {
        if(first) {
            gameCup.roll(true, false, false, false, false);
        }
        if(second) {
            gameCup.roll(false, true, false, false, false);
        }
        if(third) {
            gameCup.roll(false, false, true, false, false);
        }
        if(fourth) {
            gameCup.roll(false, false, false, true, false);
        }
        if(fifth) {
            gameCup.roll(false, false, false, false, true);
        }
    }

    public boolean announce(int newAnnouncement) {
        if (announcement < newAnnouncement) {
            return false;
        } else {
            announcement = newAnnouncement;
            return true;
        }
    }

    public boolean challenge(Player challenger) {
        if (gameCupHand.getRank() > announcement) {
            tokens.set(players.indexOf(turn), tokens.get(players.indexOf(turn)) - 1);
            pot++;
            return true;
        } else {
            tokens.set(players.indexOf(challenger), tokens.get(players.indexOf(challenger)) - 1);
            pot++;
            return false;
        }
    }
}
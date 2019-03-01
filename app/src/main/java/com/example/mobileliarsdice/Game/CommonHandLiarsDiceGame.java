package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

import java.util.ArrayList;

public class CommonHandLiarsDiceGame {
    // Attributes
    private ArrayList<Player> players;
    private ArrayList<Integer> tokens;
    private ArrayList<Cup> cups;
    private Cup gameCup;
    private Player firstPlayer;
    private Player turn;
    private int numberOfPlayers;
    private int pot;
    private int bid;

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
    
    public int getPot() {
    	return pot;
    }
    
    public int getBid() {
    	return bid;
    }
    
    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    public ArrayList<Integer> getTokens() {
        return tokens;
    }
    
    public ArrayList<Cup> getCups() {
        return cups;
    }
    
    public Cup getGameCup() {
    	return gameCup;
    }

    public Player getFirstPlayer() {
        return firstPlayer;
    }
    
    public Player getTurn() {
        return turn;
    }

    // Start the game
    // Choose the first player by each player rolling their five dice
    // The player with the highest ranking poker dice hand becomes the first player
    public void start() {
        bid = 100;
        // Check if the players have token, if no token remove player from the game
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
            PokerDiceHand first = cups.get(players.indexOf(firstPlayer)).getHand();
            PokerDiceHand current = cups.get(i).getHand();
            if (current.getRank() < first.getRank()) {
                firstPlayer = players.get(i);
            }
        }
        // First player's turn
        turn = firstPlayer;
        gameCup = new Cup();
        gameCup.shake();
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

    public boolean bid(int newBid) {
        if (bid < newBid) {
            return false;
        } else {
            bid = newBid;
            return true;
        }
    }

    public boolean challenge(Player challenger) {
        if (gameCup.getHand().getRank() >= bid) {
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
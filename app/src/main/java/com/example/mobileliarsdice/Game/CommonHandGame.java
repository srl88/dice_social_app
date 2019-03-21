package com.example.mobileliarsdice.Game;

/**
 * Created by Sung Won Caleb Bhyun
 */

import java.util.ArrayList;

public class CommonHandGame {
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
    // ante: the amount that each player has to pay in order to start a game
    // if the player quits in the middle of the game, he/she will lose all ante
    // and the player remaining will automatically gain the pot
    public CommonHandGame(ArrayList<Player> players, int ante) {
        // Takeout ante from each player
        // If any of the player does not have enough balance, set antePaidUp to false
        // otherwise, antePaidUp = true
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

    public ArrayList<Integer> getTokens() {
        return tokens;
    }

    public Cup getGameCup() {
        return gameCup;
    }

    public Player getTurn() {
        return turn;
    }
    
    public int getPot() {
    	return pot;
    }
    
    public int getBid() {
    	return bid;
    }

    // Start round
    // Choose the first player by each player rolling their five dice
    // The player with the highest ranking poker dice hand becomes the first player
    public void start() {
        // Bid initialized (higher the number, lower the bid rank)
        bid = 100;
        // Check if the players have token, if no token remove player from the game
        // The player remaining wins the pot
        for (int i = 0; i < numberOfPlayers; i++) {
            if (tokens.get(i) == 0) {
                players.remove(i);
                tokens.remove(i);
                cups.remove(i);
                // Add pot to the remaining player's balance
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

    public void bid(int newBid) {
        if (bid < newBid) {
        } else {
            bid = newBid;
        }
    }

    // End round, return true if the challenger wins and return false if the challenger loses
    public boolean challenge(Player challenger) {
        if (gameCup.getHand().getRank() < bid) {
            tokens.set(players.indexOf(challenger), tokens.get(players.indexOf(challenger)) - 1);
            pot++;
            return true;
        } else {
            // For two player game
            tokens.set((players.indexOf(challenger) + 1) % numberOfPlayers, tokens.get((players.indexOf(challenger) + 1) % numberOfPlayers) - 1);
            pot++;
            return false;
        }
    }

    // For two player game, once a player quits, the player remaining automatically wins the pot
    public void quit(Player quitter) {
        tokens.remove(players.indexOf(quitter));
        tokens.set(0, tokens.get(0) + pot);
        players.remove(quitter);
    }
}
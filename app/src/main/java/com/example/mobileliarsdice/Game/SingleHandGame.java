package com.example.mobileliarsdice.Game;

import java.util.ArrayList;

/**
 * Created by Sung Won Caleb Bhyun
 */

public class SingleHandGame {
	// Attributes
    private ArrayList<Player> players;
    private ArrayList<Cup> cups;
    private Player firstPlayer;
    private Player turn;
    private int numberOfPlayers;
    private int bidFace;
    private int bidNumber;
    
    // Constructor
    public SingleHandGame(ArrayList<Player> players) {
        this.players = players;
        this.numberOfPlayers = players.size();
        this.cups = new ArrayList<Cup>();
        for (int i = 0; i < numberOfPlayers; i++) {
            cups.add(new Cup());
        }
    }
    
    public int getBidFace() {
    	return bidFace;
    }
    
    public int getBidNumber() {
    	return bidNumber;
    }
    
    public ArrayList<Player> getPlayers() {
        return players;
    }
    
    public ArrayList<Cup> getCups() {
        return cups;
    }
    
    public Player getTurn() {
        return turn;
    }
    
    // Start the game
    // Choose the first player by each player rolling two dice
    // The player with the highest sum becomes the first player
    public void start() {
    	bidFace = 0;
    	bidNumber = 0;
        // Check if the players have any dice remaining, if no dice remove player from the game
        for (int i = 0; i < numberOfPlayers; i++) {
            if (cups.get(i).getCup().size() == 0) {
                players.remove(i);
                cups.remove(i);
            }
        }
        numberOfPlayers = players.size();
        // Get the player with the highest sum of two dice
        firstPlayer = players.get(0);
    	Die first = new Die(1);
    	Die second = new Die(1);
    	first.roll();
    	second.roll();
        int sum_first = first.getFace() + second.getFace();
        for (int i = 1; i < numberOfPlayers; i++) {
        	first.roll();
        	second.roll();
        	int sum_current = first.getFace() + second.getFace();
            if (sum_first < sum_current) {
                sum_first = sum_current;
                firstPlayer = players.get(i);
            }
        }
        // Roll all dice inside the cup for each player
        for (int i = 0; i < numberOfPlayers; i++) {
            cups.get(i).shake();
        }
        // First player's turn
        turn = firstPlayer;
    }
    
    public void endTurn() {
        turn = players.get((players.indexOf(turn) + 1) % numberOfPlayers);
    }
    
    public boolean bid(int face, int number) {
    	if (bidFace == 0 && bidNumber == 0) {
    		bidFace = face;
    		bidNumber = number;
    		return true;
    	} else if (bidFace == face && bidNumber < number) {
        	bidNumber = number;
            return true;
        } else {
            return false;
        }
    }
    
    public boolean challenge(Player challenger) {
    	int count = 0;
        for (int i = 0; i < numberOfPlayers; i++) {
            for (int j = 0; j < cups.get(i).getCup().size(); j++) {
            	if (cups.get(i).getCup().get(j).getFace() == bidFace) {
            		count++;
            	}
            }
        }
        if (count < bidNumber) {
        	cups.get((players.indexOf(challenger) + 1) % numberOfPlayers).takeOut();
            return true;
        } else {
        	cups.get(players.indexOf(challenger)).takeOut();
            return false;
        }
    }
}
package com.example.mobileliarsdice.Models;

public class SingleHandRooms {
    // Attributes
    private String room_id;
    private String player1_id, player2_id;
    private boolean player1_ready;
    private boolean player2_ready;
    private boolean started;
    private boolean bidded;
    private boolean challenged;
    private int roundWinner;
    private int player1_die1, player1_die2, player1_die3, player1_die4, player1_die5;
    private int player2_die1, player2_die2, player2_die3, player2_die4, player2_die5;
    private int turn, bid_face, bid_number;

    // Constructors
    public SingleHandRooms() {
    }

    public SingleHandRooms(String room_id, String player1_id, String player2_id, boolean player1_ready, boolean player2_ready, boolean started, boolean bidded, boolean challenged, int roundWinner, int player1_die1, int player1_die2, int player1_die3, int player1_die4, int player1_die5, int player2_die1, int player2_die2, int player2_die3, int player2_die4, int player2_die5, int turn, int bid_face, int bid_number) {
        this.room_id = room_id;
        this.player1_id = player1_id;
        this.player2_id = player2_id;
        this.player1_ready = player1_ready;
        this.player2_ready = player2_ready;
        this.started = started;
        this.bidded = bidded;
        this.challenged = challenged;
        this.roundWinner = roundWinner;
        this.player1_die1 = player1_die1;
        this.player1_die2 = player1_die2;
        this.player1_die3 = player1_die3;
        this.player1_die4 = player1_die4;
        this.player1_die5 = player1_die5;
        this.player2_die1 = player2_die1;
        this.player2_die2 = player2_die2;
        this.player2_die3 = player2_die3;
        this.player2_die4 = player2_die4;
        this.player2_die5 = player2_die5;
        this.turn = turn;
        this.bid_face = bid_face;
        this.bid_number = bid_number;
    }

    // Getters and setters
    public String getRoom_id() {
        return room_id;
    }

    public void setRoom_id(String room_id) {
        this.room_id = room_id;
    }

    public String getPlayer1_id() {
        return player1_id;
    }

    public void setPlayer1_id(String player1_id) {
        this.player1_id = player1_id;
    }

    public String getPlayer2_id() {
        return player2_id;
    }

    public void setPlayer2_id(String player2_id) {
        this.player2_id = player2_id;
    }

    public boolean isPlayer1_ready() {
        return player1_ready;
    }

    public void setPlayer1_ready(boolean player1_ready) {
        this.player1_ready = player1_ready;
    }

    public boolean isPlayer2_ready() {
        return player2_ready;
    }

    public void setPlayer2_ready(boolean player2_ready) {
        this.player2_ready = player2_ready;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public boolean isBidded() {
        return bidded;
    }

    public void setBidded(boolean bidded) {
        this.bidded = bidded;
    }

    public boolean isChallenged() {
        return challenged;
    }

    public void setChallenged(boolean challenged) {
        this.challenged = challenged;
    }

    public int getRoundWinner() {
        return roundWinner;
    }

    public void setRoundWinner(int roundWinner) {
        this.roundWinner = roundWinner;
    }

    public int getPlayer1_die1() {
        return player1_die1;
    }

    public void setPlayer1_die1(int player1_die1) {
        this.player1_die1 = player1_die1;
    }

    public int getPlayer1_die2() {
        return player1_die2;
    }

    public void setPlayer1_die2(int player1_die2) {
        this.player1_die2 = player1_die2;
    }

    public int getPlayer1_die3() {
        return player1_die3;
    }

    public void setPlayer1_die3(int player1_die3) {
        this.player1_die3 = player1_die3;
    }

    public int getPlayer1_die4() {
        return player1_die4;
    }

    public void setPlayer1_die4(int player1_die4) {
        this.player1_die4 = player1_die4;
    }

    public int getPlayer1_die5() {
        return player1_die5;
    }

    public void setPlayer1_die5(int player1_die5) {
        this.player1_die5 = player1_die5;
    }

    public int getPlayer2_die1() {
        return player2_die1;
    }

    public void setPlayer2_die1(int player2_die1) {
        this.player2_die1 = player2_die1;
    }

    public int getPlayer2_die2() {
        return player2_die2;
    }

    public void setPlayer2_die2(int player2_die2) {
        this.player2_die2 = player2_die2;
    }

    public int getPlayer2_die3() {
        return player2_die3;
    }

    public void setPlayer2_die3(int player2_die3) {
        this.player2_die3 = player2_die3;
    }

    public int getPlayer2_die4() {
        return player2_die4;
    }

    public void setPlayer2_die4(int player2_die4) {
        this.player2_die4 = player2_die4;
    }

    public int getPlayer2_die5() {
        return player2_die5;
    }

    public void setPlayer2_die5(int player2_die5) {
        this.player2_die5 = player2_die5;
    }

    public int getTurn() {
        return turn;
    }

    public void setTurn(int turn) {
        this.turn = turn;
    }

    public int getBid_face() {
        return bid_face;
    }

    public void setBid_face(int bid_face) {
        this.bid_face = bid_face;
    }

    public int getBid_number() {
        return bid_number;
    }

    public void setBid_number(int bid_number) {
        this.bid_number = bid_number;
    }

    @Override
    public String toString() {
        return "SingleHandRooms{" +
                "room_id='" + room_id + '\'' +
                ", player1_id='" + player1_id + '\'' +
                ", player2_id='" + player2_id + '\'' +
                ", player1_ready=" + player1_ready +
                ", player2_ready=" + player2_ready +
                ", started=" + started +
                ", bidded=" + bidded +
                ", challenged=" + challenged +
                ", roundWinner=" + roundWinner +
                ", player1_die1=" + player1_die1 +
                ", player1_die2=" + player1_die2 +
                ", player1_die3=" + player1_die3 +
                ", player1_die4=" + player1_die4 +
                ", player1_die5=" + player1_die5 +
                ", player2_die1=" + player2_die1 +
                ", player2_die2=" + player2_die2 +
                ", player2_die3=" + player2_die3 +
                ", player2_die4=" + player2_die4 +
                ", player2_die5=" + player2_die5 +
                ", turn=" + turn +
                ", bid_face=" + bid_face +
                ", bid_number=" + bid_number +
                '}';
    }
}
package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileliarsdice.Game.SingleHandGame;
import com.example.mobileliarsdice.Game.Player;
import com.example.mobileliarsdice.Models.SingleHandRooms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SingleHandGameActivity extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;

    private DatabaseReference database;

    private TextView currentTurn, currentBid;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button readyButton, quitButton, bidButton, challengeButton;

    private int diceID;
    private int bidFace, bidNumber;

    private SingleHandGame sh_game;
    private boolean readyButtonClicked;

    private SingleHandRooms room;
    private String room_id;
    private String player_id;
    private boolean roomMaster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_hand_game);

        currentTurn = findViewById(R.id.currentTurn);
        currentBid = findViewById(R.id.currentBid);

        firstDiceImage = findViewById(R.id.firstDiceImage);
        secondDiceImage = findViewById(R.id.secondDiceImage);
        thirdDiceImage = findViewById(R.id.thirdDiceImage);
        fourthDiceImage = findViewById(R.id.fourthDiceImage);
        fifthDiceImage = findViewById(R.id.fifthDiceImage);

        readyButton = findViewById(R.id.readyButton);
        quitButton = findViewById(R.id.quitButton);
        bidButton = findViewById(R.id.bidButton);
        challengeButton = findViewById(R.id.challengeButton);

        readyButtonClicked = false;

        firstDiceImage.setVisibility(View.INVISIBLE);
        secondDiceImage.setVisibility(View.INVISIBLE);
        thirdDiceImage.setVisibility(View.INVISIBLE);
        fourthDiceImage.setVisibility(View.INVISIBLE);
        fifthDiceImage.setVisibility(View.INVISIBLE);
        bidButton.setEnabled(false);
        challengeButton.setEnabled(false);

        // Initialize face and number
        bidFace = 0;
        bidNumber = 0;

        // Check if the player is room master
        intent = getIntent();
        roomMaster = intent.getBooleanExtra("roomMaster", true);
        player_id = intent.getStringExtra("player_id");
        if(roomMaster == true) {
            // Create room on database
            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
            room_id = database.push().getKey();
            // Create room with empty player2_id
            room = new SingleHandRooms(room_id, player_id, "", false, false, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
            database.child(room_id).setValue(room);
        } else {
            // Player 1 passes room id through invitation and
            // player 2 gets the room id by accepting invitation
            // and passes the room id when starting this activity
            room_id = intent.getStringExtra("room_id");
            // Read room information from database and update with player2_id added
            updateRoom(room_id);
            room.setPlayer2_id(player_id);
            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id);
            database.setValue(room);
            /*database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    SingleHandRooms roomData;
                    for(DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                        roomData = roomSnapshot.getValue(SingleHandRooms.class);
                        // If room with matching room_id was found
                        if(roomData.getRoom_id().equals(room_id)) {
                            roomData.setPlayer2_id(player_id);
                            // Update room information with player2_id added on database
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(id);
                            database.setValue(roomData);
                            break;
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });*/

        }

        // Add players
        Player player1 = new Player("A", 20);
        Player player2 = new Player("B", 20);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);

        sh_game = new SingleHandGame(players);
    }

    // Get room information from the database and update room variable in the activity
    public void updateRoom(final String room_id) {
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SingleHandRooms roomData;
                for(DataSnapshot roomSnapshot : dataSnapshot.getChildren()) {
                    roomData = roomSnapshot.getValue(SingleHandRooms.class);
                    // If room with matching room_id was found
                    if(roomData.getRoom_id().equals(room_id)) {
                        room = roomData;
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readyButton:
                if(!readyButtonClicked) {
                    // Update ready button
                    readyButtonClicked = true;
                    readyButton.setText(getResources().getText(R.string.ready_button_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorRed,getResources().newTheme()));
                    Toast.makeText(getApplicationContext(),"Ready.",Toast.LENGTH_SHORT).show();
                    // Check database and if both player 1 and player 2 are ready, start the game
                    updateRoom(room_id);
                    if(room.isPlayer1_ready() && room.isPlayer2_ready()) {
                        // If both players are ready, start the game
                    } else {

                    }
                } else {
                    // Update ready button
                    readyButtonClicked = false;
                    readyButton.setText(getResources().getText(R.string.ready_button_not_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                    Toast.makeText(getApplicationContext(),"Cancelled.",Toast.LENGTH_SHORT).show();
                }

            /*case R.id.readyButton:
                if(!readyButtonClicked) {
                    *//*
                    DatabaseReference database;
                    database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
                    String id = database.push().getKey();
                    SingleHandRooms dummy = new SingleHandRooms(id, "lVFtk3vNfcczHizIQNwGemZZiOA3", "PvIJDpRRA5UtUKLpioeMvQ6PWHl1", false, false, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
                    database.child(id).setValue(dummy);
                    *//*

                    readyButton.setText(getResources().getText(R.string.ready_button_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                    readyButtonClicked = true;
                    // If all players are ready start the game
                    sh_game.start();
                    Toast.makeText(getApplicationContext(),"The game has started.",Toast.LENGTH_SHORT).show();
                    currentTurn.setText(sh_game.getTurn().getName());
                    readyButton.setEnabled(false);
                    bidButton.setEnabled(true);
                    // Update dice images
                    updateDiceImages();
                } else {
                    readyButton.setText(getResources().getText(R.string.ready_button_not_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorRed,getResources().newTheme()));
                    readyButtonClicked = false;
                }
                break;*/

            case R.id.quitButton:
                finish();
                // Open main activity
                // intent = new Intent(this, Main.class);
                // startActivity(intent);
                break;

            case R.id.bidButton:
                intent = new Intent(this, BidWindow.class);
                intent.putExtra("face", bidFace);
                intent.putExtra("number", bidNumber);
                // Prevent BidWindow from opening twice
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 1);
                break;

            case R.id.challengeButton:
                if(sh_game.challenge(sh_game.getTurn())) {
                    Toast.makeText(SingleHandGameActivity.this, "Player " + sh_game.getTurn().getName() + " wins!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SingleHandGameActivity.this, "Player " + sh_game.getTurn().getName() + " loses!", Toast.LENGTH_SHORT).show();
                }
                endRound();
                break;

        }
    }

    public void updateDiceImages() {
        Toast.makeText(getApplicationContext(),"Size: " + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size(),Toast.LENGTH_SHORT).show();
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 1) {
            firstDiceImage.setVisibility(View.VISIBLE);
            diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(0).getFace(), "drawable", getPackageName());
            firstDiceImage.setImageResource(diceID);
        } else {
            firstDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 2) {
            secondDiceImage.setVisibility(View.VISIBLE);
            diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(1).getFace(), "drawable", getPackageName());
            secondDiceImage.setImageResource(diceID);
        } else {
            secondDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 3) {
            thirdDiceImage.setVisibility(View.VISIBLE);
            diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(2).getFace(), "drawable", getPackageName());
            thirdDiceImage.setImageResource(diceID);
        } else {
            thirdDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 4) {
            fourthDiceImage.setVisibility(View.VISIBLE);
            diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(3).getFace(), "drawable", getPackageName());
            fourthDiceImage.setImageResource(diceID);
        } else {
            fourthDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 5) {
            fifthDiceImage.setVisibility(View.VISIBLE);
            diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(4).getFace(), "drawable", getPackageName());
            fifthDiceImage.setImageResource(diceID);
            System.out.println(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().toString());
        } else {
            fifthDiceImage.setVisibility(View.INVISIBLE);
        }
    }

    // Source: https://stackoverflow.com/questions/2091465/how-do-i-pass-data-between-activities-in-android-application
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                bundle = data.getExtras();
                bidFace = (int) bundle.get("face");
                bidNumber = (int) bundle.get("number");

                sh_game.bid(bidFace, bidNumber);
                sh_game.endTurn();
                currentTurn.setText(sh_game.getTurn().getName());
                currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
                updateDiceImages();

                // Maximum bid; next play can only challenge
                if(bidNumber == 10) {
                    bidButton.setEnabled(false);
                }
                // After first bid, players are able to challenge
                challengeButton.setEnabled(true);
            }
        }
    }

    public void startRound() {
        sh_game.start();
        Toast.makeText(getApplicationContext(),"A round has started.",Toast.LENGTH_SHORT).show();
        currentTurn.setText(sh_game.getTurn().getName());
        currentBid.setText("");
        // Database ROOMS: set turn = ch_game.getTurn().getName()
        bidButton.setEnabled(true);
        challengeButton.setEnabled(false);
        // Update dice images
        updateDiceImages();
    }

    public void endRound() {
        Toast.makeText(getApplicationContext(),"A round has ended.",Toast.LENGTH_SHORT).show();
        // Reset values
        bidFace = 0;
        bidNumber = 0;
        // Automatically start next round
        startRound();
    }
}
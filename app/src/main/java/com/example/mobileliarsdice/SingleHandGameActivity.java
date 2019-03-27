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

        // Initialize
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
            room = new SingleHandRooms(room_id, player_id, "", false, false, false, false, false, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
            database.child(room_id).setValue(room);

        } else {
            // Player 1 passes room id through invitation and
            // player 2 gets the room id by accepting invitation
            // and passes the room id when starting this activity
            room_id = intent.getStringExtra("room_id");
            // Add player2_id to the database
            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_id");
            database.setValue(player_id);
        }

        // Update room whenever there is a change in database
        readDatabase(room_id);
    }

    // Get room information from the database and update room variable in the activity
    public void readDatabase(final String room_id) {
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean roomDataFound = false;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SingleHandRooms roomSnapshot = snapshot.getValue(SingleHandRooms.class);
                    // If room with matching room_id was found
                    if(roomSnapshot.getRoom_id().equals(room_id)) {
                        roomDataFound = true;
                        // Check database and if both player 1 and player 2 are ready, start the game only on room master side
                        if(roomMaster == true && roomSnapshot.isPlayer1_ready() && roomSnapshot.isPlayer2_ready() && !roomSnapshot.isStarted()) {
                            // Add players
                            Player player1 = new Player("Player1");
                            Player player2 = new Player("Player2");
                            ArrayList<Player> players = new ArrayList<Player>();
                            players.add(player1);
                            players.add(player2);

                            sh_game = new SingleHandGame(players);
                            sh_game.start();

                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("started");
                            database.setValue(true);
                        }

                        // Update game information from room master side
                        if(roomMaster == true && roomSnapshot.isStarted()) {
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("turn");
                            if(sh_game.getTurn().getName().equals("Player1")) {
                                database.setValue(1);
                            } else {
                                database.setValue(2);
                            }

                            // Once a bid has been made
                            if(roomSnapshot.isBidded()) {
                                sh_game.bid(roomSnapshot.getBid_face(), roomSnapshot.getBid_number());
                                sh_game.endTurn();
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bidded");
                                database.setValue(false);
                            }

                            // Once a challenge has been made
                            if(roomSnapshot.isChallenged()) {
                                sh_game.challenge(sh_game.getTurn());
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("roundWinner");
                                database.setValue(1);
                                if(sh_game.challenge(sh_game.getTurn())) {
                                    if(sh_game.getTurn().getName().equals("Player1")) {
                                        database.setValue(1);
                                    } else {
                                        database.setValue(2);
                                    }
                                } else {
                                    if(sh_game.getTurn().getName().equals("Player1")) {
                                        database.setValue(2);
                                    } else {
                                        database.setValue(1);
                                    }
                                }
                            }

                            // Update dice information on database
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_die1");
                            database.setValue(sh_game.getCups().get(0).getCup().get(0).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_die2");
                            database.setValue(sh_game.getCups().get(0).getCup().get(1).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_die3");
                            database.setValue(sh_game.getCups().get(0).getCup().get(2).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_die4");
                            database.setValue(sh_game.getCups().get(0).getCup().get(3).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_die5");
                            database.setValue(sh_game.getCups().get(0).getCup().get(4).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_die1");
                            database.setValue(sh_game.getCups().get(1).getCup().get(0).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_die2");
                            database.setValue(sh_game.getCups().get(1).getCup().get(1).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_die3");
                            database.setValue(sh_game.getCups().get(1).getCup().get(2).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_die4");
                            database.setValue(sh_game.getCups().get(1).getCup().get(3).getFace());
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_die5");
                            database.setValue(sh_game.getCups().get(1).getCup().get(4).getFace());
                        }

                        // Update information for both players
                        if(roomSnapshot.isStarted()) {
                            // Update buttons
                            readyButton.setEnabled(false);
                            if(roomSnapshot.getTurn() == 1) {
                                if(roomMaster == true) {
                                    // Maximum bid
                                    if(roomSnapshot.getBid_number() == 10) {
                                        bidButton.setEnabled(false);
                                    } else {
                                        bidButton.setEnabled(true);
                                    }
                                    // On first turn, player is unable to challenge
                                    if(roomSnapshot.getBid_number() == 0) {
                                        challengeButton.setEnabled(false);
                                    } else {
                                        challengeButton.setEnabled(true);
                                    }
                                } else {
                                    bidButton.setEnabled(false);
                                    challengeButton.setEnabled(false);
                                }
                            } else {
                                if(roomMaster == false) {
                                    if(roomSnapshot.getBid_number()== 10) {
                                        bidButton.setEnabled(false);
                                    } else {
                                        bidButton.setEnabled(true);
                                    }
                                    // On first turn, player is unable to challenge
                                    if(roomSnapshot.getBid_number() == 0) {
                                        challengeButton.setEnabled(false);
                                    } else {
                                        challengeButton.setEnabled(true);
                                    }
                                } else {
                                    bidButton.setEnabled(false);
                                    challengeButton.setEnabled(false);
                                }
                            }
                            // Update displayed information
                            bidFace = roomSnapshot.getBid_face();
                            bidNumber = roomSnapshot.getBid_number();
                            currentTurn.setText("Player " + roomSnapshot.getTurn());
                            currentBid.setText(roomSnapshot.getBid_face() + " x" + roomSnapshot.getBid_number());
                            // Update dice images
                            if(roomMaster) {
                                if (roomSnapshot.getPlayer1_die1() != 0) {
                                    firstDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer1_die1(), "drawable", getPackageName());
                                    firstDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die2() != 0) {
                                    secondDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer1_die2(), "drawable", getPackageName());
                                    secondDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die3() != 0) {
                                    thirdDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer1_die3(), "drawable", getPackageName());
                                    thirdDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die4() != 0) {
                                    fourthDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer1_die4(), "drawable", getPackageName());
                                    fourthDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die5() != 0) {
                                    fifthDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer1_die5(), "drawable", getPackageName());
                                    fifthDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                if (roomSnapshot.getPlayer2_die1() != 0) {
                                    firstDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer2_die1(), "drawable", getPackageName());
                                    firstDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die2() != 0) {
                                    secondDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer2_die2(), "drawable", getPackageName());
                                    secondDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die3() != 0) {
                                    thirdDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer2_die3(), "drawable", getPackageName());
                                    thirdDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die4() != 0) {
                                    fourthDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer2_die4(), "drawable", getPackageName());
                                    fourthDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die5() != 0) {
                                    fifthDiceImage.setVisibility(View.VISIBLE);
                                    diceID = getResources().getIdentifier("face" + roomSnapshot.getPlayer2_die5(), "drawable", getPackageName());
                                    fifthDiceImage.setImageResource(diceID);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                            }

                            // Once a player has challenged another player
                            if(roomSnapshot.isChallenged()) {
                                if (roomMaster && roomSnapshot.getRoundWinner() == 1) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the round!", Toast.LENGTH_SHORT).show();
                                } else if (roomMaster && roomSnapshot.getRoundWinner() == 2) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have lost the round!", Toast.LENGTH_SHORT).show();
                                } else if (!roomMaster && roomSnapshot.getRoundWinner() == 2) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the round!", Toast.LENGTH_SHORT).show();
                                } else if (!roomMaster && roomSnapshot.getRoundWinner() == 1) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have lost the round!", Toast.LENGTH_SHORT).show();
                                }
                                // Reset values
                                bidFace = 0;
                                bidNumber = 0;
                                bidButton.setEnabled(false);
                                challengeButton.setEnabled(false);
                            }
                        }

                        // Once a round has ended
                        if(roomMaster && roomSnapshot.isStarted() && roomSnapshot.isChallenged()) {
                            // Start new round and reset game values
                            sh_game.start();
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bidded");
                            database.setValue(false);
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("challenged");
                            database.setValue(false);
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("roundWinner");
                            database.setValue(0);
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("turn");
                            database.setValue(0);
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bid_face");
                            database.setValue(0);
                            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bid_number");
                            database.setValue(0);
                        }
                        break;
                    }
                }
                // If one of the player has quit and the data was destroyed
                if(!roomDataFound) {
                    // Disable all the buttons except for leave button for the remaining player
                    currentTurn.setText("Your opponent has left the room.");
                    currentTurn.setText("");
                    firstDiceImage.setVisibility(View.INVISIBLE);
                    secondDiceImage.setVisibility(View.INVISIBLE);
                    thirdDiceImage.setVisibility(View.INVISIBLE);
                    fourthDiceImage.setVisibility(View.INVISIBLE);
                    fifthDiceImage.setVisibility(View.INVISIBLE);
                    readyButton.setEnabled(false);
                    bidButton.setEnabled(false);
                    challengeButton.setEnabled(false);
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
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                    Toast.makeText(getApplicationContext(),"Ready.",Toast.LENGTH_SHORT).show();
                    // Update database
                    if(roomMaster == true) {
                        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_ready");
                        database.setValue(true);
                    } else {
                        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_ready");
                        database.setValue(true);
                    }
                } else {
                    // Update ready button
                    readyButtonClicked = false;
                    readyButton.setText(getResources().getText(R.string.ready_button_not_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorRed,getResources().newTheme()));
                    Toast.makeText(getApplicationContext(),"Cancelled.",Toast.LENGTH_SHORT).show();
                    // Update database
                    if(roomMaster == true) {
                        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_ready");
                        database.setValue(false);
                    } else {
                        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_ready");
                        database.setValue(false);
                    }
                }
                break;

            case R.id.quitButton:
                // Destroy data
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id);
                database.removeValue();
                finish();
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
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("challenged");
                database.setValue(true);
                break;

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
                // Update database
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bid_face");
                database.setValue(bidFace);
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bid_number");
                database.setValue(bidNumber);
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("bidded");
                database.setValue(true);
            }
        }
    }
}
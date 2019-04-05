package com.example.mobileliarsdice;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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
import com.example.mobileliarsdice.Models.Chats;
import com.example.mobileliarsdice.Models.SingleHandRooms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.TimerTask;

public class SingleHandGameActivity extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;

    private DatabaseReference database;
    private ValueEventListener eventListener;

    private  DatabaseReference chatRef;
    private  ValueEventListener eventListenerChat;

    private TextView lblCurrentTurn, lblCurrentBid, currentTurn, currentBid, dicePlayer1, dicePlayer2;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage, chat;
    private Button readyButton, quitButton, bidButton, challengeButton;

    private int diceID;
    private int bidFace, bidNumber;

    private SingleHandGame sh_game;
    private boolean readyButtonClicked;

    private SingleHandRooms room;
    private String room_id;
    private String player_id;
    private boolean roomMaster;
    private boolean ended;
    private boolean leave;

    private int[] diceOf1;
    private int[] diceOf2;
    private int nbDice1;
    private int nbDice2;
    
    SoundPool dice_sound;       //For dice sound playing
    int sound_id;  
    boolean rolling=false;      //Is die rolling?
    Handler handler;

    private int value;


    private Boolean chatExist = false;
    private Chats currentChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_hand_game);

        lblCurrentTurn = findViewById(R.id.lblCurrentTurn);
        lblCurrentBid = findViewById(R.id.lblCurrentBid);

        currentTurn = findViewById(R.id.currentTurn);
        currentBid = findViewById(R.id.currentBid);
        dicePlayer1 = findViewById(R.id.dicePlayer1);
        dicePlayer2 = findViewById(R.id.dicePlayer2);

        firstDiceImage = findViewById(R.id.firstDiceImage);
        secondDiceImage = findViewById(R.id.secondDiceImage);
        thirdDiceImage = findViewById(R.id.thirdDiceImage);
        fourthDiceImage = findViewById(R.id.fourthDiceImage);
        fifthDiceImage = findViewById(R.id.fifthDiceImage);
        chat = findViewById(R.id.inGameChat);

        readyButton = findViewById(R.id.readyButton);
        quitButton = findViewById(R.id.quitButton);
        bidButton = findViewById(R.id.bidButton);
        challengeButton = findViewById(R.id.challengeButton);

        readyButtonClicked = false;

        lblCurrentTurn.setVisibility(View.INVISIBLE);
        lblCurrentBid.setVisibility(View.INVISIBLE);
        firstDiceImage.setVisibility(View.INVISIBLE);
        secondDiceImage.setVisibility(View.INVISIBLE);
        thirdDiceImage.setVisibility(View.INVISIBLE);
        fourthDiceImage.setVisibility(View.INVISIBLE);
        fifthDiceImage.setVisibility(View.INVISIBLE);
        bidButton.setEnabled(false);
        challengeButton.setEnabled(false);
        
        InitSound();
        //link handler to callback
        handler=new Handler(callback);


        // Initialize
        bidFace = 0;
        bidNumber = 0;
        ended = false;
        leave = false;

        diceOf1 = new int[5];
        diceOf2 = new int[5];

        // Check if the player is room master
        intent = getIntent();
        roomMaster = intent.getBooleanExtra("roomMaster", true);
        player_id = intent.getStringExtra("player_id");
        if(roomMaster == true) {
            // Create room on database
            database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
            room_id = intent.getStringExtra("room_id");
            //room_id = database.push().getKey();
            // Create room with empty player2_id
            //room = new SingleHandRooms(room_id, player_id, "", false, false, false, false, false, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
            //database.child(room_id).setValue(room);
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


        /**
         * CHAT EVENT LISTENER
         */

        String  friend_id = (intent.getStringExtra("id_1").equals(UserGlobals.mUser.getId())) ? intent.getStringExtra("id_2") : intent.getStringExtra("id_1");
        String fried_name = (intent.getStringExtra("name_1").equals(UserGlobals.mUser.getUserName())) ? intent.getStringExtra("name_2") : intent.getStringExtra("name_2");
        String friend_url = (intent.getStringExtra("url_1").equals(UserGlobals.mUser.getUrl())) ? intent.getStringExtra("url_2") : intent.getStringExtra("url_1");

        currentChat = new Chats(friend_id, fried_name, friend_url, false);

        chatRef = FireBaseGlobals.getDataBase().getReference("CHATS").child(UserGlobals.mUser.getId()).child(friend_id);
        eventListenerChat = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()==null) {return;}
                chatExist = true;
                currentChat = dataSnapshot.getValue(Chats.class);
                if(currentChat.getNewChat()){
                    chat.setBackgroundResource(R.drawable.new_chat);
                }else{
                    chat.setBackgroundResource(R.drawable.chat);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        chatRef.addValueEventListener(eventListenerChat);


    }

    // Get room information from the database and update room variable in the activity
    public void readDatabase(final String room_id) {
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean roomDataFound = false;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(ended) {
                        break;
                    }
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
                            if(roomSnapshot.isChallenged() && roomSnapshot.getRoundWinner() == 0) {
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("roundWinner");
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
                            lblCurrentTurn.setVisibility(View.VISIBLE);
                            lblCurrentBid.setVisibility(View.VISIBLE);
                            currentTurn.setText("Player " + roomSnapshot.getTurn());
                            currentBid.setText(roomSnapshot.getBid_face() + " x" + roomSnapshot.getBid_number());
                            // Update the number of dice for each player
                            diceOf1[0] = roomSnapshot.getPlayer1_die1();
                            diceOf1[1] = roomSnapshot.getPlayer1_die2();
                            diceOf1[2] = roomSnapshot.getPlayer1_die3();
                            diceOf1[3] = roomSnapshot.getPlayer1_die4();
                            diceOf1[4] = roomSnapshot.getPlayer1_die5();
                            diceOf2[0] = roomSnapshot.getPlayer2_die1();
                            diceOf2[1] = roomSnapshot.getPlayer2_die2();
                            diceOf2[2] = roomSnapshot.getPlayer2_die3();
                            diceOf2[3] = roomSnapshot.getPlayer2_die4();
                            diceOf2[4] = roomSnapshot.getPlayer2_die5();
                            nbDice1 = 0;
                            nbDice2 = 0;
                            for (int i=0;i<5;i++) {
                                if (diceOf1[i]!=0) {
                                    nbDice1++;
                                }
                                if (diceOf2[i]!=0) {
                                    nbDice2++;
                                }
                            }
                            dicePlayer1.setText("Player 1 has "+nbDice1+" dice");
                            dicePlayer2.setText("Player 2 has "+nbDice2+" dice");
                            // Update dice images
                            if(roomMaster) {
                                rolling = true;
                                //Start rolling sound
                                dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);

                                if (roomSnapshot.getPlayer1_die1() != 0) {
                                    firstDiceImage.setVisibility(View.VISIBLE);
                                    firstDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer1_die1();
                                    //Pause to allow image to update
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            firstDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die2() != 0) {
                                    secondDiceImage.setVisibility(View.VISIBLE);
                                    secondDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer1_die2();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            secondDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    secondDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die3() != 0) {
                                    thirdDiceImage.setVisibility(View.VISIBLE);
                                    thirdDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer1_die3();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            thirdDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    thirdDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die4() != 0) {
                                    fourthDiceImage.setVisibility(View.VISIBLE);
                                    fourthDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer1_die4();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            fourthDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    fourthDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer1_die5() != 0) {
                                    fifthDiceImage.setVisibility(View.VISIBLE);
                                    fifthDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer1_die5();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            fifthDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    fifthDiceImage.setVisibility(View.INVISIBLE);
                                }
                            } else {
                                if (roomSnapshot.getPlayer2_die1() != 0) {
                                    firstDiceImage.setVisibility(View.VISIBLE);
                                    firstDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer2_die1();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            firstDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    firstDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die2() != 0) {
                                    secondDiceImage.setVisibility(View.VISIBLE);
                                    secondDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer2_die2();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            secondDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    secondDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die3() != 0) {
                                    thirdDiceImage.setVisibility(View.VISIBLE);
                                    thirdDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer2_die3();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            thirdDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    thirdDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die4() != 0) {
                                    fourthDiceImage.setVisibility(View.VISIBLE);
                                    fourthDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer2_die4();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            fourthDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    fourthDiceImage.setVisibility(View.INVISIBLE);
                                }
                                if (roomSnapshot.getPlayer2_die5() != 0) {
                                    fifthDiceImage.setVisibility(View.VISIBLE);
                                    fifthDiceImage.setImageResource(R.drawable.dice3droll);
                                    value = roomSnapshot.getPlayer2_die5();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            diceID = getResources().getIdentifier("face" + value, "drawable", getPackageName());
                                            fifthDiceImage.setImageResource(diceID);
                                        }
                                    }, 400);
                                } else {
                                    fifthDiceImage.setVisibility(View.INVISIBLE);
                                }
                            }

                            // If one of the player has no dice, all dice values = 0
                            if(!ended && roomSnapshot.getPlayer1_die1() == 0 && roomSnapshot.getPlayer1_die2() == 0 && roomSnapshot.getPlayer1_die3() == 0 && roomSnapshot.getPlayer1_die4() == 0 && roomSnapshot.getPlayer1_die5() == 0) {
                                if(roomMaster) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have lost the game!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the game!", Toast.LENGTH_SHORT).show();
                                }
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_id");
                                database.setValue("");
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_id");
                                database.setValue("");
                                ended = true;
                            } else if(!ended && roomSnapshot.getPlayer2_die1() == 0 && roomSnapshot.getPlayer2_die2() == 0 && roomSnapshot.getPlayer2_die3() == 0 && roomSnapshot.getPlayer2_die4() == 0 && roomSnapshot.getPlayer2_die5() == 0) {
                                if(roomMaster) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the game!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(SingleHandGameActivity.this, "You have lost the game!", Toast.LENGTH_SHORT).show();
                                }
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player1_id");
                                database.setValue("");
                                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id).child("player2_id");
                                database.setValue("");
                                ended = true;
                            }

                            // Once a player has challenged another player
                            if(!ended && roomSnapshot.isChallenged()) {
                                if(roomMaster && roomSnapshot.getRoundWinner() == 1) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the round!", Toast.LENGTH_SHORT).show();
                                } else if(roomMaster && roomSnapshot.getRoundWinner() == 2) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have lost the round!", Toast.LENGTH_SHORT).show();
                                } else if(!roomMaster && roomSnapshot.getRoundWinner() == 2) {
                                    Toast.makeText(SingleHandGameActivity.this, "You have won the round!", Toast.LENGTH_SHORT).show();
                                } else if(!roomMaster && roomSnapshot.getRoundWinner() == 1) {
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
                        if(roomMaster && roomSnapshot.isStarted() && roomSnapshot.getRoundWinner() != 0) {
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
                            // Start a new round
                            sh_game.start();
                        }

                        break;
                    }
                }
                // If the game has ended, destroy the game data
                if(ended) {
                    database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id);
                    database.removeValue();
                }
                // If the game data was destroyed
                if(!roomDataFound) {
                    // Disable all the buttons except for leave button for the remaining player
                    if(ended) {
                        currentTurn.setText("The game has ended.");
                    } else {
                        if(!leave) {
                            currentTurn.setText("Your opponent has left.");
                        }
                    }
                    currentBid.setText("");
                    lblCurrentTurn.setVisibility(View.INVISIBLE);
                    lblCurrentBid.setVisibility(View.INVISIBLE);
                    dicePlayer1.setVisibility(View.INVISIBLE);
                    dicePlayer2.setVisibility(View.INVISIBLE);
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
        };

        // Add event listener
        database.addValueEventListener(eventListener);

    }

    // Disable back button
    @Override
    public void onBackPressed() {
        leave = true;
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id);
        database.removeValue();
        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
        database.removeEventListener(eventListener);
        chatRef.removeEventListener(eventListenerChat);

        this.finish();
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.inGameChat:
                //Toast.makeText(getApplicationContext(),"CHAT",Toast.LENGTH_SHORT).show();
                checkIfChatExist();
                break;
            case R.id.readyButton:
                if(!readyButtonClicked) {
                    // Update ready button
                    readyButtonClicked = true;
                    readyButton.setText(getResources().getText(R.string.ready_button_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
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
                leave = true;
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS").child(room_id);
                database.removeValue();
                database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
                database.removeEventListener(eventListener);
                this.finish();
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

    protected void checkIfChatExist(){
        if(!chatExist){
            //Create both chats. ours and friends since we are starting the conversation.
            currentChat.setNewChat(false);
            chatRef.setValue(currentChat);
            //reference to the databse from friend
            DatabaseReference temp = FireBaseGlobals.getDataBase().getReference("CHATS").child(currentChat.getId()).child(UserGlobals.mUser.getId());
            Chats tempChat = new Chats(UserGlobals.mUser.getId(), UserGlobals.mUser.getUserName(), UserGlobals.mUser.getUrl(), false);
            temp.setValue(tempChat);
        }
        goToMessageActivity();
    }

    protected void goToMessageActivity(){
        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
        intent.putExtra("friend_id", currentChat.getId());
        intent.putExtra("friend_name", currentChat.getUserName());
        intent.putExtra("friend_url", currentChat.getUrl());
        //update current chat
        UserGlobals.current_chat_id = currentChat.getId();
        //start activity
        startActivity(intent);
    }

    void InitSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //uses builder pattern
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            //default max streams is 1
            dice_sound= new SoundPool.Builder().setAudioAttributes(aa).build();

        } else {
            //Running on device earlier than Lollipop
            dice_sound=PreLollipopSoundPool.NewSoundPool();
        }
        //Load the dice sound
        sound_id=dice_sound.load(this,R.raw.shake_dice,1);
    }

    //Receives message from timer to start dice roll
    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            rolling=false;  //user can press again
            return true;
        }
    };

            //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }


}
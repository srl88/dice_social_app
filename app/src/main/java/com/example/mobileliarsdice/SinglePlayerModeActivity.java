package com.example.mobileliarsdice;

        import android.content.Context;
        import android.content.Intent;
        import android.media.AudioAttributes;
        import android.media.SoundPool;
        import android.net.NetworkInfo;
        import android.os.Build;
        import android.os.Handler;
        import android.os.Message;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.example.mobileliarsdice.Game.SingleHandGame;
        import com.example.mobileliarsdice.Game.Player;

        import java.util.ArrayList;
        import java.util.TimerTask;

        import android.net.ConnectivityManager;

public class SinglePlayerModeActivity extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;

    private TextView lblCurrentTurn, lblCurrentBid, lblNbDice, currentTurn, currentBid, nbDice;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button readyButton, quitButton, bidButton, challengeButton;

    SoundPool dice_sound;       //For dice sound playing
    int sound_id;               //Used to implement feedback to user
    boolean rolling=false;      //Is die rolling?
    Handler handler;



    private int diceID;
    private int bidFace, bidNumber;
    private ArrayList<String> listOfBids;

    private SingleHandGame sh_game;

    private Player player1;
    private Player player2;

    int[] diceOf;
    int nbDiceCPU;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_mode);

        InitSound();

        //link handler to callback
        handler=new Handler(callback);

        lblCurrentTurn = findViewById(R.id.lblCurrentTurn);
        lblCurrentBid = findViewById(R.id.lblCurrentBid);
        lblNbDice = findViewById(R.id.lblNbDice);

        currentTurn = findViewById(R.id.currentTurn);
        currentBid = findViewById(R.id.currentBid);
        nbDice = findViewById(R.id.NbDice);

        firstDiceImage = findViewById(R.id.firstDiceImage);
        secondDiceImage = findViewById(R.id.secondDiceImage);
        thirdDiceImage = findViewById(R.id.thirdDiceImage);
        fourthDiceImage = findViewById(R.id.fourthDiceImage);
        fifthDiceImage = findViewById(R.id.fifthDiceImage);

        readyButton = findViewById(R.id.readyButton);
        quitButton = findViewById(R.id.quitButton);
        bidButton = findViewById(R.id.bidButton);
        challengeButton = findViewById(R.id.challengeButton);

        readyButton.setText("START");

        lblCurrentTurn.setVisibility(View.INVISIBLE);
        lblCurrentBid.setVisibility(View.INVISIBLE);
        lblNbDice.setVisibility(View.INVISIBLE);

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
        diceOf = new int[5];
        listOfBids = new ArrayList<String>();

        // Add players
        player1 = new Player("A");
        player2 = new Player("cpu");
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);

        sh_game = new SingleHandGame(players);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readyButton:
                // If all players are ready start the game
                sh_game.start();
                Toast.makeText(getApplicationContext(),"The game has started.",Toast.LENGTH_SHORT).show();
                readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                readyButton.setEnabled(false);
                bidButton.setEnabled(true);

                // If first turn is cpu's turn
                if(sh_game.getTurn().getName().equals("cpu")) {
                    String cpu_bid = sh_game.getPlayers().get(1).computeHand(sh_game.getCups(), 1, 2, listOfBids);
                    System.out.println("CPU BID: " + cpu_bid);
                    listOfBids.add(cpu_bid);
                    bidFace = Integer.valueOf(cpu_bid.substring(0,1));
                    bidNumber = Integer.valueOf(cpu_bid.substring(2,3));

                    sh_game.bid(Integer.valueOf(cpu_bid.substring(2,3)), Integer.valueOf(cpu_bid.substring(0,1)));
                    sh_game.endTurn();
                    challengeButton.setEnabled(true);
                }

                // Update dice images
                updateDiceImages();

                lblCurrentTurn.setVisibility(View.VISIBLE);
                lblCurrentBid.setVisibility(View.VISIBLE);
                lblNbDice.setVisibility(View.VISIBLE);
                currentTurn.setText(sh_game.getTurn().getName());

                if(bidNumber == 0) {
                    currentBid.setText("");
                } else {
                    currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
                }

                System.out.println(sh_game.getCups().get(0).toString());
                System.out.println(sh_game.getCups().get(1).toString());

                System.out.println("LIST OF BIDS: " + listOfBids.toString());

                break;

            case R.id.quitButton:
                Boolean isConnection = isNetworkConnected();
                if (isConnection) {
                    Utilities.createToast("Back to main!", SinglePlayerModeActivity.this);
                    goToMain();
                } else {this.finish();}
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
                    Toast.makeText(SinglePlayerModeActivity.this, "Player " + sh_game.getTurn().getName() + " wins!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SinglePlayerModeActivity.this, "Player " + sh_game.getTurn().getName() + " loses!", Toast.LENGTH_SHORT).show();
                }
                endRound();
                break;

        }
    }

    public void updateDiceImages() {
        diceOf[0] = sh_game.getCups().get(1).getCup().get(0).getFace();
        diceOf[1] = sh_game.getCups().get(1).getCup().get(1).getFace();
        diceOf[2] = sh_game.getCups().get(1).getCup().get(2).getFace();
        diceOf[3] = sh_game.getCups().get(1).getCup().get(3).getFace();
        diceOf[4] = sh_game.getCups().get(1).getCup().get(4).getFace();
        nbDiceCPU = 0;
        for (int i=0; i<5; i++) {
            if (diceOf[i]!=0) {
                nbDiceCPU++;
            }
        }
        nbDice.setText(Integer.toString(nbDiceCPU)) ;
        rolling = true;
        //Start rolling sound
        dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);

        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 1) {
            firstDiceImage.setVisibility(View.VISIBLE);
            firstDiceImage.setImageResource(R.drawable.dice3droll);
            //Pause to allow image to update
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(0).getFace(), "drawable", getPackageName());
                    firstDiceImage.setImageResource(diceID);
                }
            }, 400);
        } else {
            firstDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 2) {
            secondDiceImage.setVisibility(View.VISIBLE);
            secondDiceImage.setImageResource(R.drawable.dice3droll);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(1).getFace(), "drawable", getPackageName());
                    secondDiceImage.setImageResource(diceID);
                }
            }, 400);
        } else {
            secondDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 3) {
            thirdDiceImage.setVisibility(View.VISIBLE);
            thirdDiceImage.setImageResource(R.drawable.dice3droll);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(2).getFace(), "drawable", getPackageName());
                    thirdDiceImage.setImageResource(diceID);
                }
            }, 400);
        } else {
            thirdDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 4) {
            fourthDiceImage.setVisibility(View.VISIBLE);
            fourthDiceImage.setImageResource(R.drawable.dice3droll);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(3).getFace(), "drawable", getPackageName());
                    fourthDiceImage.setImageResource(diceID);
                }
            }, 400);
        } else {
            fourthDiceImage.setVisibility(View.INVISIBLE);
        }
        if(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().size() >= 5) {
            fifthDiceImage.setVisibility(View.VISIBLE);
            fifthDiceImage.setImageResource(R.drawable.dice3droll);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(4).getFace(), "drawable", getPackageName());
                    fifthDiceImage.setImageResource(diceID);
                }
            }, 400);
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
                listOfBids.add(bidNumber + " " + bidFace);
                System.out.println("LIST OF BIDS: " + listOfBids.toString());

                sh_game.bid(bidFace, bidNumber);
                sh_game.endTurn();

                // Maximum bid; next play can only challenge
                if(bidNumber == 10) {
                    bidButton.setEnabled(false);
                }
                // After first bid, players are able to challenge
                challengeButton.setEnabled(true);

                // AI makes a move after the player bids
                String cpu_bid = sh_game.getPlayers().get(1).computeHand(sh_game.getCups(), 1, 2, listOfBids);
                if(cpu_bid.equals("Liar!")) {
                    sh_game.challenge(player2);
                    endRound();
                } else {
                    listOfBids.add(cpu_bid);
                    bidFace = Integer.valueOf(cpu_bid.substring(0,1));
                    bidNumber = Integer.valueOf(cpu_bid.substring(2,3));
                    System.out.println("CPU BID: " + cpu_bid);
                    sh_game.bid(Integer.valueOf(cpu_bid.substring(2,3)), Integer.valueOf(cpu_bid.substring(0,1)));
                    sh_game.endTurn();
                }

                updateDiceImages();
                currentTurn.setText(sh_game.getTurn().getName());
                currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
            }
        }
    }

    public void startRound() {
        sh_game.start();
        Toast.makeText(getApplicationContext(),"A round has started.",Toast.LENGTH_SHORT).show();
        challengeButton.setEnabled(false);
        
        listOfBids.clear();

        // If first turn is cpu's turn
        if(sh_game.getTurn().getName().equals("cpu")) {
            challengeButton.setEnabled(true);
            String cpu_bid = sh_game.getPlayers().get(1).computeHand(sh_game.getCups(), 1, 2, listOfBids);
            System.out.println("CPU BID: " + cpu_bid);
            listOfBids.add(cpu_bid);
            bidFace = Integer.valueOf(cpu_bid.substring(0,1));
            bidNumber = Integer.valueOf(cpu_bid.substring(2,3));

            sh_game.bid(Integer.valueOf(cpu_bid.substring(2,3)), Integer.valueOf(cpu_bid.substring(0,1)));
            sh_game.endTurn();
        }

        currentTurn.setText(sh_game.getTurn().getName());
        if(bidNumber == 0) {
            currentBid.setText("");
        } else {
            currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
        }
        bidButton.setEnabled(true);

        // Update dice images
        updateDiceImages();

        System.out.println("LIST OF BIDS: " + listOfBids.toString());
    }

    public void endRound() {
        Toast.makeText(getApplicationContext(),"A round has ended.",Toast.LENGTH_SHORT).show();
        // Reset values
        bidFace = 0;
        bidNumber = 0;

        if(sh_game.getCups().get(0).getDiceNumber() == 0) {
            Toast.makeText(getApplicationContext(),"You have lost the game against AI!",Toast.LENGTH_SHORT).show();
            // Disable all except for leave button
            readyButton.setEnabled(false);
            bidButton.setEnabled(false);
            challengeButton.setEnabled(false);
            lblCurrentTurn.setVisibility(View.INVISIBLE);
            lblCurrentBid.setVisibility(View.INVISIBLE);
            lblNbDice.setVisibility(View.INVISIBLE);
            nbDice.setVisibility(View.INVISIBLE);
            firstDiceImage.setVisibility(View.INVISIBLE);
            secondDiceImage.setVisibility(View.INVISIBLE);
            thirdDiceImage.setVisibility(View.INVISIBLE);
            fourthDiceImage.setVisibility(View.INVISIBLE);
            fifthDiceImage.setVisibility(View.INVISIBLE);
            currentTurn.setText("You have lost the game.");
            currentBid.setVisibility(View.INVISIBLE);
        } else if(sh_game.getCups().get(1).getDiceNumber() == 0) {
            Toast.makeText(getApplicationContext(),"You have won the game against AI!.",Toast.LENGTH_SHORT).show();
            // Disable all except for leave button
            readyButton.setEnabled(false);
            bidButton.setEnabled(false);
            challengeButton.setEnabled(false);
            lblCurrentTurn.setVisibility(View.INVISIBLE);
            lblCurrentBid.setVisibility(View.INVISIBLE);
            lblNbDice.setVisibility(View.INVISIBLE);
            nbDice.setVisibility(View.INVISIBLE);
            firstDiceImage.setVisibility(View.INVISIBLE);
            secondDiceImage.setVisibility(View.INVISIBLE);
            thirdDiceImage.setVisibility(View.INVISIBLE);
            fourthDiceImage.setVisibility(View.INVISIBLE);
            fifthDiceImage.setVisibility(View.INVISIBLE);
            currentTurn.setText("You have won the game.");
            currentBid.setVisibility(View.INVISIBLE);
        } else {
            // Automatically start next round
            startRound();
        }
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

    /**
     *  Goes to Main!
     */
    private void goToMain(){
        Intent i = new Intent(SinglePlayerModeActivity.this, Main.class);
        startActivity(i);
        this.finish();
    }

    /**
     * Checks if the device is currently connected to a netwotk
     * Returns true if it is connected, false otherwise.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo nw = cm.getActiveNetworkInfo();
            if (nw != null) {
                return nw.isConnectedOrConnecting();
            }
        }
        return false;
    }
}

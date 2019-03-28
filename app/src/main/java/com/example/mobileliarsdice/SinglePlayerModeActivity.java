package com.example.mobileliarsdice;

        import android.content.Intent;
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
        import java.util.List;

public class SinglePlayerModeActivity extends AppCompatActivity {
    private Intent intent;
    private Bundle bundle;

    private TextView currentTurn, currentBid;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button readyButton, quitButton, bidButton, challengeButton;

    private int diceID;
    private int bidFace, bidNumber;
    private ArrayList<String> listOfBids;

    private SingleHandGame sh_game;

    private Player player1;
    private Player player2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_player_mode);

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

        readyButton.setText("READY");

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
                readyButton.setEnabled(false);
                bidButton.setEnabled(true);
                // Update dice images
                updateDiceImages();

                if(sh_game.getTurn().getName().equals("cpu")) {
                    String cpu_bid = sh_game.getPlayers().get(1).computeHand(sh_game.getCups(), 1, 2, listOfBids);
                    System.out.println(Integer.valueOf(cpu_bid.substring(2,3)) + "----" + Integer.valueOf(cpu_bid.substring(0,1)));
                    listOfBids.add(cpu_bid);
                    bidFace = Integer.valueOf(cpu_bid.substring(0,1));
                    bidNumber = Integer.valueOf(cpu_bid.substring(2,3));

                    sh_game.bid(Integer.valueOf(cpu_bid.substring(2,3)), Integer.valueOf(cpu_bid.substring(0,1)));
                    sh_game.endTurn();
                }
                currentTurn.setText(sh_game.getTurn().getName());

                break;

            case R.id.quitButton:
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
                listOfBids.add(bidNumber + " " + bidFace);
                System.out.println("LIST OF BIDS: " + listOfBids.toString());

                sh_game.bid(bidFace, bidNumber);
                sh_game.endTurn();
                updateDiceImages();

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
                    sh_game.bid(Integer.valueOf(cpu_bid.substring(2,3)), Integer.valueOf(cpu_bid.substring(0,1)));
                    sh_game.endTurn();
                }
                currentTurn.setText(sh_game.getTurn().getName());
                currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
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

package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileliarsdice.Game.SingleHandGame;
import com.example.mobileliarsdice.Game.Player;
import com.example.mobileliarsdice.Game.PokerDiceHand;

import java.util.ArrayList;

public class SingleHandGameActivity extends AppCompatActivity {
    private Intent intent;

    private TextView currentTurn, currentBid;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button readyButton, quitButton, bidButton, challengeButton;

    private int diceID;
    private int bidFace, bidNumber;

    private SingleHandGame sh_game;
    private boolean readyButtonClicked;

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

        // Add players
        Player player1 = new Player("A", 20);
        Player player2 = new Player("B", 20);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);
        sh_game = new SingleHandGame(players);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readyButton:
                if(!readyButtonClicked) {
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
                break;

            case R.id.quitButton:
                finish();
                // Open main activity
                // intent = new Intent(this, Main.class);
                // startActivity(intent);
                break;

            case R.id.bidButton:
                intent = new Intent(this, BidWindow.class);
                startActivity(intent);
                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    bidFace = extras.getInt("face");
                    bidNumber = extras.getInt("number");
                    Toast.makeText(SingleHandGameActivity.this, "Face: " + bidFace + ", Number: " + bidNumber, Toast.LENGTH_SHORT).show();
                }


                sh_game.endTurn();
                currentTurn.setText(sh_game.getTurn().getName());
                currentBid.setText(sh_game.getBidFace() + " x" + sh_game.getBidNumber());
                updateDiceImages();
                break;

            case R.id.challengeButton:
                if(sh_game.challenge(sh_game.getTurn())) {
                    Toast.makeText(SingleHandGameActivity.this, "Player " + sh_game.getTurn().getName() + " wins!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SingleHandGameActivity.this, "Player " + sh_game.getTurn().getName() + " loses!", Toast.LENGTH_SHORT).show();

                }
                recreate();
                break;

        }
    }

    public void updateDiceImages() {
        diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(0).getFace(), "drawable", getPackageName());
        firstDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(1).getFace(), "drawable", getPackageName());
        secondDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(2).getFace(), "drawable", getPackageName());
        thirdDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(3).getFace(), "drawable", getPackageName());
        fourthDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().get(4).getFace(), "drawable", getPackageName());
        fifthDiceImage.setImageResource(diceID);
        System.out.println(sh_game.getCups().get(sh_game.getPlayers().indexOf(sh_game.getTurn())).getCup().toString());
    }
}
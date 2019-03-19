package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileliarsdice.Game.CommonHandLiarsDiceGame;
import com.example.mobileliarsdice.Game.Player;
import com.example.mobileliarsdice.Game.PokerDiceHand;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    private Intent intent;

    private TextView currentTurn, currentBid;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button readyButton, quitButton, bidButton, challengeButton;

    private int diceID;

    private CommonHandLiarsDiceGame ch_game;
    private boolean allPlayersReady;
    private boolean readyButtonClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

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

        bidButton.setEnabled(false);
        challengeButton.setEnabled(false);

        // Add players
        Player player1 = new Player("A", 20);
        Player player2 = new Player("B", 20);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);
        ch_game = new CommonHandLiarsDiceGame(players, 10);

        //Toast.makeText(getApplicationContext(),"true",Toast.LENGTH_SHORT).show();

    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.readyButton:
                if(!readyButtonClicked) {
                    readyButton.setText(getResources().getText(R.string.ready_button_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                    readyButtonClicked = true;
                    // If all players are ready start the game
                    ch_game.start();
                    Toast.makeText(getApplicationContext(),"The game has started.",Toast.LENGTH_SHORT).show();
                    currentTurn.setText(ch_game.getTurn().getName());
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
                // Source: https://www.youtube.com/watch?v=LXUDqGaToe0
                PopupMenu bidMenu = new PopupMenu(GameActivity.this, bidButton);
                bidMenu.getMenuInflater().inflate(R.menu.menu_bid, bidMenu.getMenu());
                bidMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(GameActivity.this, "" + item.getTitle() + " Order: " + item.getOrder(), Toast.LENGTH_SHORT).show();
                        ch_game.bid(item.getOrder());
                        ch_game.endTurn();
                        currentTurn.setText(ch_game.getTurn().getName());
                        PokerDiceHand hand = new PokerDiceHand(item.getOrder());
                        currentBid.setText(hand.toString());
                        updateDiceImages();
                        if(ch_game.getBid() == 1) {
                            bidButton.setEnabled(false);
                        }
                        challengeButton.setEnabled(true);
                        return true;
                    }
                });
                switch(ch_game.getBid()) {
                    case 2:
                        bidMenu.getMenu().findItem(R.id.bid2).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid3).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid4).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid5).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid6).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 3:
                        bidMenu.getMenu().findItem(R.id.bid3).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid4).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid5).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid6).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 4:
                        bidMenu.getMenu().findItem(R.id.bid4).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid5).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid6).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 5:
                        bidMenu.getMenu().findItem(R.id.bid5).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid6).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 6:
                        bidMenu.getMenu().findItem(R.id.bid6).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 7:
                        bidMenu.getMenu().findItem(R.id.bid7).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 8:
                        bidMenu.getMenu().findItem(R.id.bid8).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 9:
                        bidMenu.getMenu().findItem(R.id.bid9).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 10:
                        bidMenu.getMenu().findItem(R.id.bid10).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 11:
                        bidMenu.getMenu().findItem(R.id.bid11).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 12:
                        bidMenu.getMenu().findItem(R.id.bid12).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 13:
                        bidMenu.getMenu().findItem(R.id.bid13).setVisible(false);
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    case 14:
                        bidMenu.getMenu().findItem(R.id.bid14).setVisible(false);
                        break;
                    default:
                        Toast.makeText(GameActivity.this, "DEFAULT", Toast.LENGTH_SHORT).show();
                        break;
                }
                bidMenu.show();
                break;

            case R.id.challengeButton:
                if(ch_game.challenge(ch_game.getTurn())) {
                    Toast.makeText(GameActivity.this, "Player " + ch_game.getTurn().getName() + " wins!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(GameActivity.this, "Player " + ch_game.getTurn().getName() + " loses!", Toast.LENGTH_SHORT).show();
                }
                recreate();
                break;

        }
    }

    public void updateDiceImages() {
        diceID = getResources().getIdentifier("face" + ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().get(0).getFace(), "drawable", getPackageName());
        firstDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().get(1).getFace(), "drawable", getPackageName());
        secondDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().get(2).getFace(), "drawable", getPackageName());
        thirdDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().get(3).getFace(), "drawable", getPackageName());
        fourthDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().get(4).getFace(), "drawable", getPackageName());
        fifthDiceImage.setImageResource(diceID);
        System.out.println(ch_game.getCups().get(ch_game.getPlayers().indexOf(ch_game.getTurn())).getCup().toString());
    }
}

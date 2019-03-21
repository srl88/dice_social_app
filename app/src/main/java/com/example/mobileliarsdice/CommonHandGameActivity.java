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

import com.example.mobileliarsdice.Game.CommonHandGame;
import com.example.mobileliarsdice.Game.Player;
import com.example.mobileliarsdice.Game.PokerDiceHand;

import java.util.ArrayList;

public class CommonHandGameActivity extends AppCompatActivity {
    private Intent intent;

    private TextView currentTurn, currentBid;
    private ImageView firstDiceImage, secondDiceImage, thirdDiceImage, fourthDiceImage, fifthDiceImage;
    private Button rollButton, readyButton, quitButton, bidButton, challengeButton;

    private int diceID;

    private CommonHandGame ch_game;
    private boolean readyButtonClicked;
    private boolean firstDiceImageClicked, secondDiceImageClicked, thirdDiceImageClicked, fourthDiceImageClicked, fifthDiceImageClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_hand_game);

        currentTurn = findViewById(R.id.currentTurn);
        currentBid = findViewById(R.id.currentBid);

        firstDiceImage = findViewById(R.id.firstDiceImage);
        secondDiceImage = findViewById(R.id.secondDiceImage);
        thirdDiceImage = findViewById(R.id.thirdDiceImage);
        fourthDiceImage = findViewById(R.id.fourthDiceImage);
        fifthDiceImage = findViewById(R.id.fifthDiceImage);

        rollButton = findViewById(R.id.rollButton);
        readyButton = findViewById(R.id.readyButton);
        quitButton = findViewById(R.id.quitButton);
        bidButton = findViewById(R.id.bidButton);
        challengeButton = findViewById(R.id.challengeButton);

        readyButtonClicked = false;
        firstDiceImageClicked = false;
        secondDiceImageClicked = false;
        thirdDiceImageClicked = false;
        fourthDiceImageClicked = false;
        fifthDiceImageClicked = false;

        firstDiceImage.setVisibility(View.INVISIBLE);
        secondDiceImage.setVisibility(View.INVISIBLE);
        thirdDiceImage.setVisibility(View.INVISIBLE);
        fourthDiceImage.setVisibility(View.INVISIBLE);
        fifthDiceImage.setVisibility(View.INVISIBLE);
        rollButton.setVisibility(View.INVISIBLE);
        bidButton.setEnabled(false);
        challengeButton.setEnabled(false);

        // Add players
        Player player1 = new Player("A", 20);
        Player player2 = new Player("B", 20);
        ArrayList<Player> players = new ArrayList<Player>();
        players.add(player1);
        players.add(player2);
        ch_game = new CommonHandGame(players, 10);
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.firstDiceImage:
                if (firstDiceImageClicked == false) {
                    firstDiceImageClicked = true;
                    diceID = getResources().getIdentifier("clicked_face" + ch_game.getGameCup().getCup().get(0).getFace(), "drawable", getPackageName());
                    firstDiceImage.setImageResource(diceID);
                } else {
                    firstDiceImageClicked = false;
                    diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(0).getFace(), "drawable", getPackageName());
                    firstDiceImage.setImageResource(diceID);
                }
                updateRollButton();
                break;

            case R.id.secondDiceImage:
                if (secondDiceImageClicked == false) {
                    secondDiceImageClicked = true;
                    diceID = getResources().getIdentifier("clicked_face" + ch_game.getGameCup().getCup().get(1).getFace(), "drawable", getPackageName());
                    secondDiceImage.setImageResource(diceID);
                } else {
                    secondDiceImageClicked = false;
                    diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(1).getFace(), "drawable", getPackageName());
                    secondDiceImage.setImageResource(diceID);
                }
                updateRollButton();
                break;

            case R.id.thirdDiceImage:
                if (thirdDiceImageClicked == false) {
                    thirdDiceImageClicked = true;
                    diceID = getResources().getIdentifier("clicked_face" + ch_game.getGameCup().getCup().get(2).getFace(), "drawable", getPackageName());
                    thirdDiceImage.setImageResource(diceID);
                } else {
                    thirdDiceImageClicked = false;
                    diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(2).getFace(), "drawable", getPackageName());
                    thirdDiceImage.setImageResource(diceID);
                }
                updateRollButton();
                break;

            case R.id.fourthDiceImage:
                if (fourthDiceImageClicked == false) {
                    fourthDiceImageClicked = true;
                    diceID = getResources().getIdentifier("clicked_face" + ch_game.getGameCup().getCup().get(3).getFace(), "drawable", getPackageName());
                    fourthDiceImage.setImageResource(diceID);
                } else {
                    fourthDiceImageClicked = false;
                    diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(3).getFace(), "drawable", getPackageName());
                    fourthDiceImage.setImageResource(diceID);
                }
                updateRollButton();
                break;

            case R.id.fifthDiceImage:
                if (fifthDiceImageClicked == false) {
                    fifthDiceImageClicked = true;
                    diceID = getResources().getIdentifier("clicked_face" + ch_game.getGameCup().getCup().get(4).getFace(), "drawable", getPackageName());
                    fifthDiceImage.setImageResource(diceID);
                } else {
                    fifthDiceImageClicked = false;
                    diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(4).getFace(), "drawable", getPackageName());
                    fifthDiceImage.setImageResource(diceID);
                }
                updateRollButton();
                break;

            case R.id.rollButton:
                if(firstDiceImageClicked == true) {
                    ch_game.roll(true, false, false, false, false);
                }
                if(secondDiceImageClicked == true) {
                    ch_game.roll(false, true, false, false, false);
                }
                if(thirdDiceImageClicked == true) {
                    ch_game.roll(false, false, true, false, false);
                }
                if(fourthDiceImageClicked == true) {
                    ch_game.roll(false, false, false, true, false);
                }
                if(fifthDiceImageClicked == true) {
                    ch_game.roll(false, false, false, false, true);
                }
                updateDiceImages();
                rollButton.setVisibility(View.INVISIBLE);
                firstDiceImage.setEnabled(false);
                secondDiceImage.setEnabled(false);
                thirdDiceImage.setEnabled(false);
                fourthDiceImage.setEnabled(false);
                fifthDiceImage.setEnabled(false);
                challengeButton.setEnabled(false);
                break;

            case R.id.readyButton:
                if(!readyButtonClicked) {
                    readyButton.setText(getResources().getText(R.string.ready_button_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorGray,getResources().newTheme()));
                    readyButtonClicked = true;
                    // Database ROOMS: set player(n)_ready = true
                    // Database ROOMS: if player1_ready == true && player2_ready == true then start the game
                    readyButton.setEnabled(false);
                    startRound();
                } else {
                    readyButton.setText(getResources().getText(R.string.ready_button_not_clicked));
                    readyButton.setBackgroundColor(getResources().getColor(R.color.colorRed,getResources().newTheme()));
                    readyButtonClicked = false;
                    // Database ROOMS: set player(n)_ready = false
                }
                break;

            case R.id.quitButton:
                // Database ROOMS: ch_game.quit(database.get.player_who_tapped_quit_button);
                finish();
                // Open main activity
                // intent = new Intent(this, Main.class);
                // startActivity(intent);
                break;

            case R.id.bidButton:
                // Source: https://www.youtube.com/watch?v=LXUDqGaToe0
                PopupMenu bidMenu = new PopupMenu(CommonHandGameActivity.this, bidButton);
                bidMenu.getMenuInflater().inflate(R.menu.menu_bid, bidMenu.getMenu());
                bidMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Toast.makeText(CommonHandGameActivity.this, "" + item.getTitle() + " , rank: " + item.getOrder(), Toast.LENGTH_SHORT).show();
                        ch_game.bid(item.getOrder());
                        ch_game.endTurn();
                        currentTurn.setText(ch_game.getTurn().getName());
                        // Database ROOMS: set turn = ch_game.getTurn().getName()
                        PokerDiceHand hand = new PokerDiceHand(item.getOrder());
                        currentBid.setText(hand.toString());
                        // Database ROOMS: set bid = hand.toString()
                        updateDiceImages();
                        // If the current bid is Five of a kind, next player can only 'challenge'
                        if(ch_game.getBid() == 1) {
                            bidButton.setEnabled(false);
                        } else {
                            // Renable roll
                            firstDiceImageClicked = false;
                            secondDiceImageClicked = false;
                            thirdDiceImageClicked = false;
                            fourthDiceImageClicked = false;
                            fifthDiceImageClicked = false;
                            firstDiceImage.setEnabled(true);
                            secondDiceImage.setEnabled(true);
                            thirdDiceImage.setEnabled(true);
                            fourthDiceImage.setEnabled(true);
                            fifthDiceImage.setEnabled(true);
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
                        //Toast.makeText(CommonHandGameActivity.this, "DEFAULT", Toast.LENGTH_SHORT).show();
                        break;
                }
                bidMenu.show();
                break;

            case R.id.challengeButton:
                if(ch_game.challenge(ch_game.getTurn()) == true) {
                    Toast.makeText(CommonHandGameActivity.this, "Player " + ch_game.getTurn().getName() + " wins!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(CommonHandGameActivity.this, "Player " + ch_game.getTurn().getName() + " loses!", Toast.LENGTH_SHORT).show();
                }
                //recreate();
                firstDiceImage.setEnabled(true);
                secondDiceImage.setEnabled(true);
                thirdDiceImage.setEnabled(true);
                fourthDiceImage.setEnabled(true);
                fifthDiceImage.setEnabled(true);
                endRound();
                break;
        }
    }

    public void updateDiceImages() {
        diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(0).getFace(), "drawable", getPackageName());
        firstDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(1).getFace(), "drawable", getPackageName());
        secondDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(2).getFace(), "drawable", getPackageName());
        thirdDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(3).getFace(), "drawable", getPackageName());
        fourthDiceImage.setImageResource(diceID);
        diceID = getResources().getIdentifier("face" + ch_game.getGameCup().getCup().get(4).getFace(), "drawable", getPackageName());
        fifthDiceImage.setImageResource(diceID);
        System.out.println(ch_game.getGameCup().getCup().toString());
    }

    public void updateRollButton() {
        if(firstDiceImageClicked == false && secondDiceImageClicked == false && thirdDiceImageClicked == false && fourthDiceImageClicked == false && fifthDiceImageClicked == false) {
            rollButton.setVisibility(View.INVISIBLE);
        } else {
            rollButton.setVisibility(View.VISIBLE);
        }
    }

    public void startRound() {
        ch_game.start();
        Toast.makeText(getApplicationContext(),"A round has started.",Toast.LENGTH_SHORT).show();
        currentTurn.setText(ch_game.getTurn().getName());
        currentBid.setText("");
        // Database ROOMS: set turn = ch_game.getTurn().getName()
        firstDiceImage.setVisibility(View.VISIBLE);
        secondDiceImage.setVisibility(View.VISIBLE);
        thirdDiceImage.setVisibility(View.VISIBLE);
        fourthDiceImage.setVisibility(View.VISIBLE);
        fifthDiceImage.setVisibility(View.VISIBLE);
        firstDiceImage.setEnabled(false);
        secondDiceImage.setEnabled(false);
        thirdDiceImage.setEnabled(false);
        fourthDiceImage.setEnabled(false);
        fifthDiceImage.setEnabled(false);
        bidButton.setEnabled(true);
        challengeButton.setEnabled(false);
        // Update dice images
        updateDiceImages();
    }

    public void endRound() {
        Toast.makeText(getApplicationContext(),"A round has ended.",Toast.LENGTH_SHORT).show();
        System.out.println("Pot: " + ch_game.getPot());
        System.out.println("Tokens: " + ch_game.getTokens().toString());

        // Automatically start next round
        startRound();
    }
}
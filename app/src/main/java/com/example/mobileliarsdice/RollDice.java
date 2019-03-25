package com.example.mobileliarsdice;

/**
 * Created by Philibert ADAM
 */


import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mobileliarsdice.R;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class RollDice extends AppCompatActivity {
    ImageView dice_picture_1;     //reference to dice picture
    ImageView dice_picture_2;
    ImageView dice_picture_3;
    ImageView dice_picture_4;
    ImageView dice_picture_5;
    Button button;
    Random rng=new Random();    //generate random numbers
    SoundPool dice_sound;       //For dice sound playing
    int sound_id;               //Used to control sound stream return by SoundPool
    Handler handler;            //Post message to start roll
    Timer timer=new Timer();    //Used to implement feedback to user
    boolean rolling=false;      //Is die rolling?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        //Our function to initialise sound playing
        InitSound();
        //Get a reference to image widget
        dice_picture_1 = findViewById(R.id.firstDice);
        dice_picture_2 = findViewById(R.id.secondDice);
        dice_picture_3 = findViewById(R.id.thirdDice);
        dice_picture_4 = findViewById(R.id.fourthDice);
        dice_picture_5 = findViewById(R.id.fifthDice);
        button = findViewById(R.id.button);
        button.setOnClickListener(new HandleClick()); /** replace to button **/
        //link handler to callback
        handler=new Handler(callback);
    }

    //User pressed dice, lets start
    private class HandleClick implements View.OnClickListener {
        public void onClick(View arg0) {
            if (!rolling) {
                rolling = true;
                //Show rolling image
                dice_picture_1.setImageResource(R.drawable.dice3droll);
                dice_picture_2.setImageResource(R.drawable.dice3droll);
                dice_picture_3.setImageResource(R.drawable.dice3droll);
                dice_picture_4.setImageResource(R.drawable.dice3droll);
                dice_picture_5.setImageResource(R.drawable.dice3droll);
                //Start rolling sound
                dice_sound.play(sound_id, 1.0f, 1.0f, 0, 0, 1.0f);
                //Pause to allow image to update
                timer.schedule(new Roll(), 400);
            }
        }
    }

    void InitSound() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Use the newer SoundPool.Builder
            //Set the audio attributes, SONIFICATION is for interaction events
            //uses builder pattern
            AudioAttributes aa = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            //default max streams is 1
            //also uses builder pattern
            dice_sound= new SoundPool.Builder().setAudioAttributes(aa).build();

        } else {
            //Running on device earlier than Lollipop
            //Use the older SoundPool constructor
            dice_sound=PreLollipopSoundPool.NewSoundPool();
        }
        //Load the dice sound
        sound_id=dice_sound.load(this,R.raw.shake_dice,1);
    }
    //When pause completed message sent to callback
    class Roll extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    //Receives message from timer to start dice roll
    Handler.Callback callback = new Handler.Callback() {
        public boolean handleMessage(Message msg) {
            //Get roll result
            //Remember nextInt returns 0 to 5 for argument of 6
            //hence + 1
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture_1.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture_1.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture_1.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture_1.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture_1.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture_1.setImageResource(R.drawable.six);
                    break;
                default:
            }
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture_2.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture_2.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture_2.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture_2.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture_2.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture_2.setImageResource(R.drawable.six);
                    break;
                default:
            }
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture_3.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture_3.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture_3.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture_3.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture_3.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture_3.setImageResource(R.drawable.six);
                    break;
                default:
            }
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture_4.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture_4.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture_4.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture_4.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture_4.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture_4.setImageResource(R.drawable.six);
                    break;
                default:
            }
            switch(rng.nextInt(6)+1) {
                case 1:
                    dice_picture_5.setImageResource(R.drawable.one);
                    break;
                case 2:
                    dice_picture_5.setImageResource(R.drawable.two);
                    break;
                case 3:
                    dice_picture_5.setImageResource(R.drawable.three);
                    break;
                case 4:
                    dice_picture_5.setImageResource(R.drawable.four);
                    break;
                case 5:
                    dice_picture_5.setImageResource(R.drawable.five);
                    break;
                case 6:
                    dice_picture_5.setImageResource(R.drawable.six);
                    break;
                default:
            }

            rolling=false;  //user can press again
            return true;
        }
    };

    //Clean up
    protected void onPause() {
        super.onPause();
        dice_sound.pause(sound_id);
    }
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
    }
}
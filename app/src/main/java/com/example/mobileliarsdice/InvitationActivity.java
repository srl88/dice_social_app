package com.example.mobileliarsdice;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;
import com.example.mobileliarsdice.Models.Rooms;
import com.example.mobileliarsdice.Models.SingleHandRooms;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class InvitationActivity extends AppCompatActivity {

    public String type;
    private DatabaseReference database;
    private Intent intent;
    Button acceptBtn;
    Button rejectBtn;
    Button cancelBtn;

    CircleImageView user_img;
    Rooms invitation;
    TextView txt;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);
        //get bundle values

        Intent prevIn = getIntent();
        final String friend_id = prevIn.getExtras().getString("friend_id");
        final String friend_name = prevIn.getExtras().getString("friend_name");
        final String friend_URL = prevIn.getExtras().getString("friend_URL");

        //UI elements
        acceptBtn = findViewById(R.id.acceptBtn);
        rejectBtn = findViewById(R.id.reject);
        user_img = findViewById(R.id.user_picture);
        txt = findViewById(R.id.txtView);
        cancelBtn = findViewById(R.id.CancelBtn);

        //make this activity a popup activity
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        // Set intent to single hand game activity
        intent = new Intent(this, SingleHandGameActivity.class);

        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*.8), (int)(height*.6));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -2;

        getWindow().setAttributes(params);

        //Create a room object based on who is the chalanger
        if(UserGlobals.isChallanger){
            invitation = new Rooms(UserGlobals.mUser.getId(), friend_id, UserGlobals.mUser.getUserName(),friend_name,false, false,
                    UserGlobals.mUser.getUrl(), friend_URL);
        }else{
            invitation = new Rooms(friend_id,UserGlobals.mUser.getId(), friend_name,UserGlobals.mUser.getUserName(),false, false,
                    friend_URL, UserGlobals.mUser.getUrl());
        }

        //update the Image
        loadImageToView(friend_URL);
        //HIDE UI until database is ready
        hideUI();
        //Update own user and UI
        updateOwnUser(friend_id, friend_name);

        // Create the timer

        /**
         * Event listeners
         */
        acceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create room
                createRoom(invitation, Utilities.createMessageKey(UserGlobals.mUser.getId(), friend_id));
            }
        });

        rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity2();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endActivity2();
            }
        });

        DatabaseReference roomRef = FireBaseGlobals.getDataBase().getReference("ROOMS").child(Utilities.createMessageKey(UserGlobals.mUser.getId(), friend_id));
        roomRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.getValue()==null) return;

                invitation = dataSnapshot.getValue(Rooms.class);
                if(invitation.getAccepted1()&&invitation.getAccepted2()){
                    if(timer!=null){
                        timer.cancel();
                    }
                    String room_id = Utilities.createMessageKey(invitation.getId_1(), invitation.getId_2());
                    // Start game
                    if(UserGlobals.isChallanger) {
                        intent.putExtra("roomMaster", true);
                        intent.putExtra("player_id", "player1_id");
                        database = FirebaseDatabase.getInstance().getReference("SINGLEHANDROOMS");
                        SingleHandRooms room = new SingleHandRooms(room_id, "player1_id", "", false, false, false, false, false, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1, 0, 0);
                        database.child(room_id).setValue(room);
                        startActivity(intent);
                        intent.putExtra("room_id", room_id);
                        // Prevent BidWindow from opening twice
                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        endActivity2();
                    } else {
                        intent.putExtra("roomMaster", false);
                        intent.putExtra("player_id", "player2_id");
                        intent.putExtra("room_id", room_id);
                        Toast.makeText(InvitationActivity.this, "Waiting for room creation..", Toast.LENGTH_SHORT).show();
                        // Player 2 waits 1 second for room creation
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Prevent BidWindow from opening twice
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                endActivity2();
                            }
                        }, 1000);
                    }
                }
                else if(!invitation.getAccepted1()&&!invitation.getAccepted2()){
                    //Invitation was rejected so exit!
                    if(timer!=null){
                        timer.cancel();
                    }
                    Utilities.createToast("Invitation was rejected", InvitationActivity.this);
                    endActivity2();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //GETS RID OF THE TOP BAR
        try{
            this.getSupportActionBar().hide();
        }catch(Exception e){}
    }

    /**
     * Update our object in the database
     */
    private void updateOwnUser(String friend_id, final String friend_name){
        DatabaseReference userRef = FireBaseGlobals.getDataBase().getReference("USERS").child(UserGlobals.mUser.getId());
        UserGlobals.mUser.setPlayingWithId(friend_id);
        userRef.setValue(UserGlobals.mUser).addOnCompleteListener(new OnCompleteListener<Void>(){
            @Override
            public void onComplete(@NonNull Task<Void> task){
                if(task.isSuccessful()){
                    showUI(friend_name);
                }else{
                    timer.cancel();
                    endActivity();
                }
            }
        });
    }
    /**
     * Creates a room
     */
    private void createRoom(final Rooms room, final String roomId){
        //create the room
        DatabaseReference roomsRef = FireBaseGlobals.getDataBase().getReference("ROOMS").child(roomId);
        if(UserGlobals.isChallanger){
            invitation.setAccepted1(true);
        }
        else{
            invitation.setAccepted2(true);
        }
        String temp = room.getId_1();
        if(temp.equals(UserGlobals.mUser.getId())){
            temp = room.getId_2();
        }
        final String friend_id = temp;
        roomsRef.setValue(invitation).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //make sure we have the right name
                    if(UserGlobals.isChallanger){
                        sendRequest(friend_id);
                    }else{
                        createTimer();
                    }

                } else {
                    endActivity();
                }
            }
        });
    }

    /**
     * Inform the other player about the game
     * @param friend_id
     */
    public void sendRequest(final String friend_id){
        DatabaseReference friendRef = FireBaseGlobals.getDataBase().getReference("USERS").child(friend_id).child("playingWithId");
        friendRef.setValue(UserGlobals.mUser.getId()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task){
                if(task.isSuccessful()){
                    //here we update the UI and start the timer.
                    createTimer();
                }else{
                    endActivity();
                }
            }
        });
    }

    /**
     * Timer fucntion
     */
    public void createTimer(){
        timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                txt.setText(millisUntilFinished/1000+"s");
            }

            @Override
            public void onFinish() {
                Utilities.createToast("The other player did not response! Try Again later", InvitationActivity.this);
                timer.cancel();
                endActivity2();
            }
        }.start();
        cancelMode();
    }
    /**
     * Ends the activity
     */
    private void endActivity(){
        Utilities.createToast("Error while sending the invitation.. Try again!", InvitationActivity.this);
        if(invitation!=null){
            try{
                DatabaseReference ref = FireBaseGlobals.getDataBase().getReference("ROOMS").child(Utilities.createMessageKey(invitation.getId_1(), invitation.getId_2()));
                ref.removeValue();
            }catch (Exception e){
            }
        }
        resetUser();
        this.finish();
    }

    /**
     * Shows the image save in the database!
     */
    public void loadImageToView(String url){
        if(url.equals("NONE")){
            user_img.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(url).into(user_img);
        }
    }

    /**
     * METHODS TO MODIFY THE UI
     */
    public void hideUI(){
        acceptBtn.setVisibility(View.INVISIBLE);
        rejectBtn.setVisibility(View.INVISIBLE);
        user_img.setVisibility(View.INVISIBLE);
        txt.setText("Please wait...");
    }

    public void showUI(String friend_name){
        acceptBtn.setVisibility(View.VISIBLE);
        rejectBtn.setVisibility(View.VISIBLE);
        user_img.setVisibility(View.VISIBLE);
        txt.setText("Do you want to play with "+friend_name+"?");
    }

    public void cancelMode(){
        acceptBtn.setVisibility(View.INVISIBLE);
        rejectBtn.setVisibility(View.INVISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
    }

    public void resetUser(){
        String _id = UserGlobals.mUser.getId();
        UserGlobals.mUser.setOnline(true);
        UserGlobals.mUser.setPlayingWithId("NONE");
        DatabaseReference offLineRef = FireBaseGlobals.getDataBase().getReference("USERS").child(_id);
        offLineRef.setValue(UserGlobals.mUser);
    }
    public void endActivity2(){
        if(invitation!=null){
            try{
                DatabaseReference ref = FireBaseGlobals.getDataBase().getReference("ROOMS").child(Utilities.createMessageKey(invitation.getId_1(), invitation.getId_2()));
                ref.removeValue();
            }catch (Exception e){
            }
        }
        resetUser();
        this.finish();
    }
}

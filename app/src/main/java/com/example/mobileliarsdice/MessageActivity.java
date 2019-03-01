package com.example.mobileliarsdice;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobileliarsdice.Adapters.MessageAdapter;
import com.example.mobileliarsdice.Models.Messages;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    //UI
    CircleImageView profileImage;
    TextView profileName;
    ImageView back;
    ImageView send;
    EditText sendMessage;

    // DATABASE VARIABLES
    String messagesId;
    String sender_id;
    String sender;
    //BUNDLE VARIABLES
    String friend_id;
    String friend_name;
    String friend_url;
    //Reference for database
    DatabaseReference msgRef;

    //adapter and list for msgs
    MessageAdapter messageAdapter;
    List<Messages> allMsg;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //generate the id for the chat
        Intent prevIn = getIntent();

        //get current user variables
        sender_id = UserGlobals.mUser.getId();
        sender = UserGlobals.mUser.getUserName();

        //get bundle
        friend_id = prevIn.getExtras().getString("friend_id");
        friend_name = prevIn.getExtras().getString("friend_name");
        friend_url = prevIn.getExtras().getString("friend_url");
        //get ui components
        profileImage = findViewById(R.id.image_profile);
        profileName = findViewById(R.id.user_name);
        back = findViewById(R.id.back_button);
        send = findViewById(R.id.send_msg);
        sendMessage =  findViewById(R.id.message_to_send);

        recyclerView = findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);




        //set the ui
        profileName.setText(friend_name);
        if(friend_url.equals("NONE")){
            profileImage.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(friend_url).into(profileImage);
        }

        //Initialize list
        allMsg = new ArrayList<>();

        //get the messageId
        messagesId = Utilities.createMessageKey(FireBaseGlobals.getUser().getUid(), friend_id);
        //get the reference
        msgRef = FireBaseGlobals.getDataBase().getReference("MESSAGES").child(messagesId);
        //start the event listener!
        msgRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allMsg.clear();
                for(DataSnapshot c : dataSnapshot.getChildren()) {
                    //update ui...
                    allMsg.add(c.getValue(Messages.class));
                }

                messageAdapter = new MessageAdapter(MessageActivity.this, allMsg, UserGlobals.mUser.getUrl(), friend_url);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Mark this chat as read to avoid notifications.
        checkAsRead();

        //if the user would like to go back finish this activity
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // reset chat id
                UserGlobals.current_chat_id = "";
                //Exit
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMsg =  sendMessage.getText().toString();
                if(!newMsg.isEmpty()){
                    sendNewMessage(newMsg);
                }
                //clean the editText
                sendMessage.setText("");
                //Hide the keyboard
                sendMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);
                //Notify the other user that a new message has been received
                notifyOtherUser();
            }
        });

        //GETS RID OF THE TOP BAR
        try{
            this.getSupportActionBar().hide();
        }catch(Exception e){}
    }

    /**
     * Updates the database! No error handling because fuck it!
     * @param newMsg
     */
    private void sendNewMessage(String newMsg){
        //create model
        Messages msg = new Messages(sender_id, sender, friend_id, friend_name, newMsg);
        msgRef.push().setValue(msg);
    }

    /**
     * Notifies the other user that a new chat has been added.
     * This is useful for notifications and changing the icon when a message has been received.
     */
    private void notifyOtherUser(){
        DatabaseReference newChatRef = FireBaseGlobals.getDataBase().getReference("CHATS").child(friend_id).child(sender_id).child("newChat");
        newChatRef.setValue(true);
    }

    /**
     * Mark this chat as read.
     */
    private void checkAsRead(){
        DatabaseReference newChatRef = FireBaseGlobals.getDataBase().getReference("CHATS").child(sender_id).child(friend_id).child("newChat");
        newChatRef.setValue(false);
    }
}

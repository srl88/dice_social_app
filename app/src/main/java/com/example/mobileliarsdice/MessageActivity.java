package com.example.mobileliarsdice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    //IMAGE CODES
    final int GALLERY = 101;
    final int CAMERA = 102;


    //UI
    CircleImageView profileImage;
    TextView profileName;
    ImageView back;
    ImageView send;
    ImageView image;
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
        //back = findViewById(R.id.back_button);
        send = findViewById(R.id.send_msg);
        sendMessage =  findViewById(R.id.message_to_send);
        image = findViewById(R.id.picture);

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
                    sendNewMessage(newMsg, "");
                }
                //clean the editText
                sendMessage.setText("");
                //Hide the keyboard
                sendMessage.onEditorAction(EditorInfo.IME_ACTION_DONE);
                //Notify the other user that a new message has been received
                notifyOtherUser();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
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
    private void sendNewMessage(String newMsg, String newImage){
        //create model
        Messages msg = new Messages(sender_id, sender, friend_id, friend_name, newMsg, newImage);
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

    /**
     * Dialog for images
     */
    void OpenDialog(){
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder db = new AlertDialog.Builder(MessageActivity.this);
        db.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CharSequence chosen = options[which];
                if (chosen.equals("Camera")) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (i.resolveActivity(MessageActivity.this.getPackageManager()) != null) {

                        startActivityForResult(i, CAMERA);

                    } else {
                        //No camera!
                    }
                } else if (chosen.equals("Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(i, GALLERY);
                } else {
                    dialog.dismiss();
                }
            }
        });
        db.show();
    }

    /**
     * Handles the results from the dialog.
     */
    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode== Activity.RESULT_CANCELED) return;
        if(requestCode==CAMERA){
            try{
                //SEND TO DATABASE
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream st = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, st);
                byte[] imageByte = st.toByteArray();
                saveImage(null, imageByte);

            }catch (Exception e){
                // Error..
            }
        }
        else if(requestCode == GALLERY){
            //SEND TO DATABASE

            Uri imageUri = data.getData();
            saveImage(imageUri, null);
        }
    }

    /**
     * Functions to send the images to the data base.
     */
    public void saveImage(Uri imageUri, byte[] newByte){
        final StorageReference imgRef = FireBaseGlobals.getFirebaseStorage().getReference("MESSAGES").
                child(Float.toString(System.currentTimeMillis()));
        if(imageUri==null){
            imgRef.putBytes(newByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //if successfull we update the message
                            sendNewMessage("", uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utilities.createToast("The Image could not be saved! Please try again!", MessageActivity.this);
                }
            });
        }else{
            imgRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            //if successfull we update the message
                            sendNewMessage("", uri.toString());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utilities.createToast("The Image could not be saved! Please try again!", MessageActivity.this);
                }
            });
        }
    }

    //just in case
    @Override
    public void onDestroy(){
        this.finish();
        super.onDestroy();
    }

}

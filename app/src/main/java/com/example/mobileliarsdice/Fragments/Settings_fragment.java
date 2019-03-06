package com.example.mobileliarsdice.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.net.Uri;
import com.example.mobileliarsdice.Models.Chats;
import com.example.mobileliarsdice.Models.Users;
import com.google.firebase.database.ValueEventListener;
import com.example.mobileliarsdice.UserGlobals;
import com.example.mobileliarsdice.loginActivity;
import com.example.mobileliarsdice.FireBaseGlobals;
import com.example.mobileliarsdice.R;
import com.example.mobileliarsdice.Utilities;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.google.firebase.database.DataSnapshot;
import com.example.mobileliarsdice.deleteActivity;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;


public class Settings_fragment extends Fragment{


    FloatingActionButton editPicture;
    Button logOut;
    Button deleteAccount;
    Button save;


    SeekBar distanceBar;
    TextView user_name;
    TextView user_distance;
    CircleImageView user_img;

    byte[] imageByte;
    Uri imageUri;
    StorageTask uploadImageTask;
    ValueEventListener userListener;
    DatabaseReference refUser;


    static int current_selected_distance;

    private static final  int FILE_REQUEST = 1;
    private static final  int CAMERA_REQUEST = 2;
    private static final  int DELETE_REQUEST = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.settings, container, false);

        /**
         * GET UI ELEMENTS!
         */
        logOut = rootView.findViewById(R.id.logout_btn);
        deleteAccount = rootView.findViewById(R.id.delete_btn);
        editPicture = rootView.findViewById(R.id.editPicture);
        user_name = rootView.findViewById(R.id.user_name);
        user_img = rootView.findViewById(R.id.user_picture);
        save = rootView.findViewById(R.id.saveBtn);
        user_distance = rootView.findViewById(R.id.distaneTxt);
        distanceBar = rootView.findViewById(R.id.distanceBar);

        /**
         * INITIAL SET UP!
         */
        restoreUserProfile();


        /**
         * EVENT LISTENER TO UPDATE THE USER
         */
        refUser = FireBaseGlobals.getDataBase().getReference("USERS").child(FireBaseGlobals.getUser().getUid());
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //update
                UserGlobals.mUser = dataSnapshot.getValue(Users.class);
                restoreUserProfile();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // TODO:read documentation to see the type of error
            }
        };
        refUser.addValueEventListener(userListener);


        /**
         * EVENT LISTENER FOR THE SEEKBAR
         */
        distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //update selected distance
                current_selected_distance = progress;
                user_distance.setText((current_selected_distance+1)+" km");

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //if distance is different from the saved one, allow to save

                boolean updateDistance = (current_selected_distance+1)!=UserGlobals.mUser.getDistance();
                boolean updateImage = !(imageUri.toString().equals(UserGlobals.mUser.getUrl()));
                updateImage = updateImage || (imageByte!=null);

                if(updateDistance || updateImage){
                    save.setEnabled(true);
                }else{
                    save.setEnabled(false);
                }
            }
        });


        /**
         * LOGOUT
         */
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.deleteCredentials(FireBaseGlobals.getContext());
                Intent i = new Intent(FireBaseGlobals.getContext(), loginActivity.class);
                //empty the stack activity before we logout!
                FireBaseGlobals.logOut();
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });

        /**
         * TAKE A PICTURE
         */
        editPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenDialog();
            }
        });


        /**
         * DELETE ACCOUNT
         */
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder ad = new AlertDialog.Builder(getActivity());
                ad.setMessage("Do you want to delete your account permanently");
                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(getActivity(), deleteActivity.class);
                        startActivityForResult(i, DELETE_REQUEST);
                    }
                });
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //DO NOTHING!
                    }
                });
                ad.create().show();
            }
        });


        /**
         * SAVE DATA
         */
        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveProfile();
            }
        });

        return rootView;
    }

    /*******************************************************************
     * THE FOLLOWING METHODS ARE USED TO GET AN IMAGE AND SAVE LOCALLY *
     *******************************************************************/

    /**
     * Handles the dialog for the camera and files
     */
    void OpenDialog(){
        final CharSequence[] options = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
        db.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                CharSequence chosen = options[which];
                if (chosen.equals("Camera")) {
                    Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (i.resolveActivity(getActivity().getPackageManager()) != null) {

                        startActivityForResult(i, CAMERA_REQUEST);

                    } else {
                        //No camera!
                    }
                } else if (chosen.equals("Gallery")) {
                    Intent i = new Intent(Intent.ACTION_PICK);
                    i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(i, FILE_REQUEST);
                } else {
                    dialog.dismiss();
                }
            }
        });
        db.show();
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data){
        if(requestCode==Activity.RESULT_CANCELED) return;
        if(requestCode==CAMERA_REQUEST){
            try{
                imageUri = Uri.parse(UserGlobals.mUser.getUrl());
                Bitmap bm = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream st = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, st);
                imageByte = st.toByteArray();
                user_img.setImageBitmap(bm);
            }catch (Exception e){
                // Error..
            }
        }
        else if(requestCode == FILE_REQUEST){
            imageByte=null;
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(user_img);
        }
        else if(requestCode == DELETE_REQUEST){
            Intent i = new Intent(getActivity(), loginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            getActivity().finish();
        }
        save.setEnabled(true);
    }


    /**********************************************************
     * METHODS TO RESTORE THE INFORMATIOM FROM THE GLOBAL USER.*
     * ALLOWS TO HAVE REAL TIME INFORMATION.                  *
     **********************************************************/
    public void restoreUserProfile(){
        save.setEnabled(false);
        imageUri = Uri.parse(UserGlobals.mUser.getUrl());
        current_selected_distance = UserGlobals.mUser.getDistance() - 1 ;
        user_name.setText(UserGlobals.mUser.getUserName());
        user_distance.setText(UserGlobals.mUser.getDistance()+" Km ");
        imageByte = null;
        loadImageToView();
    }

    /**
     * Shows the image save in the database!
     */
    public void loadImageToView(){
        if(UserGlobals.mUser.getUrl().equals("NONE")){
            user_img.setImageResource(R.mipmap.app_foreground);
        }else{
            Picasso.get().load(UserGlobals.mUser.getUrl()).into(user_img);
        }
    }



    /*************************************************************************
     * THE FOLLOWING METHODS ARE TO UPDATE THE USER PROFILE AND ALL CONTACTS *
     *************************************************************************/

    /**
     * Saves the new data profile
     */
    public void saveProfile(){
        boolean updateDistance = (current_selected_distance+1)!=UserGlobals.mUser.getDistance();
        boolean updateImage = !(imageUri.toString().equals(UserGlobals.mUser.getUrl()));
        updateImage = updateImage || (imageByte!=null);
        if(updateImage){
            //update both regardless
            saveDistanceAndImage(imageUri ,current_selected_distance+1, imageByte);
        }
        else if(updateDistance){
            saveData("", current_selected_distance+1);
        }else{
            save.setEnabled(false);
        }
    }


    /**
     * Saves a distance and the image. The image is saved in the FILE database
     * @param newUri
     * @param newDistance
     */
    private void saveDistanceAndImage(Uri newUri, final int newDistance, byte[] newByte){
        //First we store the image
        final StorageReference imgRef = FireBaseGlobals.getFirebaseStorage().getReference("IMAGES").
                child(UserGlobals.mUser.getId()).
                child(Float.toString(System.currentTimeMillis()));

        if(newByte!=null){
            imgRef.putBytes(newByte).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>(){
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //if successfull we update the user
                            saveData(uri.toString(), newDistance);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utilities.createToast("The Image could not be saved! Please try again!", getActivity());
                }
            });

        }else{
            imgRef.putFile(newUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imgRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            //if successfull we update the user
                            saveData(uri.toString(), newDistance);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Utilities.createToast("The Image could not be saved! Please try again!", getActivity());
                }
            });
        }
    }

    /**
     * Save url of the image in the User profile
     */
    private void saveData(final String newUrl, final int newDistance) {
        //create the map
        HashMap<String, Object> newValues = new HashMap<>();
        if(newDistance>0){
            newValues.put("distance", newDistance);
        }
        if(!newUrl.isEmpty()){
            newValues.put("url", newUrl);
        }
        //update!
        DatabaseReference profileRef = FireBaseGlobals.getDataBase().getReference("USERS").child(UserGlobals.mUser.getId());
        profileRef.updateChildren(newValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    save.setEnabled(false);
                    Utilities.createToast("Your profile was updated!", getActivity());
                    if(!newUrl.isEmpty()){
                        //update the chats
                        updateUserChats(newUrl);
                    }
                } else {
                    Utilities.createToast("We could not update your profile! Try Again!",getActivity());
                }
            }
        });
    }

    /**
     * Updates the url in all users profiles!
     * @param url
     */
    private void updateUserChats(final String url){
        DatabaseReference chatRef = FireBaseGlobals.getDataBase().getReference("CHATS").child(UserGlobals.mUser.getId());
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot c : dataSnapshot.getChildren()){
                    Chats temp = c.getValue(Chats.class);
                    //update the chat url
                    DatabaseReference friendChatRef = FireBaseGlobals.getDataBase().getReference("CHATS").
                            child(temp.getId()).child(UserGlobals.mUser.getId()).child("url");
                    friendChatRef.setValue(url);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //Remove the event listeners once the user logsout or the app i shutdown!
    @Override
    public void onDestroy(){
        super.onDestroy();
        refUser.removeEventListener(userListener);
    }
}

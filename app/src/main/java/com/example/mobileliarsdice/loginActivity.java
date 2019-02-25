package com.example.mobileliarsdice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileliarsdice.Models.Users;
import com.example.mobileliarsdice.Models.Utilities;
import com.google.firebase.auth.FirebaseAuth;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class loginActivity extends AppCompatActivity {

    Boolean isSignIn = true;

    Button signUpOrIn;
    TextView link;
    TextView forgot;
    EditText email;
    EditText password;
    EditText userName;
    ProgressDialog pd;

    //TODO: FIX THE DIALOG PROGRESS
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        //get the ui elements
        signUpOrIn = findViewById(R.id.button);
        link = findViewById(R.id.link);
        forgot = findViewById(R.id.forgot);
        email = findViewById(R.id.lblEmail);
        password = findViewById(R.id.lblPassword);
        userName = findViewById(R.id.lblName);

        pd = new ProgressDialog(loginActivity.this, R.color.colorPrimary);
        pd.setIndeterminate(true);
        pd.setMessage("Authenticating...");

        //GETS RID OF THE TOP BAR
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
        }


        //Check if there is connectivity!
        if (isNetworkConnected()) {
            //we check for credentials here... email/password
            if (Utilities.areCredentialsAvailable(loginActivity.this)) {
                // sign in
                String emailText = Utilities.getEmail(loginActivity.this);
                String passwordText = Utilities.getPassword(loginActivity.this);
                signIn(emailText, passwordText);
            }
        } else {
            //No internet... go to single mode...
            //TODO: MAKE SINGLE MODE PLAYER

        }
        // event listeners
        signUpOrIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signin Mode
                String userText = userName.getText().toString();
                String emailText = email.getText().toString();
                String passwordText = password.getText().toString();
                if (isSignIn) {
                    //validate input
                    if (validateEmailAndPassword(emailText, passwordText)) {
                        signIn(emailText, passwordText);
                    }
                }
                //signup mode
                else {
                    if (validateAll(userText, emailText, passwordText)) {
                        signUp(userText, emailText, passwordText);
                    }
                }
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSignIn) {
                    link.setText(R.string.signUp2);
                    forgot.setVisibility(View.INVISIBLE);
                    userName.setVisibility(View.VISIBLE);
                    signUpOrIn.setText(R.string.signUp);
                } else {
                    link.setText(R.string.signIn2);
                    forgot.setVisibility(View.VISIBLE);
                    userName.setVisibility(View.INVISIBLE);
                    signUpOrIn.setText(R.string.signIn);
                }
                isSignIn = !isSignIn;
            }
        });

        //TODO: MAKE FORGOT PASSWORD EMAIL

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

    /**
     * Validates email and password based on firebase rules.
     *
     * @param email
     * @param password
     */
    private Boolean validateEmailAndPassword(String email, String password) {

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Utilities.createToast("Enter a valid email address.", loginActivity.this);
            return false;
        }

        if (password.isEmpty() || password.length() < 5) {
            Utilities.createToast("The password must be 6 characters or longer.", loginActivity.this);
            return false;
        }

        return true;
    }

    /**
     * Validates username, password and email. Username must be 6 characters (this can be changed)
     *
     * @param userName
     * @param email
     * @param password
     * @return
     */
    private Boolean validateAll(String userName, String email, String password) {
        if (userName.isEmpty() || userName.length() < 5) {
            Utilities.createToast("The username must be 6 characters or longer.", loginActivity.this);
            return false;
        }
        return validateEmailAndPassword(email, password);
    }

    /**
     * Signs into the account
     *
     * @param email
     * @param password
     */
    private void signIn(final String email, final String password) {
        pd.show();
        FirebaseAuth mAuth = FireBaseGlobals.getAuth();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // update location
                            pd.setMessage("Accessing your location...");
                            Location location = Utilities.getCurrentLocation(loginActivity.this);

                            if (location != null) {
                                //update database
                                pd.setMessage("Updating your profile location...");
                                Utilities.putCredentials(loginActivity.this, email, password);
                                updateCurrentUser(location.getLongitude(), location.getLatitude());

                            } else {
                                pd.dismiss();
                                Utilities.createToast("APPLICATION CANNOT ACCESS THE GPS. PLEASE UPDATE IN DEVICE SETTINGS!", loginActivity.this);
                            }
                        } else {
                            //error
                            pd.dismiss();
                            Utilities.createToast("Authentication Error.", loginActivity.this);

                        }

                    }
                });
    }


    /*************************************************
     * METHOD TO UPDATE THE CURRENT USER AFTER LOGIN *
     *************************************************/

    //Updates location and online.
    private void updateCurrentUser(final Double longitude, final Double latitude) {
        String _id = FireBaseGlobals.getUser().getUid();
        DatabaseReference refUser = FireBaseGlobals.getDataBase().getReference("USERS/").child(_id);
        HashMap<String, Object> newValues = new HashMap<>();
        newValues.put("longitude", longitude);
        newValues.put("latitude", latitude);
        newValues.put("online", true);
        refUser.updateChildren(newValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                pd.dismiss();
                if(databaseError==null){
                    Intent i = new Intent(loginActivity.this, Main.class);
                    startActivity(i);
                }else{
                    Utilities.createToast("Error while updating your profile... Try again!", loginActivity.this);
                }
            }
        });
    }


    /**
     * Signs up a new user and creates a user object in the database.
     *
     * @param userName
     * @param email
     * @param password
     */
    private void signUp(final String userName, final String email, final String password) {
        // create account
        pd.show();
        FirebaseAuth mAuth = FireBaseGlobals.getAuth();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //get location and create user
                    pd.setMessage("Accessing your location...");
                    Location location = Utilities.getCurrentLocation(loginActivity.this);
                    if (location != null) {
                        String userId = FireBaseGlobals.getUser().getUid();
                        DatabaseReference reference = FireBaseGlobals.getDataBase().getReference("Users").child(userId);
                        Users newUser = new Users(userName, "NONE", userId, location.getLongitude(), location.getLatitude(), true, 0);
                        pd.setMessage("Creating profile...");
                        reference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    //save credentials
                                    Utilities.putCredentials(loginActivity.this, email, password);
                                    Intent i = new Intent(loginActivity.this, Main.class);
                                    startActivity(i);
                                } else {
                                    Utilities.createToast("Error while creating your account... Try to sign in!", loginActivity.this);
                                }
                            }
                        });

                    } else {
                        pd.dismiss();
                        Utilities.createToast("APPLICATION CANNOT ACCESS THE GPS. PLEASE UPDATE IN SETTINGS!", loginActivity.this);
                    }
                }else {
                    pd.dismiss();
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Utilities.createToast("This email is already registered!. Maybe you forgot your password?", loginActivity.this);
                    } else {
                        Utilities.createToast("Authentication Error.", loginActivity.this);
                    }
                }

            }
        });
    }
}




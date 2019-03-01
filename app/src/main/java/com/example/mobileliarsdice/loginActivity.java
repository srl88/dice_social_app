package com.example.mobileliarsdice;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mobileliarsdice.Models.Users;
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

public class loginActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    Boolean isSignIn = true;

    Button signUpOrIn;
    TextView link;
    TextView forgot;
    EditText email;
    EditText password;
    EditText userName;
    ProgressDialog pd;

    final static int LOCATION_PERMISSION = 100;


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

        //GETS RID OF THE TOP BAR
        try {
            this.getSupportActionBar().hide();
        } catch (Exception e) {
        }


        //Create event listeners!
        // event listeners

        /**
         * EVENTLISTENERS BLOCK
         */
        signUpOrIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //signin Mode
                Boolean isLocation = checkLocationPermission();
                if(isLocation){
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
                }else{
                    //THE PERMISSIONS BLOCK TAKES CARE OF THIS!
                }
            }
        });

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean isLocation = checkLocationPermission();
                if(isLocation){
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
                else{
                    //THE PERMISSIONS BLOCK TAKES CARE OF THIS!
                }
            }
        });


        //TODO: MAKE FORGOT PASSWORD EMAIL

        /**
         * DONE EVENTLISTENERS BLOCK
         */


        /**
         * AUTOLOGIN BLOCK
         * Autologin... connection and location are always required
         */
        Boolean isConnection = isNetworkConnected();
        Boolean isLocation = checkLocationPermission();
        if(isLocation){
            if(isConnection){
                Boolean isUserConnected =  isUserConnected();
                if(isUserConnected){
                        pd.setMessage("Updating your location...");
                        pd.show();
                        // goes automatically to main!
                        Location currentLocation = getCurrentLocation();
                        if(currentLocation!=null){
                            updateCurrentUser(currentLocation.getLongitude(), currentLocation.getLatitude());
                        }else{
                            pd.dismiss();
                            Utilities.createToast("THE APPLICATION COULD NOT RETRIEVE YOUR LOCATION", loginActivity.this);
                        }
                }
                else{
                    Boolean areCredentialsStored = Utilities.areCredentialsAvailable(loginActivity.this);
                    if(areCredentialsStored){
                        String storedEmail = Utilities.getEmail(loginActivity.this);
                        String storedPassword = Utilities.getPassword(loginActivity.this);
                        signIn(storedEmail, storedPassword);
                    }
                }
            }
            else{
                //network is not connected! Go to single mode after informing the user!
                //TODO: INFORM USER AND GO TO SINGLE MODE
            }
        }else{
            //There is no permission! This is taken care by the permission manager.
        }

        /**
         * DONE AUTOLOGIN BLOCK
         */

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
     * Checks if the user is already login
     * @return
     */
    private boolean isUserConnected(){
        return FireBaseGlobals.getUser()!=null;
    }

    /**
     *  Goes to Main!
     */
    private void goToMain(){
        Intent i = new Intent(loginActivity.this, Main.class);
        startActivity(i);
        pd.dismiss();
        this.finish();
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
        pd.setMessage("Authenticating...");
        pd.show();
        //update credentials in case the password was changed!
        Utilities.putCredentials(loginActivity.this, email, password);
        FirebaseAuth mAuth = FireBaseGlobals.getAuth();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(loginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // update location
                            pd.setMessage("Accessing your location...");
                            Location currentLocation = getCurrentLocation();
                            if (currentLocation != null) {
                                //update database
                                updateCurrentUser(currentLocation.getLongitude(), currentLocation.getLatitude());

                            } else {
                                pd.dismiss();
                                Utilities.createToast("THE APPLICATION COULD NOT RETRIEVE YOUR LOCATION", loginActivity.this);
                            }
                        } else {
                            //error
                            pd.dismiss();
                            Utilities.createToast("Authentication Error. Make sure your password and email are correct", loginActivity.this);

                        }

                    }
                });
    }


    /*************************************************
     * METHOD TO UPDATE THE CURRENT USER AFTER LOGIN *
     *************************************************/

    //Updates location and online.
    private void updateCurrentUser(final Double longitude, final Double latitude) {
        pd.setMessage("Updating your profile location...");
        final String _id = FireBaseGlobals.getUser().getUid();
        DatabaseReference refUser = FireBaseGlobals.getDataBase().getReference("USERS/").child(_id);
        HashMap<String, Object> newValues = new HashMap<>();
        newValues.put("longitude", longitude);
        newValues.put("latitude", latitude);
        newValues.put("online", true);
        refUser.updateChildren(newValues, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if(databaseError==null){

                    DatabaseReference userRef = FireBaseGlobals.getDataBase().getReference("USERS").child(_id);
                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            pd.setMessage("Retrieving your profile information...");
                            UserGlobals.mUser = dataSnapshot.getValue(Users.class);
                            goToMain();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pd.dismiss();
                        }
                    });

                }else{
                    pd.dismiss();
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
        FirebaseAuth mAuth = FireBaseGlobals.getAuth();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //get location and create user
                    pd.setMessage("Accessing your location...");
                    Location currentLocation = getCurrentLocation();
                    if (currentLocation != null) {
                        String userId = FireBaseGlobals.getUser().getUid();
                        DatabaseReference reference = FireBaseGlobals.getDataBase().getReference("USERS").child(userId);
                        final Users newUser = new Users(userName, "NONE", userId, currentLocation.getLongitude(), currentLocation.getLatitude(), true, 0, 50);
                        pd.setMessage("Creating profile...");
                        reference.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    //save credentials
                                    Utilities.putCredentials(loginActivity.this, email, password);
                                    UserGlobals.mUser = newUser;
                                    goToMain();
                                } else {
                                    Utilities.createToast("Error while creating your account... Try again!", loginActivity.this);
                                }
                            }
                        });

                    } else {
                        pd.dismiss();
                        Utilities.createToast("APPLICATION CANNOT ACCESS THE LOCATION. PLEASE UPDATE IN SETTINGS!", loginActivity.this);
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

    /**********************************************************
     * METHODS FOR LOCATION REQUESTING THE PERMISSIONS         *
     **********************************************************/

    /**
     * checks if we have permission to use the location. If we don't we ask the user
     * @return true if so... otherwise false.
     */
    public boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(FireBaseGlobals.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            try {
                ActivityCompat.requestPermissions(loginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION);
            }catch (Exception e) {
                System.out.print(e);
            }
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Same as above but returns the location
     * @return
     */
    public Location getCurrentLocation(){
        //This is always required!
        if (ActivityCompat.checkSelfPermission(FireBaseGlobals.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ){
            LocationManager locationManager = (LocationManager) FireBaseGlobals.getContext().getSystemService(Context.LOCATION_SERVICE);
            return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

        }
        return null;
    }

    /**
     * This is the call back from the previous method!
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //error checking
        if(grantResults.length>0){
            //if permission was granted!
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                //Permission was granted!
                Utilities.createToast("Please try to login again!", loginActivity.this);
                //If the user has clicked on the never show again button
            }else if(!shouldShowRequestPermissionRationale(permissions[0])){
                try {
                    Utilities.createToast("In order to play with other players we required  your location, please update your settings.", loginActivity.this);
                    Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(i);
                } catch (Exception e) {
                    Utilities.createToast("The application could not redirect you to the settings menu. Please do it manually.", loginActivity.this);
                }
            }
        }
    }
}




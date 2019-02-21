package com.example.mobileliarsdice;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;


/**
 * Class for firebase globals. This is a singleton pattern.
 * One object for the whole application.
 */
public class FireBaseGlobals extends Application {

    //Creation at before the app gets launched...
    private static Context appContext;
    private static FirebaseAuth mAuth;
    private static FirebaseApp fApp;
    private static FirebaseDatabase fDataBase;


    /**
     * This is the application context! Singleton pattern!
     */
    @Override
    public void onCreate() {
        super.onCreate();
        appContext = this;
        //fApp is no longer needed.
        fApp = FirebaseApp.initializeApp(appContext);
        fDataBase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }


    /**
     * Returns the FirebaseAuth object.
     * @return
     */
    public static FirebaseAuth getAuth() {
        return mAuth;
    }

    /**
     * Returns the signed in user object (Different from a user player)
     * @return
     */
    public static FirebaseUser getUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Returns the FirebaseDataBase object.
     * @return
     */
    public static FirebaseDatabase getDataBase() {
        return fDataBase;
    }

    /**
     * Returns the app context
     * @return
     */
    public static Context getContext(){return appContext;}
}

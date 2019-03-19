package com.example.mobileliarsdice;
import android.app.Application;
import android.content.Context;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

/**
 * Class for firebase globals. This is a singleton pattern.
 * One object for the whole application.
 */
public class FireBaseGlobals extends Application {

    //Creation at before the app gets launched...
    private static Context appContext;
    private static FirebaseAuth mAuth = null;
    private static FirebaseApp fApp;
    private static FirebaseDatabase fDataBase;
    private static FirebaseStorage fStorage;

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
        fStorage = FirebaseStorage.getInstance();
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
        if(mAuth==null) return null;
        else if(mAuth.getCurrentUser() == null){
            return null;
        }
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

    /**
     * Returns the instance of the firebaseStorage
     * @return
     */
    public static FirebaseStorage getFirebaseStorage(){return fStorage;}

    /**
     * Logout!
     */
    public static void logOut(){
        mAuth.signOut();
    }
}

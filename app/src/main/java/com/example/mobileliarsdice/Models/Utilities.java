package com.example.mobileliarsdice.Models;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;
import com.example.mobileliarsdice.FireBaseGlobals;

/**
 * Static Utility class
 */
public class Utilities {

    /****************************
     * METHODS FOR CREDENTIALS  *
     ****************************/
    //TODO: MOVE THIS methods TO A UTILITY CLASS

    /**
     * Checks if there are credentials saved in the phone
     * @param c
     * @return
     */
    public static boolean areCredentialsAvailable(Context c) {
        SharedPreferences info = c.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return !(info.getString("email", "").isEmpty() || info.getString("password", "").isEmpty());
    }

    /**
     * Returns the email saved in the phone.
     * @param c
     * @return
     */
    public static String getEmail(Context c) {
        SharedPreferences info = c.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return info.getString("email", "");
    }

    /**
     * Returns the password save in the phone.
     * @param c
     * @return
     */
    public static String getPassword(Context c) {
        SharedPreferences info = c.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        return info.getString("password", "");
    }

    /**
     * Updates the email and password saved in the phone.
     * @param c
     * @param email
     * @param password
     */
    public static void putCredentials(Context c, String email, String password) {
        SharedPreferences info = c.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        info.edit().putString("email", email).apply();
        info.edit().putString("password", password).apply();
    }

    /**********************************************************
     * METHODS FOR LOCATION AND CAMERA WITH THEIR PERMISSIONS *
     **********************************************************/
    //TODO: ONLY LOCATION IMPLEMENTED... WE SHOULD MOVE THE CAMERA IN SETTINGS...
    public static Location getCurrentLocation(Activity activity) {
        //Check for permissions
        if (ActivityCompat.checkSelfPermission(FireBaseGlobals.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED ) {
            try{
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }catch (Exception e){
                System.out.print(e);
            }
            return null;
        }
        LocationManager locationManager = (LocationManager) FireBaseGlobals.getContext().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
    }



    /**
     * Displays a toast message. This could be moved to a utility class...
     * @param msg
     */
    public static void createToast(String msg, Activity activity){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a key for messages based on two users
     */
    public static int createMessageKey(String str1, String str2){
        return str1.compareTo(str2);
    }

}

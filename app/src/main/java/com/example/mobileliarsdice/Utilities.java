package com.example.mobileliarsdice;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;


/**
 * Static Utility class
 */
public class Utilities {

    /****************************
     * METHODS FOR CREDENTIALS  *
     ****************************/

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

    public static void deleteCredentials(Context c){
        SharedPreferences info = c.getSharedPreferences("userDetails", Context.MODE_PRIVATE);
        info.edit().remove("email").commit();
        info.edit().remove("password").commit();
    }

    /**
     * Displays a toast message. This could be moved to a utility class...
     * @param msg
     */
    public static void createToast(String msg, Activity activity){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Generates a key for chat based on two users
     */
    public static String createMessageKey(String str1, String str2) {
        if (str1.compareTo(str2) > 0) {
            return str1 + str2;
        } else {
            return str2 + str1;
        }
    }
}

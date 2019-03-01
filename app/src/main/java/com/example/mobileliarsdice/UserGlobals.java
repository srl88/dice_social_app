package com.example.mobileliarsdice;

import com.example.mobileliarsdice.Models.Users;

/**
 * Class to keep track of the state of the app.
 * User, chat currently in the view, is the application on the background... and so on..
 * Useful for notifications
 */
public class UserGlobals {

    static public  Users mUser = null;
    static public String current_chat_id = "";

}

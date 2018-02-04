package com.mail.pengaduanmail.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.mail.pengaduanmail.Main;

import java.util.HashMap;

/**
 * Created by fandis on 09/01/2018.
 */

public class SessionManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "Sesi";

    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_NAME = "nama";
    public static final String KEY_EMAIL = "email";
//    public static final String KEY_NOTELP = "no_telp";

    public SessionManager(Context contex) {
        this._context = contex;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String name, String email){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
//        editor.putString(KEY_NOTELP, no_telp);
        editor.commit();
    }

    public void cekLogin(){
        if (!this.isLoggedIn()){
            Intent i = new Intent(_context, Main.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);
        }
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_NAME, pref.getString(KEY_NAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
//        user.put(KEY_NOTELP, pref.getString(KEY_NOTELP, null));
        return user;
    }

    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, Main.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    private boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, true);
    }

}

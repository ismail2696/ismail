package com.mail.pengaduanmail.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by fandis on 20/01/2018.
 */

public class PreferenceHelper {

    private final String INTRO = "intro";
    private final String NAME = "name";
    private final String USERNAME = "username";
    private final String HOBBY = "hobby";
    private SharedPreferences app_prefs;
    private Context context;

    public PreferenceHelper(Context context) {
        app_prefs = context.getSharedPreferences("shared",
                Context.MODE_PRIVATE);
        this.context = context;
    }

    public void putIsLogin(boolean loginorout) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putBoolean(INTRO, loginorout);
        edit.commit();
    }
    public boolean getIsLogin() {
        return app_prefs.getBoolean(INTRO, false);
    }

    public void putName(String loginorout) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(NAME, loginorout);
        edit.commit();
    }
    public String getName() {
        return app_prefs.getString(NAME, "");
    }

    public void putUsername(String loginorout) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(USERNAME, loginorout);
        edit.commit();
    }
    public String getUsername() {
        return app_prefs.getString(USERNAME, "");
    }

    public void putHobby(String loginorout) {
        SharedPreferences.Editor edit = app_prefs.edit();
        edit.putString(HOBBY, loginorout);
        edit.commit();
    }
    public String getHobby() {
        return app_prefs.getString(HOBBY, "");
    }

}

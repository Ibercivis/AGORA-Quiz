package com.ibercivis.agora;
import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "";
    private static final String KEY_TOKEN = "";
    private static final Boolean IS_LOGGED = false;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void createLoginSession(String username, String token) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_TOKEN, token);
        editor.putBoolean(String.valueOf(IS_LOGGED), true);
        editor.commit();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    public Boolean getIsLogged(){return prefs.getBoolean(String.valueOf(IS_LOGGED), false);}

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}


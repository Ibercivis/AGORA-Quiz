package com.ibercivis.agora;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_TOKEN = "token";
    private static final String IS_LOGGED = "isLogged";
    private static final String IS_ONBOARDING_COMPLETED = "isOnboardingCompleted";
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
        editor.putBoolean(IS_LOGGED, true);
        editor.commit();
    }

    public String getUsername() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public void setKeyUsername(String username) {
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }
    public Boolean getIsLogged() {
        return prefs.getBoolean(IS_LOGGED, false);
    }

    public void logoutUser() {
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_TOKEN);
        editor.putBoolean(IS_LOGGED, false);
        editor.commit();
    }

    public void setOnboardingCompleted(boolean completed) {
        editor.putBoolean(IS_ONBOARDING_COMPLETED, completed);
        editor.commit();
    }

    public boolean isOnboardingCompleted() {
        return prefs.getBoolean(IS_ONBOARDING_COMPLETED, false);
    }
}


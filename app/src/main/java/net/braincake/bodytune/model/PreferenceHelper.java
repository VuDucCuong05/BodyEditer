package net.braincake.bodytune.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {
    private static final String PREF_FIRST_RUN = "first_run";
    private static final String SET_LANGUAGE = "set_language";

    private final SharedPreferences sharedPreferences;

    public PreferenceHelper(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean isFirstRun() {
        return sharedPreferences.getBoolean(PREF_FIRST_RUN, true);
    }

    public void setFirstRun(boolean firstRun) {
        sharedPreferences.edit().putBoolean(PREF_FIRST_RUN, firstRun).apply();
    }

    public String getLanguage() {
        return sharedPreferences.getString(SET_LANGUAGE, "en");
    }

    public void setLanguage(String keyLanguage) {
        sharedPreferences.edit().putString(SET_LANGUAGE, keyLanguage).apply();
    }


}

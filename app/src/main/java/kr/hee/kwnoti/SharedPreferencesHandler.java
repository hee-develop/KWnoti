package kr.hee.kwnoti;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHandler {
    private static SharedPreferences SHARED_PREFERENCES = null;

    public SharedPreferencesHandler(Context context) {
        if (SHARED_PREFERENCES != null) return;
        SHARED_PREFERENCES = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static SharedPreferences getSharedPreferences() {
        return SHARED_PREFERENCES;
    }
    public static SharedPreferences.Editor getSharedPreferencesEditor() {
        return SHARED_PREFERENCES.edit();
    }
}

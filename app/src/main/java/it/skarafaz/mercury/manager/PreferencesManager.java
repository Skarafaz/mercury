package it.skarafaz.mercury.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.skarafaz.mercury.R;

public class PreferencesManager {
    private static final String FIRST_KEY = "FIRST";
    private static final String CONFIG_DIR_KEY = "CONFIG_DIR";
    private static PreferencesManager instance;
    private boolean first;
    private String configDir;

    private PreferencesManager() {
    }

    public static synchronized PreferencesManager getInstance() {
        if (instance == null) {
            instance = new PreferencesManager();
        }
        return instance;
    }

    public void load(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        first = preferences.getBoolean(FIRST_KEY, true);
        configDir = preferences.getString(CONFIG_DIR_KEY, context.getResources().getString(R.string.app_name));
    }

    public void save(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(FIRST_KEY, first);
        editor.putString(CONFIG_DIR_KEY, configDir);
        editor.apply();
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public String getConfigDir() {
        return configDir;
    }

    public void setConfigDir(String configDir) {
        this.configDir = configDir;
    }
}

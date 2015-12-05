package it.skarafaz.mercury.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.skarafaz.mercury.MercuryApplication;

public class SettingsManager {
    private static SettingsManager instance;
    private SharedPreferences prefs;
    // app
    private String logDir;
    private String logFile;
    // user
    private int defaultPort;
    private String defaultServerLabel;
    private String defaultCommandLabel;
    private boolean sortCommands;

    private SettingsManager() {
        prefs = PreferenceManager.getDefaultSharedPreferences(MercuryApplication.getContext());
        //app
        logDir = "log";
        logFile = "mercury.log";
        // user
        defaultPort = prefs.getInt("defaultPort", 22);
        defaultServerLabel = prefs.getString("defaultServerLabel", "Server");
        defaultCommandLabel = prefs.getString("defaultCommandLabel", "Command");
        sortCommands = prefs.getBoolean("sortCommands", false);
    }

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    public String getLogDir() {
        return logDir;
    }

    public String getLogFile() {
        return logFile;
    }

    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
        prefs.edit().putInt("defaultPort", this.defaultPort).apply();
    }

    public String getDefaultServerLabel() {
        return defaultServerLabel;
    }

    public void setDefaultServerLabel(String defaultServerLabel) {
        this.defaultServerLabel = defaultServerLabel;
        prefs.edit().putString("defaultServerLabel", this.defaultServerLabel).apply();
    }

    public String getDefaultCommandLabel() {
        return defaultCommandLabel;
    }

    public void setDefaultCommandLabel(String defaultCommandLabel) {
        this.defaultCommandLabel = defaultCommandLabel;
        prefs.edit().putString("defaultCommandLabel", this.defaultCommandLabel).apply();
    }

    public boolean getSortCommands() {
        return sortCommands;
    }

    public void setSortCommands(boolean sortCommands) {
        this.sortCommands = sortCommands;
        prefs.edit().putBoolean("sortCommands", this.sortCommands).apply();
    }
}

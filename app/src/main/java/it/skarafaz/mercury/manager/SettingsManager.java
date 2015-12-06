package it.skarafaz.mercury.manager;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.skarafaz.mercury.MercuryApplication;

public class SettingsManager {
    public static final String DEFAULT_PORT = "defaultPort";
    public static final String DEFAULT_SERVER_LABEL = "defaultServerLabel";
    public static final String DEFAULT_COMMAND_LABEL = "defaultCommandLabel";
    public static final String SORT_COMMANDS = "sortCommands";
    private static SettingsManager instance;
    private SharedPreferences prefs;
    // app
    private String logDir;
    private String logFile;
    private String wikiUrl;
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
        wikiUrl = "http://www.skarafaz.ovh/mercury-ssh";
        // user
        defaultPort = prefs.getInt(DEFAULT_PORT, 22);
        defaultServerLabel = prefs.getString(DEFAULT_SERVER_LABEL, "Server");
        defaultCommandLabel = prefs.getString(DEFAULT_COMMAND_LABEL, "Command");
        sortCommands = prefs.getBoolean(SORT_COMMANDS, false);
    }

    public static synchronized SettingsManager getInstance() {
        if (instance == null) {
            instance = new SettingsManager();
        }
        return instance;
    }

    // app
    public String getLogDir() {
        return logDir;
    }

    public String getLogFile() {
        return logFile;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    // user
    public int getDefaultPort() {
        return defaultPort;
    }

    public void setDefaultPort(int defaultPort) {
        this.defaultPort = defaultPort;
        prefs.edit().putInt(DEFAULT_PORT, this.defaultPort).apply();
    }

    public String getDefaultServerLabel() {
        return defaultServerLabel;
    }

    public void setDefaultServerLabel(String defaultServerLabel) {
        this.defaultServerLabel = defaultServerLabel;
        prefs.edit().putString(DEFAULT_SERVER_LABEL, this.defaultServerLabel).apply();
    }

    public String getDefaultCommandLabel() {
        return defaultCommandLabel;
    }

    public void setDefaultCommandLabel(String defaultCommandLabel) {
        this.defaultCommandLabel = defaultCommandLabel;
        prefs.edit().putString(DEFAULT_COMMAND_LABEL, this.defaultCommandLabel).apply();
    }

    public boolean getSortCommands() {
        return sortCommands;
    }

    public void setSortCommands(boolean sortCommands) {
        this.sortCommands = sortCommands;
        prefs.edit().putBoolean(SORT_COMMANDS, this.sortCommands).apply();
    }
}

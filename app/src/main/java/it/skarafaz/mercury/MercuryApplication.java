package it.skarafaz.mercury;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.skarafaz.mercury.manager.ConfigManager;
import it.skarafaz.mercury.manager.PreferencesManager;

public class MercuryApplication extends Application {
    private static Context context;
    private static ObjectMapper mapper = new ObjectMapper();

    public static Context getContext() {
        return context;
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        PreferencesManager preferencesManager = PreferencesManager.getInstance();
        preferencesManager.load(this);
        if (preferencesManager.isFirst()) {
            preferencesManager.setFirst(false);
            preferencesManager.save(this);
            if (!ConfigManager.getInstance().createConfigDir()) {
                Toast.makeText(MercuryApplication.getContext(), R.string.config_dir_creation_failed, Toast.LENGTH_LONG).show();
            }
        }
    }
}

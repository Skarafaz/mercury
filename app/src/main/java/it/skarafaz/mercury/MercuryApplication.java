package it.skarafaz.mercury;

import android.app.Application;
import android.content.Context;
import android.view.ViewConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class MercuryApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(MercuryApplication.class);
    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        // hack for devices with hw options button
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}

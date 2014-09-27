package it.skarafaz.mercury;

import android.app.Application;
import android.content.Context;

import it.skarafaz.mercury.manager.ServerManager;

/**
 * Created by Skarafaz on 20/09/2014.
 */
public class MercuryApplication  extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        ServerManager.getInstance().init();
    }

    public static Context getContext() {
        return context;
    }
}

package it.skarafaz.mercury;

import android.app.Application;
import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    }
}

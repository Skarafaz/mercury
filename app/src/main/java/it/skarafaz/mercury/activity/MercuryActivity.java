package it.skarafaz.mercury.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class MercuryActivity extends AppCompatActivity {
    private static final int ACTION_BAR_ELEVATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarElevation();
    }

    private void setActionBarElevation() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setElevation(ACTION_BAR_ELEVATION);
        }
    }
}

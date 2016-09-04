package it.skarafaz.mercury.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public abstract class MercuryActivity extends AppCompatActivity {
    private static final int ACTION_BAR_ELEVATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarElevation();
    }

    private void setActionBarElevation() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(ACTION_BAR_ELEVATION);
        }
    }
}

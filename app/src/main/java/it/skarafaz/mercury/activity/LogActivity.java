package it.skarafaz.mercury.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import it.skarafaz.mercury.R;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActionBarElevation();
        setContentView(R.layout.activity_log);
    }

    private void setActionBarElevation() {
        ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setElevation(0);
        }
    }
}

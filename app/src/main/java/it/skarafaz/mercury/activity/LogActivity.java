package it.skarafaz.mercury.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import it.skarafaz.mercury.R;

public class LogActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_log);
    }
}

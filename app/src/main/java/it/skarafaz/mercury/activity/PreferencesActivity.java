package it.skarafaz.mercury.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.manager.PreferencesManager;

public class PreferencesActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        TextView configDirValue = (TextView) findViewById(R.id.config_dir_value);
        configDirValue.setText(String.format("/%s", PreferencesManager.getInstance().getConfigDir()));
        LinearLayout configDir = (LinearLayout) findViewById(R.id.config_dir);
        configDir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(PreferencesActivity.class.getSimpleName(), "configDir");
            }
        });
    }
}

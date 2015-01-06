package it.skarafaz.mercury.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.ServerPagerAdapter;
import it.skarafaz.mercury.data.LoadConfigTaskResult;
import it.skarafaz.mercury.manager.ConfigManager;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_main);
        new AsyncTask<Void, Void, LoadConfigTaskResult>() {
            @Override
            protected LoadConfigTaskResult doInBackground(Void... params) {
                return ConfigManager.getInstance().load();
            }

            @Override
            protected void onPostExecute(LoadConfigTaskResult result) {
                if (ConfigManager.getInstance().getServers().size() > 0) {
                    ServerPagerAdapter adapter = new ServerPagerAdapter(getSupportFragmentManager(), ConfigManager.getInstance().getServers());
                    ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                    viewPager.setAdapter(adapter);
                    viewPager.setVisibility(View.VISIBLE);
                } else {
                    TextView message = (TextView) findViewById(R.id.message);
                    message.setText(getEmptyMessage(result));
                    LinearLayout empty = (LinearLayout) findViewById(R.id.empty);
                    empty.setVisibility(View.VISIBLE);
                }
                ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
                progress.setVisibility(View.INVISIBLE);
            }
        }.execute();
    }

    private String getEmptyMessage(LoadConfigTaskResult result) {
        String message = "";
        switch (result) {
            case SUCCESS:
                StringBuilder sb = new StringBuilder();
                sb.append(getResources().getString(R.string.no_servers));
                sb.append("\n");
                sb.append(ConfigManager.getInstance().getConfigDir());
                message = sb.toString();
                break;
            case CANNOT_READ_EXT_STORAGE:
                message = getResources().getString(R.string.cannot_read_ext_storage);
                break;
            case CANNOT_CREATE_CONFIG_DIR:
                message = getResources().getString(R.string.cannot_create_config_dir);
                break;
        }
        return message;
    }
}

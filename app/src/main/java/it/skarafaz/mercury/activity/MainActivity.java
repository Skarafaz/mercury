package it.skarafaz.mercury.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.ServerPagerAdapter;
import it.skarafaz.mercury.enums.LoadConfigTaskResult;
import it.skarafaz.mercury.manager.ConfigManager;


public class MainActivity extends MercuryActivity {
    @Bind(R.id.progress)
    protected ProgressBar progress;
    @Bind(R.id.empty)
    protected LinearLayout empty;
    @Bind(R.id.message)
    protected TextView message;
    @Bind(R.id.pager)
    protected ViewPager pager;

    private boolean loading = false;
    private ServerPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new ServerPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        load();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reload:
                load();
                return true;
            case R.id.action_log:
                startActivity(new Intent(this, LogActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void load() {
        if (!loading) {
            new AsyncTask<Void, Void, LoadConfigTaskResult>() {
                @Override
                protected void onPreExecute() {
                    loading = true;
                    progress.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                    pager.setVisibility(View.INVISIBLE);
                }

                @Override
                protected LoadConfigTaskResult doInBackground(Void... params) {
                    return ConfigManager.getInstance().load();
                }

                @Override
                protected void onPostExecute(LoadConfigTaskResult result) {
                    progress.setVisibility(View.INVISIBLE);
                    if (ConfigManager.getInstance().getServers().size() > 0) {
                        adapter.updateServers(ConfigManager.getInstance().getServers());
                        pager.setVisibility(View.VISIBLE);
                        if (result == LoadConfigTaskResult.ERRORS_FOUND) {
                            Toast.makeText(MainActivity.this, getString(R.string.errors_found), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message.setText(getEmptyMessage(result));
                        empty.setVisibility(View.VISIBLE);
                    }
                    loading = false;
                }
            }.execute();
        }
    }

    private String getEmptyMessage(LoadConfigTaskResult result) {
        String message = "";
        switch (result) {
            case SUCCESS:
                message = String.format(getString(R.string.empty_config_dir), ConfigManager.getInstance().getConfigDir());
                break;
            case ERRORS_FOUND:
                message = getString(R.string.errors_found);
                break;
            case CANNOT_READ_EXT_STORAGE:
                message = getString(R.string.cannot_read_ext_storage);
                break;
            case CANNOT_CREATE_CONFIG_DIR:
                message = String.format(getString(R.string.cannot_create_config_dir), ConfigManager.getInstance().getConfigDir());
                break;
        }
        return message;
    }
}

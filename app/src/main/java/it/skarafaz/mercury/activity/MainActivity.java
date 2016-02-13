package it.skarafaz.mercury.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.ServerPagerAdapter;
import it.skarafaz.mercury.enums.LoadConfigExitStatus;
import it.skarafaz.mercury.manager.ConfigManager;

public class MainActivity extends MercuryActivity {
    public static final int LOAD_CONFIG_FILES_PREQ = 1;
    @Bind(R.id.progress)
    protected ProgressBar progress;
    @Bind(R.id.empty)
    protected LinearLayout empty;
    @Bind(R.id.message)
    protected TextView message;
    @Bind(R.id.pager)
    protected ViewPager pager;
    private ServerPagerAdapter adapter;
    private boolean loading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        adapter = new ServerPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        loadConfigFiles();
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
                loadConfigFiles();
                return true;
            case R.id.action_log:
                startActivity(new Intent(this, LogActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == LOAD_CONFIG_FILES_PREQ && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadConfigFiles();
        }
    }

    private void loadConfigFiles() {
        if (!loading) {
            new AsyncTask<Void, Void, LoadConfigExitStatus>() {
                @Override
                protected void onPreExecute() {
                    loading = true;
                    progress.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                    pager.setVisibility(View.INVISIBLE);
                }

                @Override
                protected LoadConfigExitStatus doInBackground(Void... params) {
                    return ConfigManager.getInstance().loadConfigFiles();
                }

                @Override
                protected void onPostExecute(LoadConfigExitStatus status) {
                    loading = false;
                    progress.setVisibility(View.INVISIBLE);
                    if (ConfigManager.getInstance().getServers().size() > 0) {
                        adapter.updateServers(ConfigManager.getInstance().getServers());
                        pager.setVisibility(View.VISIBLE);
                        if (status == LoadConfigExitStatus.ERRORS_FOUND) {
                            Toast.makeText(MainActivity.this, getString(R.string.errors_found), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        message.setText(String.format(getString(status.msg()), ConfigManager.getInstance().getConfigDir()));
                        empty.setVisibility(View.VISIBLE);
                        if (status == LoadConfigExitStatus.PERMISSION) {
                            manageStoragePermission();
                        }
                    }
                }
            }.execute();
        }
    }

    private void manageStoragePermission() {
        if (!MercuryApplication.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, LOAD_CONFIG_FILES_PREQ);
        } else {
            loadConfigFiles();
        }
    }
}

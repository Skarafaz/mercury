package it.skarafaz.mercury.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.ServerPagerAdapter;
import it.skarafaz.mercury.fragment.ProgressDialogFragment;
import it.skarafaz.mercury.manager.ConfigManager;
import it.skarafaz.mercury.manager.ExportPublicKeyStatus;
import it.skarafaz.mercury.manager.LoadConfigFilesStatus;
import it.skarafaz.mercury.manager.SshManager;
import it.skarafaz.mercury.ssh.SshCommandPubKey;

public class MainActivity extends MercuryActivity {
    private static final int STORAGE_PERMISSION_CONFIG_REQ = 1;
    private static final int STORAGE_PERMISSION_PUB_REQ = 2;
    private static final int APP_INFO_REQ = 1;

    @Bind(R.id.progress)
    protected ProgressBar progress;
    @Bind(R.id.empty)
    protected LinearLayout empty;
    @Bind(R.id.message)
    protected TextView message;
    @Bind(R.id.settings)
    protected TextView settings;
    @Bind(R.id.pager)
    protected ViewPager pager;

    private ServerPagerAdapter adapter;
    private MainEventSubscriber subscriber;
    private boolean busy = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        adapter = new ServerPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAppInfo();
            }
        });

        subscriber = new MainEventSubscriber(this);

        loadConfigFiles();
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(subscriber);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(subscriber);

        super.onStop();
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
            case R.id.action_export_public_key:
                exportPublicKey();
                return true;
            case R.id.action_send_public_key:
                new SshCommandPubKey().start();
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
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case STORAGE_PERMISSION_CONFIG_REQ:
                    loadConfigFiles();
                    break;
                case STORAGE_PERMISSION_PUB_REQ:
                    exportPublicKey();
                    break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == APP_INFO_REQ) {
            loadConfigFiles();
        }
    }

    private void loadConfigFiles() {
        if (!busy) {
            new AsyncTask<Void, Void, LoadConfigFilesStatus>() {
                @Override
                protected void onPreExecute() {
                    busy = true;
                    progress.setVisibility(View.VISIBLE);
                    empty.setVisibility(View.INVISIBLE);
                    pager.setVisibility(View.INVISIBLE);
                }

                @Override
                protected LoadConfigFilesStatus doInBackground(Void... params) {
                    return ConfigManager.getInstance().loadConfigFiles();
                }

                @Override
                protected void onPostExecute(LoadConfigFilesStatus status) {
                    progress.setVisibility(View.INVISIBLE);
                    if (ConfigManager.getInstance().getServers().size() > 0) {
                        adapter.updateServers(ConfigManager.getInstance().getServers());
                        pager.setVisibility(View.VISIBLE);
                        if (status == LoadConfigFilesStatus.ERROR) {
                            Toast.makeText(MainActivity.this, getString(status.message()), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        message.setText(getString(status.message(), ConfigManager.getInstance().getConfigDir()));
                        empty.setVisibility(View.VISIBLE);
                        if (status == LoadConfigFilesStatus.PERMISSION) {
                            settings.setVisibility(View.VISIBLE);
                            requestStoragePermission(STORAGE_PERMISSION_CONFIG_REQ);
                        } else {
                            settings.setVisibility(View.GONE);
                        }
                    }
                    busy = false;
                }
            }.execute();
        }
    }

    private void exportPublicKey() {
        if (!busy) {
            new AsyncTask<Void, Void, ExportPublicKeyStatus>() {
                @Override
                protected void onPreExecute() {
                    showProgressDialog(getString(R.string.exporting_public_key));
                }

                @Override
                protected ExportPublicKeyStatus doInBackground(Void... params) {
                    return SshManager.getInstance().exportPublicKey();
                }

                @Override
                protected void onPostExecute(ExportPublicKeyStatus status) {
                    dismissProgressDialog();

                    boolean toast = true;
                    if (status == ExportPublicKeyStatus.PERMISSION) {
                        toast = !requestStoragePermission(STORAGE_PERMISSION_PUB_REQ);
                    }
                    if (toast) {
                        Toast.makeText(MainActivity.this, getString(status.message(), SshManager.getInstance().getPublicKeyExportedFile()), Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        }
    }

    private boolean requestStoragePermission(int req) {
        boolean requested = false;
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, req);
            requested = true;
        }
        return requested;
    }

    private void startAppInfo() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(String.format("package:%s", getPackageName())));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivityForResult(intent, APP_INFO_REQ);
    }

    protected void showProgressDialog(String content) {
        FragmentTransaction ft = this.getSupportFragmentManager().beginTransaction();
        ft.add(ProgressDialogFragment.newInstance(content), ProgressDialogFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    protected void dismissProgressDialog() {
        FragmentManager fm = this.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = fm.findFragmentByTag(ProgressDialogFragment.TAG);
        if (frag != null) {
            ft.remove(frag);
        }
        ft.commitAllowingStateLoss();
    }
}

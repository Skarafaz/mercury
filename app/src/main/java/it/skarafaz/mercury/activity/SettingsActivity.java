/*
 * Mercury-SSH
 * Copyright (C) 2018 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.skarafaz.mercury.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.manager.ExportPublicKeyStatus;
import it.skarafaz.mercury.manager.SshManager;
import it.skarafaz.mercury.ssh.SshCommandPubKey;
import it.skarafaz.mercury.ssh.SshEventSubscriber;
import org.greenrobot.eventbus.EventBus;

public class SettingsActivity extends MercuryActivity {
    private static final String ACTION_EXPORT_PUBLIC_KEY = "it.skarafaz.mercury.EXPORT_PUBLIC_KEY";
    private static final String ACTION_SEND_PUBLIC_KEY = "it.skarafaz.mercury.SEND_PUBLIC_KEY";
    private static final int PRC_WRITE_EXT_STORAGE = 101;

    private SshEventSubscriber sshEventSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        sshEventSubscriber = new SshEventSubscriber(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_EXPORT_PUBLIC_KEY:
                    exportPublicKey();
                    break;
                case ACTION_SEND_PUBLIC_KEY:
                    new SshCommandPubKey().start();
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(sshEventSubscriber);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(sshEventSubscriber);

        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PRC_WRITE_EXT_STORAGE:
                    exportPublicKey();
                    break;
            }
        }
    }

    private void exportPublicKey() {
        new AsyncTask<Void, Void, ExportPublicKeyStatus>() {
            @Override
            protected void onPreExecute() {
                MercuryApplication.showProgressDialog(getSupportFragmentManager(), getString(R.string.exporting_public_key));
            }

            @Override
            protected ExportPublicKeyStatus doInBackground(Void... params) {
                return SshManager.getInstance().exportPublicKey();
            }

            @Override
            protected void onPostExecute(ExportPublicKeyStatus status) {
                MercuryApplication.dismissProgressDialog(getSupportFragmentManager());

                SettingsActivity activity = SettingsActivity.this;

                boolean toast = true;
                if (status == ExportPublicKeyStatus.PERMISSION) {
                    toast = !MercuryApplication.requestPermission(activity, PRC_WRITE_EXT_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                if (toast) {
                    Toast.makeText(activity, getString(status.message(), SshManager.getInstance().getPublicKeyExportedFile()), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}

/*
 * Mercury-SSH
 * Copyright (C) 2017 Skarafaz
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

package it.skarafaz.mercury.manager;

import android.Manifest;
import android.content.Context;
import android.os.Environment;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.ssh.SshCommandRegular;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class SshManager {
    private static final String SSH_DIR = "ssh";
    private static final String KNOWN_HOSTS_FILE = "known_hosts";
    private static final int PRIVATE_KEY_LENGTH = 2048;
    private static final String PRIVATE_KEY_FILE = "id_rsa";
    private static final String PUBLIC_KEY_FILE = "id_rsa.pub";
    private static final String PUBLIC_KEY_COMMENT = "mercuryssh";
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRegular.class);
    private static SshManager instance;
    private JSch jsch;

    private SshManager() {
        this.jsch = new JSch();
    }

    public static synchronized SshManager getInstance() {
        if (instance == null) {
            instance = new SshManager();
        }
        return instance;
    }

    public File getKnownHosts() throws IOException {
        File file = getKnownHostsFile();
        file.createNewFile();
        return file;
    }

    public File getPrivateKey() throws IOException, JSchException {
        File file = getPrivateKeyFile();
        if (!file.exists()) {
            generatePrivateKey(file);
        }
        return file;
    }

    public File getPublicKey() throws IOException, JSchException {
        File file = getPublicKeyFile();
        if (!file.exists()) {
            generatePublicKey(file);
        }
        return file;
    }

    public String getPublicKeyContent() throws IOException, JSchException {
        return FileUtils.readFileToString(getPublicKey()).replace("\n", "");
    }

    public File getPublicKeyExportedFile() {
        return new File(Environment.getExternalStorageDirectory(), PUBLIC_KEY_FILE);
    }

    public ExportPublicKeyStatus exportPublicKey() {
        ExportPublicKeyStatus status = ExportPublicKeyStatus.SUCCESS;
        if (MercuryApplication.isExternalStorageWritable()) {
            if (MercuryApplication.hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                try {
                    FileUtils.copyFile(getPublicKey(), getPublicKeyExportedFile());
                } catch (JSchException | IOException e) {
                    status = ExportPublicKeyStatus.ERROR;
                    logger.error(e.getMessage().replace("\n", " "));
                }
            } else {
                status = ExportPublicKeyStatus.PERMISSION;
            }
        } else {
            status = ExportPublicKeyStatus.CANNOT_WRITE_EXT_STORAGE;
        }
        return status;
    }

    private File getKnownHostsFile() {
        return new File(getSshDir(), KNOWN_HOSTS_FILE);
    }

    private File getPrivateKeyFile() {
        return new File(getSshDir(), PRIVATE_KEY_FILE);
    }

    private File getPublicKeyFile() {
        return new File(getSshDir(), PUBLIC_KEY_FILE);
    }

    private File getSshDir() {
        return MercuryApplication.getContext().getDir(SSH_DIR, Context.MODE_PRIVATE);
    }

    private void generatePrivateKey(File file) throws IOException, JSchException {
        KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, PRIVATE_KEY_LENGTH);
        kpair.writePrivateKey(file.getAbsolutePath());
        kpair.dispose();
    }

    private void generatePublicKey(File file) throws IOException, JSchException {
        KeyPair kpair = KeyPair.load(jsch, getPrivateKey().getAbsolutePath());
        kpair.writePublicKey(file.getAbsolutePath(), PUBLIC_KEY_COMMENT);
        kpair.dispose();
    }
}

package it.skarafaz.mercury.manager;

import android.content.Context;
import android.os.Environment;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.KeyPair;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.model.ServerAuthType;
import it.skarafaz.mercury.ssh.SshCommandRegular;

public class SshManager {
    private static final String SSH_DIR = "ssh";
    private static final String KNOWN_HOSTS_FILE = "known_hosts";
    private static final String PRIVATE_KEY_FILE = "id_%s";
    private static final String PUBLIC_KEY_FILE = "id_%s.pub";
    private static final String PUBLIC_KEY_COMMENT = "mercuryssh";
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRegular.class);
    private static SshManager instance;
    private JSch jsch;

    private SshManager() {
        this.jsch = new JSch();
        migrateSshKeys();
    }

    public static synchronized SshManager getInstance() {
        if (instance == null) {
            instance = new SshManager();
        }
        return instance;
    }

    private void migrateSshKeys() {
        /* Migrate old keys to new naming scheme */
        File oldPrivateKeyFile = new File(getSshDir(), "id_rsa");
        if (oldPrivateKeyFile.exists()) {
            try {
                FileUtils.moveFile(oldPrivateKeyFile, getPrivateKeyFile(ServerAuthType.RSA2048));
                logger.info(String.format("Migrated old key %s to %s", oldPrivateKeyFile,
                        getPrivateKeyFile(ServerAuthType.RSA2048)));
            } catch (IOException e) {
                logger.error(String.format("Could not migrate old key %s to %s: %s",
                        oldPrivateKeyFile, getPrivateKeyFile(ServerAuthType.RSA2048), e));
            }
        }
        File oldPublicKeyFile = new File(getSshDir(), "id_rsa.pub");
        if (oldPublicKeyFile.exists()) {
            try {
                FileUtils.moveFile(oldPublicKeyFile, getPublicKeyFile(ServerAuthType.RSA2048));
                logger.info(String.format("Migrated old key %s to %s", oldPrivateKeyFile,
                        getPublicKeyFile(ServerAuthType.RSA2048)));
            } catch (IOException e) {
                logger.error(String.format("Could not migrate old key %s to %s: %s",
                        oldPrivateKeyFile, getPublicKeyFile(ServerAuthType.RSA2048), e));
            }
        }
    }

    public File getKnownHosts() throws IOException {
        File file = getKnownHostsFile();
        file.createNewFile();
        return file;
    }

    public File getPrivateKey(ServerAuthType authType) throws IOException, JSchException {
        File file = getPrivateKeyFile(authType);
        if (!file.exists()) {
            generatePrivateKey(authType, file);
        }
        return file;
    }

    public File getPublicKey(ServerAuthType authType) throws IOException, JSchException {
        File file = getPublicKeyFile(authType);
        if (!file.exists()) {
            generatePublicKey(authType, file);
        }
        return file;
    }

    public String getPublicKeyContent(ServerAuthType authType) throws IOException, JSchException {
        return FileUtils.readFileToString(getPublicKey(authType)).replace("\n", "");
    }

    public File getPublicKeyExportedFile(ServerAuthType authType) {
        return new File(Environment.getExternalStorageDirectory(), String.format(PUBLIC_KEY_FILE,
                authType.toString().toLowerCase()));
    }

    public ExportPublicKeyStatus exportPublicKey(ServerAuthType authType) {
        ExportPublicKeyStatus status = ExportPublicKeyStatus.SUCCESS;
        if (MercuryApplication.isExternalStorageWritable()) {
            if (MercuryApplication.storagePermissionGranted()) {
                try {
                    FileUtils.copyFile(getPublicKey(authType), getPublicKeyExportedFile(authType));
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

    private File getPrivateKeyFile(ServerAuthType authType) {
        return new File(getSshDir(), String.format(PRIVATE_KEY_FILE, authType.toString()
                .toLowerCase()));
    }

    private File getPublicKeyFile(ServerAuthType authType) {
        return new File(getSshDir(), String.format(PUBLIC_KEY_FILE, authType.toString()
                .toLowerCase()));
    }

    private File getSshDir() {
        return MercuryApplication.getContext().getDir(SSH_DIR, Context.MODE_PRIVATE);
    }

    private void generatePrivateKey(ServerAuthType authType, File file) throws IOException,
            JSchException {
        KeyPair kpair = KeyPair.genKeyPair(jsch, authType.getKeyType(), authType.getKeySize());
        kpair.writePrivateKey(file.getAbsolutePath());
        kpair.dispose();
    }

    private void generatePublicKey(ServerAuthType authType, File file) throws IOException,
            JSchException {
        KeyPair kpair = KeyPair.load(jsch, getPrivateKey(authType).getAbsolutePath());
        kpair.writePublicKey(file.getAbsolutePath(), PUBLIC_KEY_COMMENT);
        kpair.dispose();
    }
}

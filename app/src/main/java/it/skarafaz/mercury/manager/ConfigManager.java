package it.skarafaz.mercury.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.enums.LoadConfigExitStatus;
import it.skarafaz.mercury.jackson.ServerMapper;
import it.skarafaz.mercury.jackson.ValidationException;
import it.skarafaz.mercury.model.Server;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private File configDir;
    private ServerMapper mapper;
    private List<Server> servers;

    private ConfigManager() {
        configDir = new File(Environment.getExternalStorageDirectory(), "Mercury-SSH");
        mapper = new ServerMapper();
        servers = new ArrayList<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public File getConfigDir() {
        return configDir;
    }

    public List<Server> getServers() {
        return servers;
    }

    public LoadConfigExitStatus loadConfigFiles() {
        servers.clear();
        LoadConfigExitStatus result = LoadConfigExitStatus.SUCCESS;
        if (isExternalStorageReadable()) {
            if (storagePermissionGranted()) {
                if (configDir.exists() && configDir.isDirectory()) {
                    for (File file : FileUtils.listFiles(configDir, new String[]{"json", "JSON"}, false)) {
                        try {
                            servers.add(mapper.readValue(file));
                        } catch (IOException | ValidationException e) {
                            result = LoadConfigExitStatus.ERRORS_FOUND;
                            logger.error(e.getMessage().replace("\n", " "));
                        }
                    }
                    Collections.sort(servers);
                } else {
                    if (!(isExternalStorageWritable() && configDir.mkdirs())) {
                        result = LoadConfigExitStatus.CANNOT_CREATE_CONFIG_DIR;
                    }
                }
            } else {
                result = LoadConfigExitStatus.PERMISSION;
            }
        } else {
            result = LoadConfigExitStatus.CANNOT_READ_EXT_STORAGE;
        }
        return result;
    }

    private boolean storagePermissionGranted() {
        Context context = MercuryApplication.getContext();
        String permission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }
}

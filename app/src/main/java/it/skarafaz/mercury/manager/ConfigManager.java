package it.skarafaz.mercury.manager;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.jackson.ServerMapper;
import it.skarafaz.mercury.jackson.ValidationException;
import it.skarafaz.mercury.model.Server;

public class ConfigManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static final String CONFIG_DIR = "Mercury-SSH";
    private static final String JSON_EXT = "json";
    private static ConfigManager instance;
    private File configDir;
    private ServerMapper mapper;
    private List<Server> servers;

    private ConfigManager() {
        configDir = new File(Environment.getExternalStorageDirectory(), CONFIG_DIR);
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

    public LoadConfigFilesStatus loadConfigFiles() {
        servers.clear();

        LoadConfigFilesStatus status = LoadConfigFilesStatus.SUCCESS;
        if (MercuryApplication.isExternalStorageReadable()) {
            if (MercuryApplication.storagePermissionGranted()) {
                if (configDir.exists() && configDir.isDirectory()) {
                    for (File file : listConfigFiles()) {
                        try {
                            servers.add(mapper.readValue(file));
                        } catch (IOException | ValidationException e) {
                            status = LoadConfigFilesStatus.ERROR;
                            logger.error(e.getMessage().replace("\n", " "));
                        }
                    }
                    Collections.sort(servers);
                } else {
                    if (!(MercuryApplication.isExternalStorageWritable() && configDir.mkdirs())) {
                        status = LoadConfigFilesStatus.CANNOT_CREATE_CONFIG_DIR;
                    }
                }
            } else {
                status = LoadConfigFilesStatus.PERMISSION;
            }
        } else {
            status = LoadConfigFilesStatus.CANNOT_READ_EXT_STORAGE;
        }
        return status;
    }

    private Collection<File> listConfigFiles() {
        return FileUtils.listFiles(configDir, new String[] { JSON_EXT, JSON_EXT.toUpperCase() }, false);
    }
}

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

import it.skarafaz.mercury.enums.LoadConfigTaskResult;
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

    public boolean createConfigDir() {
        return isExternalStorageWritable() && configDir.mkdirs();
    }

    public LoadConfigTaskResult load() {
        servers.clear();
        LoadConfigTaskResult result = LoadConfigTaskResult.SUCCESS;
        if (isExternalStorageReadable()) {
            if (configDir.exists() && configDir.isDirectory()) {
                Collection<File> files = listConfigFiles();
                for (File file : files) {
                    try {
                        servers.add(mapper.readValue(file));
                    } catch (IOException | ValidationException e) {
                        result = LoadConfigTaskResult.ERRORS_FOUND;
                        logger.error(e.getMessage().replace("\n", " "));
                    }
                }
                Collections.sort(servers);
            } else {
                if (!createConfigDir()) {
                    result = LoadConfigTaskResult.CANNOT_CREATE_CONFIG_DIR;
                }
            }
        } else {
            result = LoadConfigTaskResult.CANNOT_READ_EXT_STORAGE;
        }
        return result;
    }

    private Collection<File> listConfigFiles() {
        return FileUtils.listFiles(configDir, new String[] { "json", "JSON" }, false);
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

package it.skarafaz.mercury.manager;

import android.os.Environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.LoadConfigTaskResult;
import it.skarafaz.mercury.data.Server;
import it.skarafaz.mercury.exception.ValidationException;
import it.skarafaz.mercury.jackson.ServerMapper;

public class ConfigManager {
    public static final int SLEEP = 500;
    public static final String JSON_EXT = ".json";
    private static final Logger logger = LoggerFactory.getLogger(ConfigManager.class);
    private static ConfigManager instance;
    private ServerMapper mapper;
    private List<Server> servers;

    private ConfigManager() {
        mapper = new ServerMapper();
        servers = new ArrayList<>();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public List<Server> getServers() {
        return servers;
    }

    public LoadConfigTaskResult load() {
        sleep();
        servers.clear();
        LoadConfigTaskResult result = LoadConfigTaskResult.SUCCESS;
        if (isExternalStorageReadable()) {
            File configDir = getConfigDir();
            if (configDir.exists() && configDir.isDirectory()) {
                File[] files = getConfigFiles(configDir);
                for (File file : files) {
                    try {
                        servers.add(mapper.readValue(file));
                    } catch (IOException | ValidationException e) {
                        result = LoadConfigTaskResult.ERRORS_FOUND;
                        logger.error(e.getMessage());
                    }
                }
            } else {
                if (!createConfigDir(configDir)) {
                    result = LoadConfigTaskResult.CANNOT_CREATE_CONFIG_DIR;
                }
            }
        } else {
            result = LoadConfigTaskResult.CANNOT_READ_EXT_STORAGE;
        }
        return result;
    }

    public File getConfigDir() {
        String appName = MercuryApplication.getContext().getResources().getString(R.string.app_name);
        return new File(Environment.getExternalStorageDirectory(), appName);
    }

    private boolean createConfigDir(File configDir) {
        return isExternalStorageWritable() && configDir.mkdirs();
    }

    private File[] getConfigFiles(File configDir) {
        File[] files = configDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.toLowerCase().endsWith(JSON_EXT);
            }
        });
        Arrays.sort(files);
        return files;
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

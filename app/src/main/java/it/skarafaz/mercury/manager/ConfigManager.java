package it.skarafaz.mercury.manager;

import android.os.Environment;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Server;
import it.skarafaz.mercury.enums.LoadConfigTaskResult;
import it.skarafaz.mercury.exception.ValidationException;
import it.skarafaz.mercury.jackson.ServerMapper;

public class ConfigManager {
    public static final String JSON = "json";
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

    public LoadConfigTaskResult load() {
        servers.clear();
        LoadConfigTaskResult result = LoadConfigTaskResult.SUCCESS;
        if (isExternalStorageReadable()) {
            File configDir = getConfigDir();
            if (configDir.exists() && configDir.isDirectory()) {
                Collection<File> files = getConfigFiles(configDir);
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
                if (!createConfigDir(configDir)) {
                    result = LoadConfigTaskResult.CANNOT_CREATE_CONFIG_DIR;
                }
            }
        } else {
            result = LoadConfigTaskResult.CANNOT_READ_EXT_STORAGE;
        }
        return result;
    }

    public List<Server> getServers() {
        return servers;
    }

    public File getConfigDir() {
        String appName = MercuryApplication.getContext().getString(R.string.app_name);
        return new File(Environment.getExternalStorageDirectory(), appName);
    }

    private boolean createConfigDir(File configDir) {
        return isExternalStorageWritable() && configDir.mkdirs();
    }

    private Collection<File> getConfigFiles(File configDir) {
        SuffixFileFilter filter = new SuffixFileFilter(new String[]{JSON}, IOCase.INSENSITIVE);
        return FileUtils.listFiles(configDir, filter, null);
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

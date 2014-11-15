package it.skarafaz.mercury.manager;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.data.Server;

public class ConfigManager {
    private static ConfigManager instance;
    private List<Server> servers;

    private ConfigManager() {
        servers = new ArrayList<Server>();
    }

    public static synchronized ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public void init() {
        servers.clear();
        List<String> files = getConfigFiles();
        try {
            for (String file : files) {
                servers.add(MercuryApplication.getObjectMapper().readValue(file, Server.class));
            }
        } catch (IOException e) {
            Log.e(ConfigManager.class.getSimpleName(), e.getMessage());
        }
    }

    private List<String> getConfigFiles() {
        List<String> jsonFiles = new ArrayList<String>();
        if (isExternalStorageReadable()) {
            File configDir = getConfigDir();
            if (configDir.exists() && configDir.isDirectory()) {
                File[] files = configDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) {
                        return filename.toLowerCase().endsWith("*.json");
                    }
                });
                for (File file : files) {
                    try {
                        InputStream is = FileUtils.openInputStream(file);
                        jsonFiles.add(IOUtils.toString(is, "UTF-8"));
                        is.close();
                    } catch (IOException e) {
                        Log.e(ConfigManager.class.getSimpleName(), e.getMessage());
                    }
                }
            } else {
                Log.d(ConfigManager.class.getSimpleName(), "bad config dir");
                //FIXME Toaster
                //Toast.makeText(MercuryApplication.getContext(), R.string.bad_config_dir, Toast.LENGTH_LONG).show();
            }
        } else {
            Log.d(ConfigManager.class.getSimpleName(), "cannot read external");
            //FIXME Toaster
            //Toast.makeText(MercuryApplication.getContext(), R.string.external_read, Toast.LENGTH_LONG).show();
        }
        return jsonFiles;
    }

    private File getConfigDir() {
        return new File(Environment.getExternalStorageDirectory(), PreferencesManager.getInstance().getConfigDir());
    }

    private boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private List<String> getSampleConfigFiles() {
        List<String> files = new ArrayList<String>();
        AssetManager assetManager = MercuryApplication.getContext().getAssets();
        try {
            String[] assets = assetManager.list("config");
            for (String asset : assets) {
                InputStream inputStream = assetManager.open("config/" + asset);
                files.add(IOUtils.toString(inputStream, "UTF-8"));
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(ConfigManager.class.getSimpleName(), e.getMessage());
        }
        return files;
    }

    public List<Server> getServers() {
        return servers;
    }

    public boolean createConfigDir() {
        boolean success = true;
        File configDir = getConfigDir();
        if (!configDir.exists()) {
            success = configDir.mkdirs();
        }
        return success;
    }
}

package it.skarafaz.mercury.manager;

import android.content.res.AssetManager;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.data.Server;

/**
 * Created by Skarafaz on 14/09/2014.
 */
public class ServerManager {
    private static ServerManager instance;
    private List<Server> servers;

    private ServerManager() {
    }

    public static synchronized ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
        }
        return instance;
    }

    public void init() {
        List<String> files = getSampleConfigFiles();
        ObjectMapper mapper = new ObjectMapper();
        try {
            for (String file: files) {
                servers.add(mapper.readValue(file, Server.class));
            }
        } catch (IOException e) {
            Log.e(ServerManager.class.getSimpleName(), e.getMessage());
        }
    }

    private List<String> getSampleConfigFiles() {
        List<String> files = new ArrayList<String>();
        AssetManager assetManager = MercuryApplication.getContext().getAssets();
        try {
            String[] assets = assetManager.list("");
            for (String asset: assets) {
                InputStream inputStream = assetManager.open(asset);
                files.add(IOUtils.toString(inputStream, "UTF-8"));
                inputStream.close();
            }
        } catch (IOException e) {
            Log.e(ServerManager.class.getSimpleName(), e.getMessage());
        }
        return files;
    }

    public List<Server> getServers() {
        return servers;
    }
}

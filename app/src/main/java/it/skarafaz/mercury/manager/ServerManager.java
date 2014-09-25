package it.skarafaz.mercury.manager;

import java.util.List;

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
    }

    public List<Server> getServers() {
        return servers;
    }
}

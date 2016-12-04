package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import it.skarafaz.mercury.manager.SshManager;
import it.skarafaz.mercury.model.Server;

public class SshServer extends Thread {
    protected static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshServer.class);

    protected JSch jsch;
    protected Session session;

    protected String host;
    protected Integer port;
    protected String user;
    protected String password;
    protected String sudoPath;
    protected String nohupPath;


    public SshServer() {
        this.jsch = new JSch();
    }

    public SshServer(Server server) {
        this();

        host = server.getHost();
        port = server.getPort();
        user = server.getUser();
        password = server.getPassword();
        sudoPath = server.getSudoPath();
        nohupPath = server.getNohupPath();
    }

    synchronized public SshCommandStatus connect() {
        if (!isConnected()) {
            if (!initConnection()) {
                return SshCommandStatus.CONNECTION_INIT_ERROR;
            }
            if (!getSession()) {
                return SshCommandStatus.CONNECTION_FAILED;
            }
        }
        return SshCommandStatus.COMMAND_SUCCESSFUL;
    }

    public void disconnect() {
        logger.debug(String.format("Disconnecting from server %s", formatServerLabel()));
        if (isConnected()) {
            session.disconnect();
        }
    }

    protected boolean isConnected() {
        return session != null && session.isConnected();
    }

    protected boolean initConnection() {
        boolean success = true;
        try {
            jsch.setKnownHosts(SshManager.getInstance().getKnownHosts().getAbsolutePath());
            jsch.addIdentity(SshManager.getInstance().getPrivateKey().getAbsolutePath());
            jsch.setLogger(new com.jcraft.jsch.Logger() {
                @Override
                public boolean isEnabled(int level) {
                    return true;
                }

                @Override
                public void log(int level, String message) {
                    message = String.format("JSch: %s", message);
                    switch (level) {
                        case com.jcraft.jsch.Logger.FATAL:
                            logger.error(message);
                            break;
                        case com.jcraft.jsch.Logger.ERROR:
                            logger.warn(message);
                            break;
                        case com.jcraft.jsch.Logger.WARN:
                            logger.info(message);
                            break;
                        case com.jcraft.jsch.Logger.INFO:
                            logger.debug(message);
                            break;
                        case com.jcraft.jsch.Logger.DEBUG:
                            logger.trace(message);
                            break;
                    }
                }
            });
        } catch (IOException | JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    protected boolean getSession() {
        boolean success = true;
        try {
            logger.debug(String.format("Connecting to server %s", formatServerLabel()));
            session = jsch.getSession(user, host, port);

            session.setUserInfo(getUserInfo());
            session.setConfig(getSessionConfig());
            session.setPassword(password);

            session.connect(TIMEOUT);
        } catch (JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    protected UserInfo getUserInfo() {
        return new SshCommandUserInfo();
    }

    protected Properties getSessionConfig() {
        Properties config = new Properties();
        config.put("PreferredAuthentications", "publickey,password");
        config.put("MaxAuthTries", "1");
        return config;
    }

    public String formatServerLabel() {
        StringBuilder sb = new StringBuilder(String.format("%s@%s", user, host));
        if (port != 22) {
            sb.append(String.format(":%d", port));
        }
        return sb.toString();
    }
}

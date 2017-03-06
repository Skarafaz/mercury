package it.skarafaz.mercury.ssh;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiManager;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.Security;
import java.util.Properties;

import it.skarafaz.mercury.manager.SshManager;
import it.skarafaz.mercury.model.Server;
import it.skarafaz.mercury.model.ServerAuthType;

public class SshServer extends Thread {
    protected static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshServer.class);
    protected static final int NOT_RESOLVED = 0;
    protected static final int RESOLVING = 1;
    protected static final int RESOLVED = 2;
    protected static final int RESOLVING_FAILED = 3;

    protected JSch jsch;
    protected Session session;
    protected WifiManager wifiManager;
    protected WifiManager.MulticastLock multicastLock;
    protected NsdManager nsdManager;
    protected NsdServiceInfo serviceInfo;
    protected NsdManager.ResolveListener resolveListener;
    protected int resolving;
    protected final Object lock;

    protected String host;
    protected Integer port;
    protected String mDnsName;
    protected String mDnsType;
    protected String user;
    protected String password;
    protected ServerAuthType authType;
    protected String sudoPath;
    protected String nohupPath;

    static {
        logger.debug("Configure spongy castle security");
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }

    public SshServer() {
        this.jsch = new JSch();
        resolving = NOT_RESOLVED;
        lock = new Object();
    }

    public SshServer(Server server, Context context) {
        this();

        host = server.getHost();
        port = server.getPort();
        mDnsName = server.getMDnsName();
        mDnsType = server.getMDnsType();
        user = server.getUser();
        password = server.getPassword();
        authType =  ServerAuthType.valueOf(ServerAuthType.appendDefaultLength(server.getAuthType()));
        sudoPath = server.getSudoPath();
        nohupPath = server.getNohupPath();

        if (mDnsName != null && mDnsType != null) {
            wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifiManager.createMulticastLock(getClass().getName());
            nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
            serviceInfo = new NsdServiceInfo();
            serviceInfo.setServiceType(mDnsType);
            serviceInfo.setServiceName(mDnsName);
            resolveListener = new NsdManager.ResolveListener() {
                @Override
                public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    logger.debug(String.format("mDNS resolve failed for service name %s (type " +
                            "%s) : %d", serviceInfo.getServiceName(), serviceInfo.getServiceType(),
                            errorCode));
                    synchronized (lock) {
                        if (resolving == RESOLVING) {
                            resolving = RESOLVING_FAILED;
                            lock.notifyAll();
                        }
                    }
                }

                @Override
                public void onServiceResolved(NsdServiceInfo serviceInfo) {
                    logger.debug("mDNS resolve succeeded: " + serviceInfo);
                    host = serviceInfo.getHost().getHostAddress();
                    port = serviceInfo.getPort();
                    synchronized (lock) {
                        resolving = RESOLVED;
                        lock.notifyAll();
                    }
                }
            };
        }
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
            if (mDnsName != null && (resolving == NOT_RESOLVED || resolving == RESOLVING_FAILED)) {
                logger.debug("mDNS resolve: " + serviceInfo);
                resolving = RESOLVING;
                multicastLock.acquire();
                nsdManager.resolveService(serviceInfo, resolveListener);
                /* Wait TIMEOUT milliseconds, otherwise try host/port (classic dns) if specified */
                synchronized (lock) {
                    long time = System.currentTimeMillis() + TIMEOUT;
                    while (resolving == RESOLVING && time > System.currentTimeMillis()) {
                        try {
                            lock.wait(time - System.currentTimeMillis());
                        } catch (InterruptedException ee) {
                            logger.debug(ee.getMessage());
                        }
                    }
                    logger.debug(String.format("mDNS resolve: cleanup after %d ms", System
                            .currentTimeMillis() - time + TIMEOUT));
                }
                multicastLock.release();
            }
            if (host == null || port == null) {
                logger.error(String.format("Resolving service name % (type %s) with mDNS " +
                        "failed. No host/port specified.", serviceInfo.getServiceName(),
                        serviceInfo.getServiceType()));
                return false;
            }
            jsch.setKnownHosts(SshManager.getInstance().getKnownHosts().getAbsolutePath());
            jsch.addIdentity(SshManager.getInstance().getPrivateKey(authType).getAbsolutePath());
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
        } catch (IOException | JSchException | RuntimeException e) {
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
        } catch (JSchException | RuntimeException e) {
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

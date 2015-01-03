package it.skarafaz.mercury.manager;

import android.util.Log;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import it.skarafaz.mercury.data.Command;

public class SSHManager {
    private static final int TIMEOUT = 10000;
    private JSch jsch;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private Session session;
    private String command;


    public SSHManager(Command command) {
        jsch = new JSch();
        host = command.getServer().getHost();
        port = command.getServer().getPort();
        user = command.getServer().getUser();
        password = command.getServer().getPassword();
        this.command = command.getCmd();
    }

    public boolean connect() {
        boolean success = true;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no"); // TODO known_hosts mng
            session.connect(TIMEOUT);
        } catch (JSchException e) {
            Log.e(SSHManager.class.getSimpleName(), e.getMessage());
            success = false;
        }
        return success;
    }

    public boolean sendCommand() {
        boolean success = true;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.setOutputStream(System.out);
            channel.setErrStream(System.err);
            channel.connect(TIMEOUT);
            channel.disconnect();
        } catch (JSchException e) {
            Log.e(SSHManager.class.getSimpleName(), e.getMessage());
            success = false;
        }
        return success;
    }

    public void disconnect() {
        session.disconnect();
    }
}

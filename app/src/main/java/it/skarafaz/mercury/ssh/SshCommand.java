package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.model.Command;

public class SshCommand {
    private static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshCommand.class);
    private JSch jsch;
    private Session session;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String command;
    private Boolean sudo;


    public SshCommand(Command command) {
        jsch = new JSch();
        host = command.getServer().getHost();
        port = command.getServer().getPort();
        user = command.getServer().getUser();
        password = command.getServer().getPassword();
        this.command = command.getCmd();
        sudo = command.getSudo();
    }

    public boolean connect() {
        boolean success = true;
        try {
            session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(TIMEOUT);
        } catch (JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    public boolean send() {
        boolean success = true;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(prepareCommand());
            channel.connect(TIMEOUT);
            channel.disconnect();
        } catch (JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    private String prepareCommand() {
        String toReturn = null;
        if (sudo) {
            toReturn =  String.format("echo %s | sudo -S -p '' nohup %s > /dev/null 2>&1", password, command);
        } else {
            toReturn = String.format("nohup %s > /dev/null 2>&1", command);
        }
        logger.debug(toReturn);
        return toReturn;
    }

    public void disconnect() {
        session.disconnect();
    }
}

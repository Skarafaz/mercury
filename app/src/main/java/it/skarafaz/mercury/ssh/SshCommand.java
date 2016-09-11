package it.skarafaz.mercury.ssh;

import android.content.Context;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SshCommandConfirm;
import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.event.SshCommandStart;
import it.skarafaz.mercury.model.Command;

public class SshCommand extends Thread {
    private static final int TIMEOUT = 10000;
    private static final String SSH_DIR = "ssh";
    private static final String KNOWN_HOSTS_FILE = "known_hosts";
    private static final Logger logger = LoggerFactory.getLogger(SshCommand.class);
    private JSch jsch;
    private Session session;
    private String host;
    private Integer port;
    private String user;
    private String password;
    private String sudoPath;
    private String nohupPath;
    private Boolean sudo;
    private String cmd;
    private Boolean confirm;

    public SshCommand(Command command) {
        this.jsch = new JSch();
        this.host = command.getServer().getHost();
        this.port = command.getServer().getPort();
        this.user = command.getServer().getUser();
        this.password = command.getServer().getPassword();
        this.sudoPath = command.getServer().getSudoPath();
        this.nohupPath = command.getServer().getNohupPath();
        this.sudo = command.getSudo();
        this.cmd = command.getCmd();
        this.confirm = command.getConfirm();
    }

    @Override
    public void run() {
        if (confirm) {
            SshCommandDrop<Boolean> drop = new SshCommandDrop<>();
            EventBus.getDefault().postSticky(new SshCommandConfirm(cmd, drop));

            if (!drop.take()) {
                return;
            }
        }

        if (sudo && password == null) {
            SshCommandDrop<String> drop = new SshCommandDrop<>();
            String message = MercuryApplication.getContext().getString(R.string.type_sudo_password, prepareServerLabel());
            EventBus.getDefault().postSticky(new SshCommandPassword(message, drop));

            password = drop.take();
            if (password == null) {
                return;
            }
        }

        execute();
    }

    private void execute() {
        EventBus.getDefault().postSticky(new SshCommandStart());

        SshCommandStatus status = SshCommandStatus.COMMAND_SENT;
        if (connect()) {
            if (!send(prepareCmd())) {
                status = SshCommandStatus.CONNECTION_FAILED;
            }
            disconnect();
        } else {
            status = SshCommandStatus.CONNECTION_FAILED;
        }

        EventBus.getDefault().postSticky(new SshCommandEnd(status));
    }

    private boolean connect() {
        boolean success = true;
        try {
            jsch.setKnownHosts(getKnownHostsFile().getAbsolutePath());
            session = jsch.getSession(user, host, port);
            session.setConfig("PreferredAuthentications", "password");
            session.setConfig("MaxAuthTries", "1");
            session.setUserInfo(new SshCommandUserInfo());
            session.setPassword(password);
            session.connect(TIMEOUT);
        } catch (JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    private boolean send(String cmd) {
        logger.debug("sending commnad: {}", cmd);

        boolean success = true;
        try {
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            channel.connect(TIMEOUT);
            channel.disconnect();
        } catch (JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    private void disconnect() {
        session.disconnect();
    }

    private String prepareCmd() {
        if (sudo) {
            return String.format("echo %s | %s -S -p '' %s %s > /dev/null 2>&1", password, sudoPath, nohupPath, cmd);
        } else {
            return String.format("%s %s > /dev/null 2>&1", nohupPath, cmd);
        }
    }

    private String prepareServerLabel() {
        StringBuilder sb = new StringBuilder(String.format("%s@%s", user, host));
        if (port != 22) {
            sb.append(String.format(":%d", port));
        }
        return sb.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getKnownHostsFile() {
        File file = new File(getSshDir(), KNOWN_HOSTS_FILE);
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.error(e.getMessage().replace("\n", " "));
        }
        return file;
    }

    private File getSshDir() {
        return MercuryApplication.getContext().getDir(SSH_DIR, Context.MODE_PRIVATE);
    }
}

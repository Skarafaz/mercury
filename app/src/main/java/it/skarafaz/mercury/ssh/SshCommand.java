package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandStart;
import it.skarafaz.mercury.model.Command;

public abstract class SshCommand extends Thread {
    protected static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshCommand.class);
    protected SshServer server;
    protected Command command;
    protected Boolean sudo;
    protected String cmd;
    protected Boolean confirm;
    protected Boolean wait;
    protected Boolean background;
    protected Boolean multiple;
    protected Boolean silent;
    protected String output;
    protected String error;

    public SshCommand(SshServer server, Command command) {
        this.server = server;
        this.command = command;
    }

    @Override
    public void run() {
        output = error = null;
        if(beforeExecute()) {
            SshCommandStatus status = execute();
            afterExecute(status);
        }
    }

    protected boolean beforeExecute() {
        command.increaseRunning();
        EventBus.getDefault().postSticky(new SshCommandStart(background));
        return true;
    }

    private SshCommandStatus execute() {
        SshCommandStatus status = server.connect();
        if (status != SshCommandStatus.COMMAND_SUCCESSFUL) {
            return status;
        }
        return send(formatCmd(cmd));
    }

    protected void afterExecute(SshCommandStatus status) {
        command.decreaseRunning();
        EventBus.getDefault().postSticky(new SshCommandEnd(background, silent, status));
    }

    protected SshCommandStatus send(String cmd) {
        logger.debug(String.format("[%d] sending command: %s", getId(), cmd));

        ChannelExec channel = null;

        try {
            channel = (ChannelExec) server.session.openChannel("exec");
            channel.setCommand(cmd);
            channel.setInputStream(null);

            InputStream stdout = channel.getInputStream();
            InputStream stderr = channel.getErrStream();

            channel.connect(TIMEOUT);
            return waitForChannelClosed(channel, stdout, stderr);
        } catch (IOException | JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            return SshCommandStatus.COMMUNICATION_ERROR;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    protected SshCommandStatus waitForChannelClosed(ChannelExec channel, InputStream stdout,
                                                    InputStream stderr) throws IOException {
        if (!wait) return SshCommandStatus.COMMAND_SENT;
        long start = System.currentTimeMillis();
        boolean success = false;
        byte buf[] = new byte[1024];
        int len;
        StringBuilder sbout = new StringBuilder();
        StringBuilder sberr = new StringBuilder();

        while (true) {
            if (stdout.available() > 0) {
                len = stdout.read(buf, 0, 1024);
                if (len < 0) throw new IOException("stdout");
                sbout.append(new String(buf, 0, len, "UTF-8"));
                logger.trace(String.format("[%d] stdout available = %d / read %d",
                        getId(), stdout.available(), sbout.length()));
            }
            if (stderr.available() > 0) {
                len = stderr.read(buf, 0, 1024);
                if (len < 0) throw new IOException("stderr");
                sberr.append(new String(buf, 0, len, "UTF-8"));
                logger.trace(String.format("[%d] stderr available = %d / read %d",
                        getId(), stderr.available(), sberr.length()));
            }
            if (channel.isClosed()) {
                if (stdout.available() > 0 || stderr.available() > 0) continue;
                break;
            }
            if (System.currentTimeMillis() - start > TIMEOUT) {
                logger.error(String.format("Timeout after %d milliseconds", TIMEOUT));
                return SshCommandStatus.COMMAND_TIMEOUT;
            }
            try {
                Thread.sleep(100);
            } catch (Exception ee) {
                logger.debug(ee.getMessage().replace("\n", " "));
            }
        }
        output = sbout.toString();
        error = sberr.toString();
        logger.debug(String.format("[%d] exitcode = %d", getId(), channel.getExitStatus()));
        logger.debug(String.format("[%d] stdout = %s", getId(), output.replace("\n", " ")));
        logger.debug(String.format("[%d] stderr = %s", getId(), error.replace("\n", " ")));
        if (channel.getExitStatus() == 0) {
            return SshCommandStatus.COMMAND_SUCCESSFUL;
        } else {
            return SshCommandStatus.EXECUTION_FAILED;
        }
    }

    protected String formatCmd(String cmd) {
        return cmd;
    }
}

/*
 * Mercury-SSH
 * Copyright (C) 2017 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SshCommandConfirm;
import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.event.SshCommandStart;
import it.skarafaz.mercury.model.Command;

public abstract class SshCommand extends Thread {
    protected static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshCommand.class);
    protected SshServer server;
    protected Command command;
    protected Boolean sudo;
    protected String cmd;
    protected HashMap<String, String> env;
    protected Boolean confirm;
    protected Boolean wait;
    protected Boolean background;
    protected Boolean silent;
    protected String output;
    protected String error;

    public SshCommand(SshServer server, Command command) {
        this.server = server;
        this.command = command;
        this.env = new HashMap<String, String>();
    }

    public void setEnv(String variable, String value) {
        env.put(variable, value);
    }

    @Override
    public void run() {
        output = error = null;
        if (beforeExecute()) {
            SshCommandStatus status = execute();
            afterExecute(status);
        }
    }

    protected boolean beforeExecute() {
        if (command != null) {
            command.increaseRunning();
        }

        if (confirm) {
            SshCommandDrop<Boolean> drop = new SshCommandDrop<>();
            EventBus.getDefault().postSticky(new SshCommandConfirm(cmd, drop));

            if (!drop.take()) {
                return false;
            }
        }

        if (sudo && server.password == null) {
            SshCommandDrop<String> drop = new SshCommandDrop<>();
            String message = MercuryApplication.getContext().getString(R.string.type_sudo_password,
                    server.formatServerLabel());
            EventBus.getDefault().postSticky(new SshCommandPassword(message, drop));

            server.password = drop.take();
            if (server.password == null) {
                return false;
            }
        }

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
        if (command != null) {
            command.decreaseRunning();
        }
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
        } catch (IOException | JSchException | RuntimeException e) {
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
                logger.trace(String.format("[%d] stdout available = %d / read %d", getId(), stdout.available(),
                        sbout.length()));
            }
            if (stderr.available() > 0) {
                len = stderr.read(buf, 0, 1024);
                if (len < 0) throw new IOException("stderr");
                sberr.append(new String(buf, 0, len, "UTF-8"));
                logger.trace(String.format("[%d] stderr available = %d / read %d", getId(), stderr.available(),
                        sberr.length()));
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
        /* Can't use setEnv because it is prabably blocked by server (see AcceptEnv) */
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, String> entry : env.entrySet()) {
            sb.append(String.format("export %s='%s';", entry.getKey(), entry.getValue().replaceAll("'", "'\"'\"'")));
        }
        if (sudo) {
            sb.append(String.format("echo '%s' | '%s' -S -p '' ",server.password.replaceAll("'", "'\"'\"'"),
                    server.sudoPath.replaceAll("'", "'\"'\"'")));
        }
        sb.append(String.format("'%s' \"${SHELL}\" -c '%s'", server.nohupPath.replaceAll("'", "'\"'\"'"),
                cmd.replaceAll("'", "'\"'\"'")));
        return sb.toString();
    }
}

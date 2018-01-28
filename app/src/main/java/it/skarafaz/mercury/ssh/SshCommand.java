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

import com.jcraft.jsch.*;
import it.skarafaz.mercury.event.SshCommandEnd;
import it.skarafaz.mercury.event.SshCommandStart;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class SshCommand extends Thread {
    protected static final int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(SshCommand.class);
    protected JSch jsch;
    protected Session session;
    protected String host;
    protected Integer port;
    protected String user;
    protected String password;
    protected String sudoPath;
    protected String nohupPath;
    protected Boolean sudo;
    protected String cmd;
    protected Boolean confirm;

    public SshCommand() {
        this.jsch = new JSch();
    }

    @Override
    public void run() {
        if(beforeExecute()) {
            SshCommandStatus status = execute();
            afterExecute(status);
        }
    }

    protected boolean beforeExecute() {
        EventBus.getDefault().postSticky(new SshCommandStart());
        return true;
    }

    private SshCommandStatus execute() {
        SshCommandStatus status = SshCommandStatus.COMMAND_SENT;

        if (initConnection()) {
            if (connect()) {
                if (!send(formatCmd(cmd))) {
                    status = SshCommandStatus.EXECUTION_FAILED;
                }
                disconnect();
            } else {
                status = SshCommandStatus.CONNECTION_FAILED;
            }
        } else {
            status = SshCommandStatus.CONNECTION_INIT_ERROR;
        }

        return status;
    }

    protected void afterExecute(SshCommandStatus status) {
        EventBus.getDefault().postSticky(new SshCommandEnd(status));
    }

    protected boolean initConnection() {
        return true;
    }

    protected boolean connect() {
        boolean success = true;
        try {
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

    protected boolean send(String cmd) {
        logger.debug("sending command: {}", cmd);

        ChannelExec channel = null;

        boolean success = true;
        try {
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            channel.setInputStream(null);

            InputStream stdout = channel.getInputStream();
            InputStream stderr = channel.getErrStream();

            channel.connect(TIMEOUT);
            success = waitForChannelClosed(channel, stdout, stderr);
        } catch (IOException | JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
        return success;
    }

    protected boolean waitForChannelClosed(ChannelExec channel, InputStream stdout, InputStream stderr) {
        return true;
    }

    protected void disconnect() {
        session.disconnect();
    }

    protected UserInfo getUserInfo() {
        return null;
    }

    protected Properties getSessionConfig() {
        return new Properties();
    }

    protected String formatCmd(String cmd) {
        return cmd;
    }
}

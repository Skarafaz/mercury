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

import com.jcraft.jsch.JSchException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import it.skarafaz.mercury.manager.SshManager;

public class SshCommandPubKey extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandPubKey.class);
    private String pubKey;

    public SshCommandPubKey(SshServer server) {
        super(server, null);
        sudo = false;
        confirm = false;
        wait = true;
        background = false;
        silent = false;
    }

    @Override
    protected boolean beforeExecute() {
        try {
            pubKey = SshManager.getInstance().getPublicKeyContent(server.authType);
        } catch (IOException | JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            return false;
        }
        return super.beforeExecute();
    }

    @Override
    protected String formatCmd(String cmd) {
        StringBuilder sb = new StringBuilder();
        sb.append("mkdir -p ~/.ssh && ");
        sb.append(String.format("echo \"%s\" >> ~/.ssh/authorized_keys && ", pubKey));
        sb.append("chmod 700 ~/.ssh && ");
        sb.append("chmod 600 ~/.ssh/authorized_keys");
        return sb.toString();
    }
}

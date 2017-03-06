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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.model.RegularCommand;

public class SshCommandRegular extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRegular.class);

    private String download;
    private Boolean view;

    public SshCommandRegular(SshServer server, RegularCommand command) {
        super(server, command);

        this.sudo = command.getSudo();
        this.cmd = command.getCmd();
        this.download = command.getDownload();
        this.confirm = command.getConfirm();
        this.wait = command.getWait();
        this.background = command.getBackground();
        this.silent = command.getSilent();
        this.view = command.getView();
    }

    @Override
    protected void afterExecute(SshCommandStatus status) {
        super.afterExecute(status);
        if (download != null && (status == SshCommandStatus.COMMAND_SENT || status == SshCommandStatus
                .COMMAND_SUCCESSFUL)) {
            (new SftpDownload(server, (RegularCommand) command)).start();
        }
    }
}

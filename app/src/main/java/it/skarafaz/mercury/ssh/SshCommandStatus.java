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

import it.skarafaz.mercury.R;

public enum SshCommandStatus {
    CONNECTION_INIT_ERROR(R.string.connection_init_error),
    CONNECTION_FAILED(R.string.connection_failed),
    EXECUTION_FAILED(R.string.execution_failed),
    COMMUNICATION_ERROR(R.string.communication_error),
    COMMAND_TIMEOUT(R.string.command_timeout),
    COMMAND_SENT(R.string.command_sent),
    COMMAND_SUCCESSFUL(R.string.command_successful);

    private int message;
    private String output;

    SshCommandStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}

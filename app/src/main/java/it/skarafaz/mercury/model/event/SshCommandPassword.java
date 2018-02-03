/*
 * Mercury-SSH
 * Copyright (C) 2018 Skarafaz
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

package it.skarafaz.mercury.model.event;

import it.skarafaz.mercury.ssh.SshCommandDrop;

public class SshCommandPassword {
    private String message;
    private SshCommandDrop<String> drop;

    public SshCommandPassword(String message, SshCommandDrop<String> drop) {
        this.message = message;
        this.drop = drop;
    }

    public SshCommandDrop<String> getDrop() {
        return drop;
    }

    public String getMessage() {
        return message;
    }
}

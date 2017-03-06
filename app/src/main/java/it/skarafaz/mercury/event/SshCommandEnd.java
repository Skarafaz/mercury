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

package it.skarafaz.mercury.event;

import it.skarafaz.mercury.ssh.SshCommandStatus;

public class SshCommandEnd {
    private Boolean background;
    private Boolean silent;
    private SshCommandStatus status;

    public SshCommandEnd(Boolean background, Boolean silent, SshCommandStatus status) {
        this.background = background;
        this.silent = silent;
        this.status = status;
    }

    public Boolean getBackground() {
        return background;
    }

    public Boolean getSilent() {
        return silent;
    }

    public SshCommandStatus getStatus() {
        return status;
    }
}

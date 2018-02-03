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

import com.jcraft.jsch.UserInfo;
import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.model.event.SshCommandMessage;
import it.skarafaz.mercury.model.event.SshCommandPassword;
import it.skarafaz.mercury.model.event.SshCommandYesNo;
import org.greenrobot.eventbus.EventBus;

public class SshCommandUserInfo implements UserInfo {
    private String password;

    @Override
    public String getPassphrase() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean promptPassword(String message) {
        SshCommandDrop<String> drop = new SshCommandDrop<>();
        message = String.format(MercuryApplication.getContext().getString(R.string.type_login_password), message.toLowerCase());
        EventBus.getDefault().postSticky(new SshCommandPassword(message, drop));
        password = drop.take();
        return password != null;
    }

    @Override
    public boolean promptPassphrase(String message) {
        return false;
    }

    @Override
    public boolean promptYesNo(String message) {
        SshCommandDrop<Boolean> drop = new SshCommandDrop<>();
        EventBus.getDefault().postSticky(new SshCommandYesNo(message, drop));
        return drop.take();
    }

    @Override
    public void showMessage(String message) {
        SshCommandDrop<Boolean> drop = new SshCommandDrop<>();
        EventBus.getDefault().postSticky(new SshCommandMessage(message, drop));
        drop.take();
    }
}

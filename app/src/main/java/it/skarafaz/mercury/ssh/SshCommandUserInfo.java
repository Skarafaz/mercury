package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.UserInfo;

import org.greenrobot.eventbus.EventBus;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SshCommandMessage;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.event.SshCommandYesNo;

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

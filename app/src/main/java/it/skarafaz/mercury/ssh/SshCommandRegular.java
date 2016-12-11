package it.skarafaz.mercury.ssh;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.event.SshCommandConfirm;
import it.skarafaz.mercury.event.SshCommandPassword;
import it.skarafaz.mercury.model.Command;

public class SshCommandRegular extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRegular.class);

    public SshCommandRegular(SshServer server, Command command) {
        super(server, command);

        this.sudo = command.getSudo();
        this.cmd = command.getCmd();
        this.confirm = command.getConfirm();
        this.wait = command.getWait();
        this.background = command.getBackground();
        this.multiple = command.getMultiple();
        this.silent = command.getSilent();
    }

    @Override
    protected boolean beforeExecute() {
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

        return super.beforeExecute();
    }

    @Override
    protected String formatCmd(String cmd) {
        if (sudo) {
            return String.format("echo %s | %s -S -p '' %s %s", server.password,
                    server.sudoPath, server.nohupPath, cmd);
        } else {
            return String.format("%s %s", server.nohupPath, cmd);
        }
    }
}

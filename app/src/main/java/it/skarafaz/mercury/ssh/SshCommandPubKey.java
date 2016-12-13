package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.JSchException;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import it.skarafaz.mercury.event.SshCommandPubKeyInput;
import it.skarafaz.mercury.manager.SshManager;
import it.skarafaz.mercury.model.Command;

public class SshCommandPubKey extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandPubKey.class);
    private String pubKey;

    public SshCommandPubKey(SshServer server) {
        super(server, new Command());
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
        // Use user@host:port from server
        /*SshCommandDrop<String> drop = new SshCommandDrop<>();
        EventBus.getDefault().postSticky(new SshCommandPubKeyInput(drop));

        String input = drop.take();
        if (input == null) {
            return false;
        }
        setHostPortUser(input);*/

        return super.beforeExecute();
    }

    private void setHostPortUser(String input) {
        String[] sInput = input.split("@");
        String left = sInput[0];
        String right = sInput[1];

        String[] sRight = right.split(":");

        server.host = sRight[0];
        server.port = sRight.length > 1 ? Integer.valueOf(sRight[1]) : 22;
        server.user = left;
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

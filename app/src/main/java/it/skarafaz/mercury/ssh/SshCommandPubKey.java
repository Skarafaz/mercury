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

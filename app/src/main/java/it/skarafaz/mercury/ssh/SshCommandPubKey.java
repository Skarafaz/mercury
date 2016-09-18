package it.skarafaz.mercury.ssh;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.UserInfo;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import it.skarafaz.mercury.event.SshCommandPubKeyInput;
import it.skarafaz.mercury.manager.SshManager;

public class SshCommandPubKey extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandPubKey.class);
    private String pubKey;

    public SshCommandPubKey() {
        super();
    }

    @Override
    protected boolean beforeExecute() {
        SshCommandDrop<String> drop = new SshCommandDrop<>();
        EventBus.getDefault().postSticky(new SshCommandPubKeyInput(drop));

        String input = drop.take();
        if (input == null) {
            return false;
        }
        setHostPortUser(input);

        return super.beforeExecute();
    }

    private void setHostPortUser(String input) {
        String[] sInput = input.split("@");
        String left = sInput[0];
        String right = sInput[1];

        String[] sRight = right.split(":");

        this.host = sRight[0];
        this.port = sRight.length > 1 ? Integer.valueOf(sRight[1]) : 22;
        this.user = left;
    }

    @Override
    protected boolean initConnection() {
        boolean success = true;
        try {
            jsch.setKnownHosts(SshManager.getInstance().getKnownHosts().getAbsolutePath());
            pubKey = SshManager.getInstance().getPublicKeyContent();
        } catch (IOException | JSchException e) {
            logger.error(e.getMessage().replace("\n", " "));
            success = false;
        }
        return success;
    }

    @Override
    protected boolean waitForChannelClosed(ChannelExec channel, InputStream stdout, InputStream stderr) {
        boolean success = true;
        try {
            byte[] tmp = new byte[1024];
            while (true) {
                while (stdout.available() > 0) {
                    int i = stdout.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                }
                if (channel.isClosed()) {
                    if (stdout.available() > 0) {
                        continue;
                    }
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                    // ignore
                }
            }
            if (channel.getExitStatus() != 0) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(stderr));
                    logger.error(String.format("exit-status: %d - %s", channel.getExitStatus(), read(reader)));
                } finally {
                    if (reader != null) {
                        reader.close();
                    }
                }
                success = false;
            }
        } catch (IOException e) {
            logger.error(e.getMessage().replace("\n", " "));
        }
        return success;
    }

    @Override
    protected UserInfo getUserInfo() {
        return new SshCommandUserInfo();
    }

    @Override
    protected Properties getSessionConfig() {
        Properties config = super.getSessionConfig();
        config.put("PreferredAuthentications", "password");
        config.put("MaxAuthTries", "1");
        return config;
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

    private String read(BufferedReader reader) throws IOException {
        String aux;
        List<String> lines = new ArrayList<>();
        while ((aux = reader.readLine()) != null) {
            lines.add(aux);
        }
        return StringUtils.join(lines, " ");
    }
}

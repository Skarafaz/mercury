package it.skarafaz.mercury.event;

import it.skarafaz.mercury.ssh.SshCommandDrop;

public class SshCommandConfirm {
    private String cmd;
    private SshCommandDrop<Boolean> drop;

    public SshCommandConfirm(String cmd, SshCommandDrop<Boolean> drop) {
        this.cmd = cmd;
        this.drop = drop;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public SshCommandDrop<Boolean> getDrop() {
        return drop;
    }

    public void setDrop(SshCommandDrop<Boolean> drop) {
        this.drop = drop;
    }
}

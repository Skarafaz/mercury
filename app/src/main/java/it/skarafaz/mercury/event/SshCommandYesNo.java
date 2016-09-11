package it.skarafaz.mercury.event;

import it.skarafaz.mercury.ssh.SshCommandDrop;

public class SshCommandYesNo {
    private String message;
    private SshCommandDrop<Boolean> drop;

    public SshCommandYesNo(String message, SshCommandDrop<Boolean> drop) {
        this.message = message;
        this.drop = drop;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SshCommandDrop<Boolean> getDrop() {
        return drop;
    }

    public void setDrop(SshCommandDrop<Boolean> drop) {
        this.drop = drop;
    }
}

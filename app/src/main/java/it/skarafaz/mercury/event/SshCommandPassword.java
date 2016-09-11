package it.skarafaz.mercury.event;

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

    public void setDrop(SshCommandDrop<String> drop) {
        this.drop = drop;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

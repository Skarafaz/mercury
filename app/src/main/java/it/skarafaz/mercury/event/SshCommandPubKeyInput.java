package it.skarafaz.mercury.event;

import it.skarafaz.mercury.ssh.SshCommandDrop;

public class SshCommandPubKeyInput {
    SshCommandDrop<String> drop;

    public SshCommandPubKeyInput(SshCommandDrop<String> drop) {
        this.drop = drop;
    }

    public SshCommandDrop<String> getDrop() {
        return drop;
    }
}

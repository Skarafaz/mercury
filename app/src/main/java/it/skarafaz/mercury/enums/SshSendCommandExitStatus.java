package it.skarafaz.mercury.enums;

import it.skarafaz.mercury.R;

public enum SshSendCommandExitStatus {
    CONNECTION_FAILED(R.string.connection_failed),
    COMMAND_SENT(R.string.command_sent);

    private int msg;

    SshSendCommandExitStatus(int msg) {
        this.msg = msg;
    }

    public int msg() {
        return msg;
    }
}

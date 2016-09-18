package it.skarafaz.mercury.ssh;

import it.skarafaz.mercury.R;

public enum SshCommandStatus {
    CONNECTION_INIT_ERROR(R.string.connection_init_error),
    CONNECTION_FAILED(R.string.connection_failed),
    EXECUTION_FAILED(R.string.execution_failed),
    COMMAND_SENT(R.string.command_sent);

    private int message;

    SshCommandStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}

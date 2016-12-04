package it.skarafaz.mercury.ssh;

import it.skarafaz.mercury.R;

public enum SshCommandStatus {
    CONNECTION_INIT_ERROR(R.string.connection_init_error),
    CONNECTION_FAILED(R.string.connection_failed),
    COMMUNICATION_ERROR(R.string.communication_error),
    EXECUTION_FAILED(R.string.execution_failed),
    COMMAND_TIMEOUT(R.string.command_timeout),
    COMMAND_SUCCESSFUL(R.string.command_successful),
    COMMAND_SENT(R.string.command_sent);

    private int message;

    SshCommandStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}

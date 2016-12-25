package it.skarafaz.mercury.ssh;

import it.skarafaz.mercury.R;

public enum SshCommandStatus {
    CONNECTION_INIT_ERROR(R.string.connection_init_error),
    CONNECTION_FAILED(R.string.connection_failed),
    EXECUTION_FAILED(R.string.execution_failed),
    COMMUNICATION_ERROR(R.string.communication_error),
    COMMAND_TIMEOUT(R.string.command_timeout),
    COMMAND_SENT(R.string.command_sent),
    COMMAND_SUCCESSFUL(R.string.command_successful);

    private int message;
    private String output;

    SshCommandStatus(int message) {
        this.message = message;
    }

    public int message() {
        return message;
    }
}

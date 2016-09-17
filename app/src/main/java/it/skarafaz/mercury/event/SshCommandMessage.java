package it.skarafaz.mercury.event;

public class SshCommandMessage {
    private String message;

    public SshCommandMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

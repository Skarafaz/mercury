package it.skarafaz.mercury.event;

import it.skarafaz.mercury.ssh.SshCommandStatus;

public class SshCommandEnd {
    private Boolean background;
    private Boolean silent;
    private SshCommandStatus status;

    public SshCommandEnd(Boolean background, Boolean silent, SshCommandStatus status) {
        this.background = background;
        this.silent = silent;
        this.status = status;
    }

    public Boolean getBackground() {
        return background;
    }

    public Boolean getSilent() {
        return silent;
    }

    public SshCommandStatus getStatus() {
        return status;
    }
}

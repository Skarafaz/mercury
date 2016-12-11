package it.skarafaz.mercury.event;

public class SshCommandStart {
    private Boolean background;

    public SshCommandStart(Boolean background) {
        this.background = background;
    }

    public Boolean getBackground() {
        return background;
    }
}

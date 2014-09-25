package it.skarafaz.mercury.data;

/**
 * Created by Skarafaz on 14/09/2014.
 */
public class Command {
    private String name;
    private boolean sudo;
    private String cmd;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSudo() {
        return sudo;
    }

    public void setSudo(boolean sudo) {
        this.sudo = sudo;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }
}

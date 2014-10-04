package it.skarafaz.mercury.data;

import java.io.Serializable;

public class Command implements Serializable {
    private static final long serialVersionUID = -1107949489549383265L;
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

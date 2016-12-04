package it.skarafaz.mercury.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Command implements Serializable, Comparable<Command> {
    private static final long serialVersionUID = -1107949489549383265L;
    private String icon;
    private String name;
    private Boolean sudo;
    private String cmd;
    private Boolean confirm;
    private Boolean wait;
    private Server server;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSudo() {
        return sudo;
    }

    public void setSudo(Boolean sudo) {
        this.sudo = sudo;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Boolean getConfirm() {
        return confirm;
    }

    public void setConfirm(Boolean confirm) {
        this.confirm = confirm;
    }

    public Boolean getWait() {
        return wait;
    }

    public void setWait(Boolean wait) {
        this.wait = wait;
    }

    @JsonBackReference
    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public int compareTo(@NonNull Command another) {
        return name.toLowerCase().compareTo(another.getName().toLowerCase());
    }
}

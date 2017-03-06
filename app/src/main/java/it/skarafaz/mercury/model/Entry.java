package it.skarafaz.mercury.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

@SuppressWarnings("unused")
public abstract class Entry implements Serializable, Comparable<Entry> {
    private static final long serialVersionUID = -1107949489549383265L;
    private String icon;
    private String name;

    @JsonIgnore
    private int running = 0;
    @JsonIgnore
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

    public int getRunning() {
        return running;
    }

    public int increaseRunning() {
        return ++this.running;
    }

    public int decreaseRunning() {
        return --this.running;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public int compareTo(@NonNull Entry another) {
        return name.toLowerCase().compareTo(another.getName().toLowerCase());
    }

    public abstract String getInfo();
    public abstract String getProgressText();
}

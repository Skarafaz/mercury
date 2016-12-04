package it.skarafaz.mercury.model;

import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("unused")
public class Server implements Serializable, Comparable<Server> {
    private static final long serialVersionUID = 7247694914871605048L;
    private String name;
    private String host;
    private Integer port;
    @JsonProperty("mdnsname")
    private String mDnsName;
    @JsonProperty("mdnstype")
    private String mDnsType;
    private String user;
    private String password;
    private String sudoPath;
    private String nohupPath;
    private List<Command> commands;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getMDnsName() {
        return mDnsName;
    }

    public void setMDnsName(String mDnsName) {
        this.mDnsName = mDnsName;
    }

    public String getMDnsType() {
        return mDnsType;
    }

    public void setMDnsType(String mDnsType) {
        this.mDnsType = mDnsType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSudoPath() {
        return sudoPath;
    }

    public void setSudoPath(String sudoPath) {
        this.sudoPath = sudoPath;
    }

    public String getNohupPath() {
        return nohupPath;
    }

    public void setNohupPath(String nohupPath) {
        this.nohupPath = nohupPath;
    }

    @JsonManagedReference
    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    @Override
    public int compareTo(@NonNull Server another) {
        return name.toLowerCase().compareTo(another.getName().toLowerCase());
    }
}

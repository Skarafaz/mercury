package it.skarafaz.mercury.event;

import java.io.File;

import it.skarafaz.mercury.ssh.SshCommandStatus;


public class SftpDownloadEnd {
    private int id;
    private File file;
    private SshCommandStatus status;
    private Boolean view;

    public SftpDownloadEnd(int id, File file, SshCommandStatus status, Boolean view) {
        this.id = id;
        this.file = file;
        this.status = status;
        this.view = view;
    }

    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public SshCommandStatus getStatus() {
        return status;
    }

    public Boolean getView() {
        return view;
    }
}

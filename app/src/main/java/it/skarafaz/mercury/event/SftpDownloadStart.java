package it.skarafaz.mercury.event;

import java.io.File;

public class SftpDownloadStart {
    private int id;
    private File file;

    public SftpDownloadStart(int id, File file) {
        this.id = id;
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
    }
}

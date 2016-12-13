package it.skarafaz.mercury.event;

import java.io.File;

public class SftpDownloadProgress {
    private int id;
    private File file;
    private long progress;
    private long max;

    public SftpDownloadProgress(int id, File file, long progress, long max) {
        this.id = id;
        this.file = file;
        this.progress = progress;
        this.max = max;
    }

    public int getId() {
        return id;
    }

    public File getFile() {
        return file;
    }

    public long getProgress() {
        return progress;
    }

    public long getMax() {
        return max;
    }
}

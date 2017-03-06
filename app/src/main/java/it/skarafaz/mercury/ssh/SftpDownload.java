package it.skarafaz.mercury.ssh;

import android.os.Environment;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.SftpProgressMonitor;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import it.skarafaz.mercury.event.SftpDownloadEnd;
import it.skarafaz.mercury.event.SftpDownloadProgress;
import it.skarafaz.mercury.event.SftpDownloadStart;
import it.skarafaz.mercury.model.Command;
import it.skarafaz.mercury.model.RegularCommand;

public class SftpDownload extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(SftpDownload.class);
    protected static final int TIMEOUT = 10000;
    protected static int nextId = 0;
    protected int id;
    protected SshServer server;
    protected RegularCommand command;

    protected File target;

    public SftpDownload(SshServer server, RegularCommand command) {
        this.id = nextId++;
        this.server = server;
        this.command = command;
        this.target = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), new
                File(command.getDownload()).getName());
    }

    @Override
    public void run() {
        if(beforeExecute()) {
            SshCommandStatus status = execute();
            afterExecute(status);
        }
    }

    protected boolean beforeExecute() {
        EventBus.getDefault().postSticky(new SftpDownloadStart(id, target));
        return true;
    }

    private SshCommandStatus execute() {
        SshCommandStatus status = server.connect();
        if (status != SshCommandStatus.COMMAND_SUCCESSFUL) {
            return status;
        }

        return download(command.getDownload(), target);
    }

    protected void afterExecute(SshCommandStatus status) {
        EventBus.getDefault().postSticky(new SftpDownloadEnd(id, target, status, command.getView()));
    }

    protected SshCommandStatus download(String source, final File target) {
        logger.debug(String.format("[%d] downloading file %s to loaction %s", getId(), source, target));
        ChannelSftp channel = null;
        try {
            channel = (ChannelSftp) server.session.openChannel("sftp");
            channel.connect();
            channel.get(source, target.toString(), new SftpProgressMonitor() {
                private long max;
                private long count;
                private long lastUpdate;
                @Override
                public void init(int op, String src, String dest, long max) {
                    logger.trace(String.format("Init download op %d from %s to %s (%d bytes)", op, src, dest, max));
                    this.max = max;
                    this.count = 0;
                    this.lastUpdate = 0;
                    if (!command.getSilent()) {
                        EventBus.getDefault().postSticky(new SftpDownloadProgress(id, target, 0, max));
                    }
                }

                @Override
                public boolean count(long count) {
                    this.count += count;
                    logger.trace(String.format("Downloaded %d of %d bytes", this.count, max));
                    if (!command.getSilent() && System.currentTimeMillis() - lastUpdate > 100) {
                        EventBus.getDefault().postSticky(new SftpDownloadProgress(id, target, this.count, max));
                        lastUpdate = System.currentTimeMillis();
                    }
                    return true;
                }

                @Override
                public void end() {
                    logger.trace("Download finished");
                }
            }, ChannelSftp.OVERWRITE);
            logger.debug(String.format("[%d] finished downloading file %s to loaction %s", getId(), source, target));
            return SshCommandStatus.COMMAND_SUCCESSFUL;
        } catch (JSchException | SftpException e) {
            logger.debug(String.format("[%d] could not download file %s to loaction %s", getId(), source, target), e);
            logger.error(e.getMessage().replace("\n", " "));
            return SshCommandStatus.COMMUNICATION_ERROR;
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
        }
    }
}

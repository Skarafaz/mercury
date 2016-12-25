package it.skarafaz.mercury.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.model.RegularCommand;

public class SshCommandRegular extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRegular.class);

    private String download;
    private Boolean view;

    public SshCommandRegular(SshServer server, RegularCommand command) {
        super(server, command);

        this.sudo = command.getSudo();
        this.cmd = command.getCmd();
        this.download = command.getDownload();
        this.confirm = command.getConfirm();
        this.wait = command.getWait();
        this.background = command.getBackground();
        this.silent = command.getSilent();
        this.view = command.getView();
    }

    @Override
    protected void afterExecute(SshCommandStatus status) {
        super.afterExecute(status);
        if (download != null && (status == SshCommandStatus.COMMAND_SENT || status == SshCommandStatus
                .COMMAND_SUCCESSFUL)) {
            (new SftpDownload(server, (RegularCommand) command)).start();
        }
    }
}

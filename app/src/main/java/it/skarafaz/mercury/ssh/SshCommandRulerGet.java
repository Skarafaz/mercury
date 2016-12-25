package it.skarafaz.mercury.ssh;

import org.greenrobot.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.event.SshCommandRulerUpdate;
import it.skarafaz.mercury.model.Ruler;

public class SshCommandRulerGet extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRulerGet.class);

    public SshCommandRulerGet(SshServer server, Ruler ruler) {
        super(server, ruler);

        this.sudo = ruler.getSudo();
        this.cmd = ruler.getGetCmd();
        this.confirm = ruler.getConfirm();
        this.wait = true;
        this.background = ruler.getBackground();
        this.silent = ruler.getSilent();
    }

    @Override
    protected void afterExecute(SshCommandStatus status) {
        if (status == SshCommandStatus.COMMAND_SENT || status == SshCommandStatus.COMMAND_SUCCESSFUL) {
            try {
                ((Ruler) command).setValue(Integer.valueOf(output.split("\n")[0]));
                EventBus.getDefault().postSticky(new SshCommandRulerUpdate());
            } catch (NumberFormatException e) {
                logger.error(String.format("Could not parse value: %s", e));
                status = SshCommandStatus.EXECUTION_FAILED;
            }
        }
        super.afterExecute(status);
    }
}

package it.skarafaz.mercury.ssh;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.model.Ruler;

public class SshCommandRulerSet extends SshCommand {
    private static final Logger logger = LoggerFactory.getLogger(SshCommandRulerSet.class);

    public SshCommandRulerSet(SshServer server, Ruler ruler) {
        super(server, ruler);

        this.sudo = ruler.getSudo();
        this.cmd = ruler.getSetCmd();
        this.confirm = ruler.getConfirm();
        this.wait = ruler.getWait();
        this.background = ruler.getBackground();
        this.silent = ruler.getSilent();
    }

    @Override
    protected boolean beforeExecute() {
        setEnv("VALUE", String.valueOf(((Ruler) command).getValue()));
        return super.beforeExecute();
    }
}

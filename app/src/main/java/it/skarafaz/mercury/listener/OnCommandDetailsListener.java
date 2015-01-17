package it.skarafaz.mercury.listener;

import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.skarafaz.mercury.data.Command;

public class OnCommandDetailsListener implements View.OnClickListener {
    private static final Logger logger = LoggerFactory.getLogger(OnCommandDetailsListener.class);
    Command command;

    public OnCommandDetailsListener(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        logger.debug("show details: " + command.getName());
    }
}

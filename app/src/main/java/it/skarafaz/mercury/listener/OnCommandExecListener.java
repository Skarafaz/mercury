package it.skarafaz.mercury.listener;

import android.util.Log;
import android.view.View;

import it.skarafaz.mercury.data.Command;

public class OnCommandExecListener implements View.OnClickListener {
    Command command;

    public OnCommandExecListener(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        Log.d("OnCommandExecListener", "exec " + command.getName());
    }
}

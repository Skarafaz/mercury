package it.skarafaz.mercury.listener;

import android.util.Log;
import android.view.View;

import it.skarafaz.mercury.data.Command;

public class OnCommandDetailsListener implements View.OnClickListener {
    Command command;

    public OnCommandDetailsListener(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        Log.d(OnCommandDetailsListener.class.getSimpleName(), "details: " + command.getName());
    }
}

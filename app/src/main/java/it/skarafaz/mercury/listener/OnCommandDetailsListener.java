package it.skarafaz.mercury.listener;

import android.content.Context;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import it.skarafaz.mercury.data.Command;

public class OnCommandDetailsListener implements View.OnClickListener {
    Context context;
    Command command;

    public OnCommandDetailsListener(Context context, Command command) {
        this.context = context;
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        new MaterialDialog.Builder(context)
            .title(command.getName())
            .content(command.getCmd())
            .show();
    }
}

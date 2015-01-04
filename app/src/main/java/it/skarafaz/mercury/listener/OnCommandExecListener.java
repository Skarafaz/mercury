package it.skarafaz.mercury.listener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.data.ExecCommandTaskResult;
import it.skarafaz.mercury.manager.SSHManager;

public class OnCommandExecListener implements View.OnClickListener {
    Context context;
    Command command;
    ProgressDialog pDialog;

    public OnCommandExecListener(Context context, Command command) {
        this.context = context;
        this.command = command;
        pDialog = new ProgressDialog(this.context);
        pDialog.setMessage(this.context.getResources().getString(R.string.sending_command));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        new AsyncTask<Void, Void, ExecCommandTaskResult>() {
            @Override
            protected void onPreExecute() {
                pDialog.show();
            }

            @Override
            protected ExecCommandTaskResult doInBackground(Void... params) {
                ExecCommandTaskResult result = ExecCommandTaskResult.COMMAND_SENT;
                SSHManager sshManager = new SSHManager(command);
                if (sshManager.connect()) {
                    if (!sshManager.sendCommand()) {
                        result = ExecCommandTaskResult.CONNECTION_FAILED;
                    }
                    sshManager.disconnect();
                } else {
                    result = ExecCommandTaskResult.CONNECTION_FAILED;
                }
                return result;
            }

            @Override
            protected void onPostExecute(ExecCommandTaskResult result) {
                String message = "";
                if (result == ExecCommandTaskResult.COMMAND_SENT) {
                    message = context.getResources().getString(R.string.command_sent);
                } else if (result == ExecCommandTaskResult.CONNECTION_FAILED) {
                    message = context.getResources().getString(R.string.connection_failed);
                }
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                pDialog.dismiss();
                toast.show();
            }
        }.execute();
    }
}

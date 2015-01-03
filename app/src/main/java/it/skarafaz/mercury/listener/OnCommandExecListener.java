package it.skarafaz.mercury.listener;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

import it.skarafaz.mercury.MercuryApplication;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.data.ExecCommandTaskResult;
import it.skarafaz.mercury.manager.SSHManager;

public class OnCommandExecListener implements View.OnClickListener {
    Command command;

    public OnCommandExecListener(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        new AsyncTask<Void, Void, ExecCommandTaskResult>() {
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
                    message = MercuryApplication.getContext().getResources().getString(R.string.command_sent);
                } else if (result == ExecCommandTaskResult.CONNECTION_FAILED) {
                    message = MercuryApplication.getContext().getResources().getString(R.string.connection_failed);
                }
                Toast toast = Toast.makeText(MercuryApplication.getContext(), message, Toast.LENGTH_SHORT);
                toast.show();
            }
        }.execute();
    }
}

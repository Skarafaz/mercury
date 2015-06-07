package it.skarafaz.mercury.listener;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Toast;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.data.enums.ExecCommandTaskResult;
import it.skarafaz.mercury.fragment.SendingCommandDialogFragment;
import it.skarafaz.mercury.ssh.SshCommand;

public class OnCommandExecListener implements View.OnClickListener {
    private Context context;
    private Command command;

    public OnCommandExecListener(Context context, Command command) {
        this.context = context;
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        new AsyncTask<Void, Void, ExecCommandTaskResult>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected ExecCommandTaskResult doInBackground(Void... params) {
                ExecCommandTaskResult result = ExecCommandTaskResult.COMMAND_SENT;
                SshCommand sshCommand = new SshCommand(command);
                if (sshCommand.connect()) {
                    if (!sshCommand.send()) {
                        result = ExecCommandTaskResult.CONNECTION_FAILED;
                    }
                    sshCommand.disconnect();
                } else {
                    result = ExecCommandTaskResult.CONNECTION_FAILED;
                }
                return result;
            }

            @Override
            protected void onPostExecute(ExecCommandTaskResult result) {
                showToaster(result);
                dismissProgressDialog();
            }
        }.execute();
    }

    private void showToaster(ExecCommandTaskResult result) {
        String message = "";
        if (result == ExecCommandTaskResult.COMMAND_SENT) {
            message = context.getResources().getString(R.string.command_sent);
        } else if (result == ExecCommandTaskResult.CONNECTION_FAILED) {
            message = context.getResources().getString(R.string.connection_failed);
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void showProgressDialog() {
        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        ft.add(SendingCommandDialogFragment.newInstance(), SendingCommandDialogFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    private void dismissProgressDialog() {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = fm.findFragmentByTag(SendingCommandDialogFragment.TAG);
        if (frag != null) {
            ft.remove(frag);
        }
        ft.commitAllowingStateLoss();
    }
}

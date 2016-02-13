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
import it.skarafaz.mercury.enums.SendCommandExitStatus;
import it.skarafaz.mercury.fragment.ProgressDialogFragment;
import it.skarafaz.mercury.model.Command;
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
        new AsyncTask<Void, Void, SendCommandExitStatus>() {
            @Override
            protected void onPreExecute() {
                showProgressDialog();
            }

            @Override
            protected SendCommandExitStatus doInBackground(Void... params) {
                SendCommandExitStatus status = SendCommandExitStatus.COMMAND_SENT;
                SshCommand sshCommand = new SshCommand(command);
                if (sshCommand.connect()) {
                    if (!sshCommand.send()) {
                        status = SendCommandExitStatus.CONNECTION_FAILED;
                    }
                    sshCommand.disconnect();
                } else {
                    status = SendCommandExitStatus.CONNECTION_FAILED;
                }
                return status;
            }

            @Override
            protected void onPostExecute(SendCommandExitStatus status) {
                Toast.makeText(context, context.getString(status.msg()), Toast.LENGTH_SHORT).show();
                dismissProgressDialog();
            }
        }.execute();
    }

    private void showProgressDialog() {
        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        ft.add(ProgressDialogFragment.newInstance(context.getString(R.string.sending_command)), ProgressDialogFragment.TAG);
        ft.commitAllowingStateLoss();
    }

    private void dismissProgressDialog() {
        FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment frag = fm.findFragmentByTag(ProgressDialogFragment.TAG);
        if (frag != null) {
            ft.remove(frag);
        }
        ft.commitAllowingStateLoss();
    }
}

package it.skarafaz.mercury.ssh;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.enums.SendCommandExitStatus;
import it.skarafaz.mercury.fragment.ProgressDialogFragment;
import it.skarafaz.mercury.model.Command;

public class SshSendCommandTask extends AsyncTask<Command, Void, SendCommandExitStatus> {
    private Context context;

    public SshSendCommandTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected SendCommandExitStatus doInBackground(Command... params) {
        SendCommandExitStatus status = SendCommandExitStatus.COMMAND_SENT;
        SshCommand sshCommand = new SshCommand(params[0]);
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

package it.skarafaz.mercury.ssh;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.Toast;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.enums.SshSendCommandExitStatus;
import it.skarafaz.mercury.fragment.ProgressDialogFragment;
import it.skarafaz.mercury.model.Command;

public class SshSendCommandTask extends AsyncTask<Command, Void, SshSendCommandExitStatus> {
    private Context context;

    public SshSendCommandTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        showProgressDialog();
    }

    @Override
    protected SshSendCommandExitStatus doInBackground(Command... params) {
        SshSendCommandExitStatus status = SshSendCommandExitStatus.COMMAND_SENT;
        SshCommand sshCommand = new SshCommand(params[0]);
        if (sshCommand.connect()) {
            if (!sshCommand.send()) {
                status = SshSendCommandExitStatus.CONNECTION_FAILED;
            }
            sshCommand.disconnect();
        } else {
            status = SshSendCommandExitStatus.CONNECTION_FAILED;
        }
        return status;
    }

    @Override
    protected void onPostExecute(SshSendCommandExitStatus status) {
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

package it.skarafaz.mercury.listener;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.InputStream;

import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.data.Server;

public class OnCommandExecListener implements View.OnClickListener {
    Command command;

    public OnCommandExecListener(Command command) {
        this.command = command;
    }

    @Override
    public void onClick(View v) {
        Log.d("OnCommandExecListener", "exec " + command.getName());
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                execCommand();
                return null;
            }
        }.execute();
    }

    private void execCommand() {
        try {
            Server server = command.getServer();
            JSch jsch = new JSch();
            Session session = jsch.getSession(server.getUser(), server.getHost(), server.getPort());
            session.setPassword(server.getPassword());
            session.setConfig("StrictHostKeyChecking", "no"); // FIXME known hosts management
            session.connect();
            ChannelExec channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command.getCmd());
            channel.setInputStream(null);
            channel.setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }
            channel.disconnect();
            session.disconnect();
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), e.getMessage());
        }
    }
}

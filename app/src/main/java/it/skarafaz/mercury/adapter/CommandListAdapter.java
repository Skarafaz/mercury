package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jcraft.jsch.Logger;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.model.Command;
import it.skarafaz.mercury.ssh.SshCommandRegular;
import it.skarafaz.mercury.ssh.SshServer;
import it.skarafaz.mercury.view.TextProgressBar;

public class CommandListAdapter extends ArrayAdapter<Command> {
    protected final SshServer server;

    public CommandListAdapter(Context context, SshServer server, List<Command> commands) {
        super(context, R.layout.command_list_item, commands);
        this.server = server;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.command_list_item, parent, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final Command command = getItem(position);
        if (command.getIcon() == null) {
            holder.icon.setVisibility(View.INVISIBLE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageBitmap(BitmapFactory.decodeFile(command.getIcon()));
        }

        holder.name.setText(command.getName());
        holder.progress.setText(command.getMultiple() ? String.valueOf(command.getRunning()) : "");
        holder.progress.setVisibility(command.getRunning() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title(command.getName())
                        .content(command.getCmd())
                        .show();
            }
        });
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (command.getMultiple() || command.getRunning() == 0) {
                    new SshCommandRegular(server, command).start();
                }
            }
        });
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.row)
        RelativeLayout row;
        @Bind(R.id.icon)
        ImageView icon;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.progress)
        TextProgressBar progress;
        @Bind(R.id.info)
        ImageView info;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
            progress.setTextColor(Color.WHITE);
            progress.setTextSize(32);
        }
    }
}

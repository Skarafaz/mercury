package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.listener.OnCommandDetailsListener;
import it.skarafaz.mercury.listener.OnCommandExecListener;

public class CommandListAdapter extends ArrayAdapter<Command> {
    private List<Command> commands;
    private Context context;

    public CommandListAdapter(Context context, List<Command> commands) {
        super(context, 0, commands);
        this.commands = commands;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.command_list_item, parent, false);
        }
        LinearLayout layout = (LinearLayout) row.findViewById(R.id.container);
        if (position % 2 == 0) {
            layout.setBackgroundColor(context.getResources().getColor(R.color.list_even));
        } else {
            layout.setBackgroundColor(context.getResources().getColor(R.color.list_odd));
        }
        final Command command = getItem(position);
        TextView name = (TextView) row.findViewById(R.id.name);
        name.setText(command.getName());
        TextView cmd = (TextView) row.findViewById(R.id.cmd);
        cmd.setText(command.getCmd());
        LinearLayout label = (LinearLayout) row.findViewById(R.id.label);
        label.setOnClickListener(new OnCommandDetailsListener(command));
        ImageView play = (ImageView) row.findViewById(R.id.play);
        play.setOnClickListener(new OnCommandExecListener(command));
        return row;
    }

    @Override
    public Command getItem(int index) {
        return commands.get(index);
    }

    @Override
    public int getCount() {
        return commands.size();
    }
}

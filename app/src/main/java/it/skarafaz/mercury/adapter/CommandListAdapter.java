package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;
import it.skarafaz.mercury.listener.OnCommandExecListener;

public class CommandListAdapter extends ArrayAdapter<Command> {

    public CommandListAdapter(Context context, List<Command> commands) {
        super(context, R.layout.command_list_item, commands);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Command command = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.command_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.container);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.cmd = (TextView) convertView.findViewById(R.id.cmd);
            viewHolder.label = (LinearLayout) convertView.findViewById(R.id.label);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            viewHolder.layout.setBackgroundColor(getContext().getResources().getColor(R.color.list_even));
        }
        viewHolder.name.setText(command.getName());
        viewHolder.cmd.setText(command.getCmd());
        viewHolder.label.setOnClickListener(new OnCommandExecListener(getContext(), command));
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout layout;
        TextView name;
        TextView cmd;
        LinearLayout label;
    }
}

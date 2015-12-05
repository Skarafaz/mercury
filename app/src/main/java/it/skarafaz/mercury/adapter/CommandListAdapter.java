package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.listener.OnCommandDetailsListener;
import it.skarafaz.mercury.listener.OnCommandExecListener;
import it.skarafaz.mercury.model.Command;

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
            viewHolder.row = (RelativeLayout) convertView.findViewById(R.id.row);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.info = (ImageView) convertView.findViewById(R.id.info);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.name.setText(command.getName());
        viewHolder.info.setOnClickListener(new OnCommandDetailsListener(getContext(), command));
        viewHolder.row.setOnClickListener(new OnCommandExecListener(getContext(), command));
        return convertView;
    }

    static class ViewHolder {
        RelativeLayout row;
        TextView name;
        ImageView info;
    }
}

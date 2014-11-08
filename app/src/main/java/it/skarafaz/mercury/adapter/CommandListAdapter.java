package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Command;

public class CommandListAdapter extends ArrayAdapter<Command> {
    private List<Command> commands;
    private Context context;

    public CommandListAdapter(Context context, List<Command> objects) {
        super(context, R.layout.command_list_item, objects);
        commands = objects;
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
        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CommandListAdapter", "details " + command.getName());
            }
        });
        ImageView play = (ImageView) row.findViewById(R.id.play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("CommandListAdapter", "play " + command.getName());
            }
        });
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

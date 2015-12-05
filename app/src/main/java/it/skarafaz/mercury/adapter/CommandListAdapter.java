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

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.listener.OnCommandDetailsListener;
import it.skarafaz.mercury.listener.OnCommandExecListener;
import it.skarafaz.mercury.model.Command;

public class CommandListAdapter extends ArrayAdapter<Command> {

    public CommandListAdapter(Context context, List<Command> commands) {
        super(context, R.layout.command_list_item, commands);
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
        Command command = getItem(position);
        holder.name.setText(command.getName());
        holder.info.setOnClickListener(new OnCommandDetailsListener(getContext(), command));
        holder.row.setOnClickListener(new OnCommandExecListener(getContext(), command));
        return view;
    }

    static class ViewHolder {
        @Bind(R.id.row)
        RelativeLayout row;
        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.info)
        ImageView info;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

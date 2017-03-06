/*
 * Mercury-SSH
 * Copyright (C) 2017 Skarafaz
 *
 * This file is part of Mercury-SSH.
 *
 * Mercury-SSH is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * Mercury-SSH is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Mercury-SSH.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import it.skarafaz.mercury.R;
import it.skarafaz.mercury.model.Entry;
import it.skarafaz.mercury.model.RegularCommand;
import it.skarafaz.mercury.model.Ruler;
import it.skarafaz.mercury.ssh.SshCommandRegular;
import it.skarafaz.mercury.ssh.SshCommandRulerGet;
import it.skarafaz.mercury.ssh.SshCommandRulerSet;
import it.skarafaz.mercury.ssh.SshServer;
import it.skarafaz.mercury.view.TextProgressBar;

public class CommandListAdapter extends ArrayAdapter<Entry> {
    private static final Logger logger = LoggerFactory.getLogger(CommandListAdapter.class);
    protected final SshServer server;

    public CommandListAdapter(Context context, SshServer server, List<Entry> entries) {
        super(context, R.layout.command_list_item, entries);
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

        final Entry entry = getItem(position);
        if (entry.getIcon() == null) {
            holder.icon.setVisibility(View.INVISIBLE);
        } else {
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageBitmap(BitmapFactory.decodeFile(entry.getIcon()));
        }

        holder.name.setText(entry.getName());
        if (entry instanceof Ruler) {
            final Ruler ruler = (Ruler) entry;
            holder.name.setVisibility(View.INVISIBLE);
            holder.ruler.setVisibility(View.VISIBLE);
            holder.ruler.setMax((ruler.getMax() - ruler.getMin() + ruler.getStep() - 1) / ruler.getStep());
            if (ruler.getValue() != null) {
                holder.ruler.setProgress(ruler.getValue());
            } else if (entry.getRunning() == 0) {
                logger.trace(String.format("Ruler %s has no value, starting get command...", ruler.getName()));
                new SshCommandRulerGet(server, ruler).start();
            }
            holder.ruler.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        ruler.setValue(progress * ruler.getStep() + ruler.getMin());
                        logger.trace(String.format("Ruler %s changed to, starting set command...", ruler.getName(),
                                ruler.getValue()));
                        (new SshCommandRulerSet(server, ruler)).start();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        } else {
            holder.name.setVisibility(View.VISIBLE);
            holder.ruler.setVisibility(View.INVISIBLE);
        }
        holder.progress.setText(entry.getProgressText());
        holder.progress.setVisibility(entry.getRunning() == 0 ? View.INVISIBLE : View.VISIBLE);
        holder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(getContext())
                        .title(entry.getName())
                        .content(entry.getInfo())
                        .show();
            }
        });
        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entry instanceof RegularCommand) {
                    if (entry.getRunning() == 0 || ((RegularCommand) entry).getMultiple()) {
                        new SshCommandRegular(server, (RegularCommand) entry).start();
                    }
                } else if (entry instanceof Ruler && entry.getRunning() == 0) {
                    new SshCommandRulerGet(server, (Ruler) entry).start();
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
        @Bind(R.id.ruler)
        SeekBar ruler;
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

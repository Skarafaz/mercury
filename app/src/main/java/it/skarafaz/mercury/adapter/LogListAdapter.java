package it.skarafaz.mercury.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import it.skarafaz.mercury.R;

public class LogListAdapter extends ArrayAdapter<String> {

    public LogListAdapter(Context context, List<String> lines) {
        super(context, R.layout.log_list_item, lines);
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}

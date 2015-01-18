package it.skarafaz.mercury.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.skarafaz.mercury.R;

public class LogActivity extends ActionBarActivity {
    private static final Logger logger = LoggerFactory.getLogger(LogActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setContentView(R.layout.activity_log);
        ListView list = (ListView) findViewById(R.id.list);
        list.setAdapter(new ArrayAdapter<>(this, R.layout.log_list_item, readLogFile()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_log, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                clear();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<String> readLogFile() {
        File file = new File(getDir("log", Context.MODE_PRIVATE), "mercury.log");
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, "UTF-8");
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return lines;
    }

    private void clear() {
        logger.debug("Clear log");
    }
}

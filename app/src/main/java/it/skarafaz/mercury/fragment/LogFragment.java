package it.skarafaz.mercury.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.util.ContextInitializer;
import ch.qos.logback.core.joran.spi.JoranException;
import it.skarafaz.mercury.R;

public class LogFragment extends ListFragment {
    public static final String LOG_DIR = "log";
    public static final String LOG_FILE = "mercury.log";
    public static final String ENCODING = "UTF-8";
    private static final Logger logger = LoggerFactory.getLogger(LogFragment.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_log, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        reload();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_log, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear:
                clearLog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void reload() {
        setListAdapter(new ArrayAdapter<>(getActivity(), R.layout.log_list_item, readLog()));
    }

    private List<String> readLog() {
        File file = new File(getActivity().getDir(LOG_DIR, Context.MODE_PRIVATE), LOG_FILE);
        List<String> lines = new ArrayList<>();
        try {
            lines = FileUtils.readLines(file, ENCODING);
        } catch (IOException e) {
            logger.debug(e.getMessage());
        }
        return lines;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void clearLog() {
        File logDir = getActivity().getDir(LOG_DIR, Context.MODE_PRIVATE);
        for (File file : logDir.listFiles()) {
            file.delete();
        }
        resetLoggerContext();
        reload();
    }

    private void resetLoggerContext() {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        ContextInitializer ci = new ContextInitializer(lc);
        lc.reset();
        try {
            ci.autoConfig();
        } catch (JoranException e) {
            Log.e(LogFragment.class.getSimpleName(), e.getMessage());
        }
    }
}

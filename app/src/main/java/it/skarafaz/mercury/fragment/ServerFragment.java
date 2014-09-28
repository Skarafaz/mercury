package it.skarafaz.mercury.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.data.Server;

public class ServerFragment extends Fragment{
    public static final String SERVER_ARG = "SERVER_ARG";
    private Server server;

    public static ServerFragment newInstance(Server server) {
        ServerFragment fragment = new ServerFragment();
        Bundle args = new Bundle();
        args.putSerializable(SERVER_ARG, server);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        server = getArguments() != null ? (Server) getArguments().getSerializable(SERVER_ARG) : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_server, container, false);
        ((TextView) rootView.findViewById(R.id.text)).setText(server.getName());
        return rootView;
    }
}

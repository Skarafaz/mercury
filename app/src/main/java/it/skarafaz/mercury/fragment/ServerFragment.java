package it.skarafaz.mercury.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.CommandListAdapter;
import it.skarafaz.mercury.model.Server;

public class ServerFragment extends ListFragment {
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
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new CommandListAdapter(getActivity(), server.getCommands()));
    }
}

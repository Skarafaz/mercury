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

package it.skarafaz.mercury.fragment;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.skarafaz.mercury.R;
import it.skarafaz.mercury.adapter.CommandListAdapter;
import it.skarafaz.mercury.model.Server;
import it.skarafaz.mercury.ssh.SshServer;

public class ServerFragment extends ListFragment {
    public static final String SERVER_ARG = "SERVER_ARG";
    private Server server;
    private SshServer sshServer;

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
        sshServer = new SshServer(server, getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_server, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setListAdapter(new CommandListAdapter(getActivity(), sshServer, server.getEntries()));
    }

    @Override
    public void onPause() {
        sshServer.disconnect();
        super.onPause();
     }

}

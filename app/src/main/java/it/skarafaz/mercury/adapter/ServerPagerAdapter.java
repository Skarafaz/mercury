package it.skarafaz.mercury.adapter;

import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import it.skarafaz.mercury.data.Server;
import it.skarafaz.mercury.fragment.ServerFragment;

public class ServerPagerAdapter extends FragmentStatePagerAdapter {
    List<Server> servers;

    public ServerPagerAdapter(FragmentManager fm, List<Server> servers) {
        super(fm);
        this.servers = servers;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new ServerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ServerFragment.SERVER_ARG, servers.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return servers.size();
    }
}

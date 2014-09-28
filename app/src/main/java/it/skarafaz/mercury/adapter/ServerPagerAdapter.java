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
        return ServerFragment.newInstance(servers.get(i));
    }

    @Override
    public int getCount() {
        return servers.size();
    }
}

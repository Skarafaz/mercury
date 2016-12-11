package it.skarafaz.mercury.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import it.skarafaz.mercury.fragment.ServerFragment;
import it.skarafaz.mercury.model.Server;

public class ServerPagerAdapter extends FragmentStatePagerAdapter {
    private List<Server> servers;
    private ServerFragment currentFragment;

    public ServerPagerAdapter(FragmentManager fm) {
        super(fm);

        servers = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return servers.size();
    }

    @Override
    public Fragment getItem(int i) {
        return ServerFragment.newInstance(servers.get(i));
    }

    public Server getServer(int i) {
        return servers.get(i);
    }

    public ServerFragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentFragment = (ServerFragment) object;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return servers.get(position).getName();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void updateServers(List<Server> servers) {
        this.servers.clear();
        this.servers.addAll(servers);
        notifyDataSetChanged();
    }
}

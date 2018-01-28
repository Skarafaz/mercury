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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import it.skarafaz.mercury.fragment.ServerFragment;
import it.skarafaz.mercury.model.Server;

import java.util.ArrayList;
import java.util.List;

public class ServerPagerAdapter extends FragmentStatePagerAdapter {
    private List<Server> servers;

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

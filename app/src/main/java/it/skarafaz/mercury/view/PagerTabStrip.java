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

package it.skarafaz.mercury.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class PagerTabStrip extends android.support.v4.view.PagerTabStrip {
    private static final int PADDING_BOTTOM = 9; // dp

    public PagerTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            if (v instanceof TextView) {
                TextView t = (TextView) v;
                t.setPadding(0, 0, 0, getPixelsFromDps(PADDING_BOTTOM));
            }
        }
    }

    private int getPixelsFromDps(int dps) {
        final float density = getResources().getDisplayMetrics().density;
        return (int) (dps * density + 0.5f);
    }
}

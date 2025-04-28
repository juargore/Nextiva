/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Menu;
import android.view.MenuItem;

import androidx.core.graphics.drawable.DrawableCompat;

/**
 * Created by adammacdonald on 2/8/18.
 */

public class MenuUtil {

    public static void tintAllIcons(Menu menu, final int color) {
        for (int i = 0; i < menu.size(); ++i) {
            final MenuItem item = menu.getItem(i);
            tintMenuItemIcon(color, item);
        }
    }

    public static void tintMenuItemIcon(int color, MenuItem item) {
        final Drawable drawable = item.getIcon();
        if (drawable != null) {
            final Drawable wrapped = DrawableCompat.wrap(drawable);
            drawable.mutate();
            DrawableCompat.setTint(wrapped, color);
            item.setIcon(drawable);
        }
    }

    public static void setMenuContentDescriptions(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setContentDescription(menu.getItem(i).getTitle());

                if (menu.getItem(i).getSubMenu() != null) {
                    for (int k = 0; k < menu.getItem(i).getSubMenu().size(); k++) {
                        menu.getItem(i).getSubMenu().getItem(k).setContentDescription(menu.getItem(i).getSubMenu().getItem(k).getTitle());
                    }
                }
            }
        }
    }
}

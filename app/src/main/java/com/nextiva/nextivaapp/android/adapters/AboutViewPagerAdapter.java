/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.fragments.AboutInfoFragment;
import com.nextiva.nextivaapp.android.fragments.AboutLegalNoticesFragment;
import com.nextiva.nextivaapp.android.fragments.AboutLicenseFragment;
import com.nextiva.nextivaapp.android.fragments.AboutPrivacyFragment;

/**
 * Created by joedephillipo on 2/20/18.
 */

public class AboutViewPagerAdapter extends FragmentPagerAdapter {

    public static final int ABOUT_INFO_FRAGMENT_INDEX = 0;
    public static final int ABOUT_LICENSE_FRAGMENT_INDEX = 1;
    public static final int ABOUT_PRIVACY_FRAGMENT_INDEX = 2;
    public static final int ABOUT_LEGAL_NOTICES_FRAGMENT_INDEX = 3;

    private final TabLayout mTabLayout;
    @ColorInt
    private final int mSelectedColor;
    @ColorInt
    private final int mUnselectedColor;
    private final ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private final Context mContext;

    public AboutViewPagerAdapter(FragmentManager fm, TabLayout tabLayout, @ColorInt int selectedColor, @ColorInt int unselectedColor, Context context) {
        super(fm);
        mTabLayout = tabLayout;
        mSelectedColor = selectedColor;
        mUnselectedColor = unselectedColor;
        mContext = context;

        mTabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, R.color.nextivaOrange));
    }

    public void setTabColor(int index, float positionOffset) {
        // Color transition between current and next tab colors
        TabLayout.Tab currentTab = mTabLayout.getTabAt(index);
        TabLayout.Tab presentTab;

        if (currentTab != null && currentTab.getCustomView() != null) {
            TextView currentTabTextView = (TextView) currentTab.getCustomView();
            currentTabTextView.setTextColor((Integer) mArgbEvaluator.evaluate(positionOffset, mSelectedColor, mUnselectedColor));
        }

        TabLayout.Tab nextTab = mTabLayout.getTabAt(index + 1);

        if (nextTab != null && nextTab.getCustomView() != null) {
            TextView nextTabTextView = (TextView) nextTab.getCustomView();
            nextTabTextView.setTextColor((Integer) mArgbEvaluator.evaluate(1 - positionOffset, mSelectedColor, mUnselectedColor));
        }

        TextView otherTabTextView;
        // Default other tabs to unselected color
        if (index > 0) {
            for (int i = 0; i < index; i++) {
                presentTab = mTabLayout.getTabAt(i);
                if (presentTab != null) {
                    otherTabTextView = (TextView) presentTab.getCustomView();
                    if (otherTabTextView != null) {
                        otherTabTextView.setTextColor(mUnselectedColor);
                    }
                }
            }
        }
        TextView lastTabText;
        if (index + 2 < getCount()) {
            for (int i = index + 2; i < getCount(); i++) {
                presentTab = mTabLayout.getTabAt(i);
                if (presentTab != null) {
                    lastTabText = (TextView) presentTab.getCustomView();
                    if (lastTabText != null) {
                        lastTabText.setTextColor(mUnselectedColor);
                    }
                }
            }
        }
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ABOUT_INFO_FRAGMENT_INDEX: {
                return AboutInfoFragment.newInstance();
            }
            case ABOUT_LICENSE_FRAGMENT_INDEX: {
                return AboutLicenseFragment.newInstance();
            }
            case ABOUT_PRIVACY_FRAGMENT_INDEX: {
                return AboutPrivacyFragment.newInstance();
            }
            case ABOUT_LEGAL_NOTICES_FRAGMENT_INDEX: {
                return AboutLegalNoticesFragment.newInstance();
            }
        }

        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public void setupTabTitles() {
        TabLayout.Tab presentTab;

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            presentTab = mTabLayout.getTabAt(i);
            TextView customTabView = (TextView) View.inflate(mContext, R.layout.view_text_view_tab, null);
            if (presentTab != null) {
                presentTab.setCustomView(customTabView);
                if (presentTab.getCustomView() != null) {
                    TextView tabText = presentTab.getCustomView().findViewById(R.id.tab_text_view);


                    if (i == ABOUT_INFO_FRAGMENT_INDEX) {
                        tabText.setText(R.string.about_info_tab);
                    } else if (i == ABOUT_LICENSE_FRAGMENT_INDEX) {
                        tabText.setText(R.string.about_license_tab);
                    } else if (i == ABOUT_PRIVACY_FRAGMENT_INDEX) {
                        tabText.setText(R.string.about_privacy_tab);
                    } else if (i == ABOUT_LEGAL_NOTICES_FRAGMENT_INDEX) {
                        tabText.setText(R.string.about_legal_notices_tab);
                    }

                    presentTab.setContentDescription(tabText.getText());
                }
            }
        }
    }
}
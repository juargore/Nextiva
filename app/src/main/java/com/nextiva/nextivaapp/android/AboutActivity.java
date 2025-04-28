/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.INFO_TAB_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.INFO_TAB_SWIPED_TO;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LEGAL_NOTICES_TAB_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LEGAL_NOTICES_TAB_SWIPED_TO;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LICENSE_TAB_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.LICENSE_TAB_SWIPED_TO;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.PRIVACY_TAB_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ABOUT;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.nextiva.nextivaapp.android.adapters.AboutViewPagerAdapter;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ActivityAboutBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutActivity extends BaseActivity {

    protected Toolbar mToolbar;
    protected TabLayout mTabLayout;
    protected ViewPager mViewPager;
    protected RelativeLayout mBackArrowView;
    protected TextView mAboutTitleTextView;

    @Inject
    protected AnalyticsManager mAnalyticsManager;

    @Enums.View.ViewPagerActionTypes.ActionType
    private int mLastAction = Enums.View.ViewPagerActionTypes.RESET;
    private AboutViewPagerAdapter mAdapter;

    public static Intent newIntent(@NonNull Context context) {
        return new Intent(context, AboutActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());

        mAboutTitleTextView.setText(getString(R.string.about_toolbar_title, getString(R.string.app_name)));
        setSupportActionBar(mToolbar);

        mBackArrowView.setOnClickListener(v -> {
            mAnalyticsManager.logEvent(ABOUT, BACK_BUTTON_PRESSED);
            onBackPressed();
        });

        mAdapter = new AboutViewPagerAdapter(getSupportFragmentManager(),
                                             mTabLayout,
                                             ContextCompat.getColor(this, ApplicationUtil.isNightModeEnabled(this, mSettingsManager) ? R.color.nextivaNewBlue : R.color.nextivaPrimaryBlue),
                                             ContextCompat.getColor(this, R.color.nextivaGrey),
                                             this);

        // In order for the analytics to correctly track, we must setup the ViewPager before setting up the TabLayout
        mViewPager.setAdapter(mAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mAdapter.setTabColor(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                if (mLastAction == Enums.View.ViewPagerActionTypes.RESET) {
                    mLastAction = Enums.View.ViewPagerActionTypes.SWIPE;

                    switch (position) {
                        case AboutViewPagerAdapter.ABOUT_INFO_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, INFO_TAB_SWIPED_TO);
                            break;
                        case AboutViewPagerAdapter.ABOUT_LICENSE_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, LICENSE_TAB_SWIPED_TO);
                            break;
                        case AboutViewPagerAdapter.ABOUT_PRIVACY_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, LICENSE_TAB_SWIPED_TO);
                            break;
                        case AboutViewPagerAdapter.ABOUT_LEGAL_NOTICES_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, LEGAL_NOTICES_TAB_SWIPED_TO);
                            break;
                    }

                } else {
                    mLastAction = Enums.View.ViewPagerActionTypes.RESET;
                }
            }
        });

        // In order for the analytics to correctly track, we must setup the TabLayout after setting up the ViewPager
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (mLastAction == Enums.View.ViewPagerActionTypes.RESET) {
                    mLastAction = Enums.View.ViewPagerActionTypes.SELECT;

                    switch (tab.getPosition()) {
                        case AboutViewPagerAdapter.ABOUT_INFO_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, INFO_TAB_PRESSED);
                            break;
                        case AboutViewPagerAdapter.ABOUT_LICENSE_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, LICENSE_TAB_PRESSED);
                            break;
                        case AboutViewPagerAdapter.ABOUT_PRIVACY_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, PRIVACY_TAB_PRESSED);
                            break;
                        case AboutViewPagerAdapter.ABOUT_LEGAL_NOTICES_FRAGMENT_INDEX:
                            mAnalyticsManager.logEvent(ABOUT, LEGAL_NOTICES_TAB_PRESSED);
                            break;
                    }

                } else {
                    mLastAction = Enums.View.ViewPagerActionTypes.RESET;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        mAdapter.setupTabTitles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(ABOUT);
    }

    public View bindViews() {
        ActivityAboutBinding binding = ActivityAboutBinding.inflate(getLayoutInflater());

        mToolbar = binding.aboutToolbar;
        mTabLayout = binding.aboutTabLayout;
        mViewPager = binding.aboutViewPager;
        mBackArrowView = binding.backArrowInclude.backArrowView;
        mAboutTitleTextView = binding.aboutTitleTextView;

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }
}

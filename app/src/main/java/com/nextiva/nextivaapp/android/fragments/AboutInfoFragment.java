/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;


import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ABOUT_INFO;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.databinding.FragmentAboutInfoBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager;
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager;

import java.util.Calendar;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 */

@AndroidEntryPoint
public class AboutInfoFragment extends BaseFragment {

    protected TextView mVersionTextView;
    protected TextView mHeaderTextView;
    protected TextView mCopyRightTextView;

    @Inject
    protected IntentManager mIntentManager;
    @Inject
    protected SharedPreferencesManager mSharedPreferencesManager;
    @Inject
    protected SessionManager mSessionManager;

    private int mDeveloperModeClickCounter = 0;

    public AboutInfoFragment() {
        // Required empty public constructor
    }

    public static AboutInfoFragment newInstance() {
        Bundle args = new Bundle();

        AboutInfoFragment fragment = new AboutInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() == null) {
            return;
        }

    }

    private void setCopyRightText() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        mCopyRightTextView.setText(getString(R.string.about_copyright_string, String.valueOf(year)));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = bindViews(inflater, container);

        if (getActivity() != null) {
            mHeaderTextView.setText(getString(R.string.about_head_text, getString(R.string.app_name)));
            mVersionTextView.setText(getString(R.string.about_version_string, BuildConfig.VERSION_NAME));
            setCopyRightText();

            if (mSessionManager.getUserDetails() == null &&
                    !mSharedPreferencesManager.getBoolean(SharedPreferencesManager.ENDPOINTS_AND_CREDENTIALS_ENABLED, false)) {
                mVersionTextView.setOnClickListener(clickView -> {
                    mDeveloperModeClickCounter += 1;

                    if (mDeveloperModeClickCounter == 5) {
                        mSharedPreferencesManager.setBoolean(SharedPreferencesManager.ENDPOINTS_AND_CREDENTIALS_ENABLED, true);
                        Toast.makeText(getActivity(), getString(R.string.about_developer_mode), Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                    }
                });
            }
        }

        return view;
    }

    public View bindViews(LayoutInflater inflater, ViewGroup container) {
        FragmentAboutInfoBinding binding = FragmentAboutInfoBinding.inflate(inflater, container, false);

        mVersionTextView = binding.aboutInfoVersionTextView;
        mHeaderTextView = binding.aboutInfoHeaderTextView;
        mCopyRightTextView = binding.aboutInfoCopyrightTextView;

        // Click Listeners

        binding.aboutInfoUrlTextView.setOnClickListener(v -> {
            if (getActivity() == null) {
                return;
            }
            mIntentManager.showUrl(getActivity(), ABOUT_INFO, getString(R.string.about_url));
        });

        return binding.getRoot();
    }
}

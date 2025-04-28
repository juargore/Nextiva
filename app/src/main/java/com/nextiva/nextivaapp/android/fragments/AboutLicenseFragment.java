/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.managers.interfaces.IntentManager;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AboutLicenseFragment extends BaseFragment {

    @Inject
    protected IntentManager mIntentManager;

    public AboutLicenseFragment() {
        // Required empty public constructor
    }

    public static AboutLicenseFragment newInstance() {
        Bundle args = new Bundle();

        AboutLicenseFragment fragment = new AboutLicenseFragment();
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about_license, container, false);
    }
}

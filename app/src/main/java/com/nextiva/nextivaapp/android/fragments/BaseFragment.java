/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nextiva.nextivaapp.android.listeners.ToolbarListener;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.util.LogUtil;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by adammacdonald on 2/5/18.
 */
@AndroidEntryPoint
public class BaseFragment extends Fragment {

    @Inject
    protected DialogManager mDialogManager;

    protected ToolbarListener mToolbarListener;
    protected final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtil.log("onAttach: " + this.getClass().getSimpleName());

        try {
            mToolbarListener = (ToolbarListener) context;
        } catch (ClassCastException e) {
            LogUtil.log(context.getClass().getSimpleName() + " is probably meant to implement ToolbarListener.");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.log("onCreate: " + this.getClass().getSimpleName());

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.log("onCreateView: " + this.getClass().getSimpleName());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtil.log("onStart: " + this.getClass().getSimpleName());
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtil.log("onResume: " + this.getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtil.log("onPause: " + this.getClass().getSimpleName());

        mCompositeDisposable.clear();

        mDialogManager.dismissAllDialogs();
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtil.log("onStop: " + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.log("onDestroyView: " + this.getClass().getSimpleName());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.log("onDestroy: " + this.getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtil.log("onDetach: " + this.getClass().getSimpleName());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        LogUtil.log("onCreateOptionsMenu: " + this.getClass().getSimpleName());
    }
}

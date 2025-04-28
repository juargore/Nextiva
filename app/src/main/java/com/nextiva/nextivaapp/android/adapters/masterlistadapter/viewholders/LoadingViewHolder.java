/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.LoadingListItem;

/**
 * Created by adammacdonald on 2/9/18.
 */

public class LoadingViewHolder extends BaseViewHolder<LoadingListItem> {

    public LoadingViewHolder(@NonNull ViewGroup parent, @NonNull Context context) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_loading, parent, false), context, null);
    }

    @Override
    public void bind(@NonNull LoadingListItem listItem) {
        // Do nothing
    }
}

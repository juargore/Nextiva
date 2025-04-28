/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;

/**
 * Created by adammacdonald on 2/9/18.
 */

public abstract class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    @NonNull
    final
    protected Context mContext;
    @Nullable
    final
    protected MasterListListener mMasterListListener;
    protected T mListItem;

    public BaseViewHolder(@NonNull View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView);
        mContext = context;
        mMasterListListener = masterListListener;
    }

    public abstract void bind(@NonNull T listItem);

    /**
     * Removed the ItemView from its parent.  This should be called when
     * Binding the ViewHolder.  If this is not called it can cause crashes
     * when the app is cleaning up the RecyclerView which has ItemViews
     * which are tied to a parent.
     */
    protected void removeItemViewFromParent() {
        if (itemView.getParent() != null && itemView.getParent() instanceof ViewGroup) {
            ((ViewGroup) itemView.getParent()).removeView(itemView);
        }
    }
}

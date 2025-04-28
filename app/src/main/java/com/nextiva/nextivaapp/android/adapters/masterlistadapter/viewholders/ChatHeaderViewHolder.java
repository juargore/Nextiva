/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.ChatHeaderListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemChatHeaderBinding;

/**
 * Created by adammacdonald on 3/4/18.
 */

public class ChatHeaderViewHolder extends BaseViewHolder<ChatHeaderListItem> {

    protected TextView mTitleTextView;

    public ChatHeaderViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_header, parent, false),
             context,
             masterListListener);
    }

    private ChatHeaderViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
    }

    @Override
    public void bind(@NonNull ChatHeaderListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mTitleTextView.setText(mListItem.getData());
    }

    private void bindViews(View view) {
        ListItemChatHeaderBinding binding = ListItemChatHeaderBinding.bind(view);

        mTitleTextView = binding.listItemChatHeaderTitleTextView;
    }
}

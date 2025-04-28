/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.DetailItemViewListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemDetailItemViewBinding;
import com.nextiva.nextivaapp.android.view.DetailItemView;

/**
 * Created by joedephillipo on 2/22/18.
 */

public class DetailItemViewViewHolder extends BaseViewHolder<DetailItemViewListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private final View mMasterItemView;
    protected DetailItemView mDetailItemView;

    public DetailItemViewViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_detail_item_view, parent, false),
             context,
             masterListListener);
    }

    DetailItemViewViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
        mMasterItemView = itemView;

        mMasterItemView.setOnClickListener(this);
        mMasterItemView.setOnLongClickListener(this);
        mDetailItemView.setOnAction1ClickListener(this);
        mDetailItemView.setOnAction2ClickListener(this);
    }

    @Override
    public void bind(@NonNull DetailItemViewListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;
        mListItem.setItemView(mDetailItemView);

        mMasterItemView.setClickable(mListItem.isClickable());
        mMasterItemView.setLongClickable(mListItem.isLongClickable());
        mDetailItemView.setTitleText(mListItem.getTitle());

        if (!TextUtils.isEmpty(mListItem.getSubTitle())) {
            mDetailItemView.setSubTitleEnabled(true);
            mDetailItemView.setSubTitleText(mListItem.getSubTitle());

        } else {
            mDetailItemView.setSubTitleEnabled(false);
        }

        if (mListItem.getActionButtonOneResId() != 0) {
            mDetailItemView.setAction1Drawable(mListItem.getActionButtonOneResId());
            mDetailItemView.setAction1Visible(true);

        } else {
            mDetailItemView.setAction1Visible(false);
        }

        if (mListItem.getActionButtonTwoResId() != 0) {
            mDetailItemView.setAction2Visible(true);
            mDetailItemView.setAction2Drawable(mListItem.getActionButtonTwoResId());

        } else {
            mDetailItemView.setAction2Visible(false);
        }

        setContentDescriptions();
    }

    private void setContentDescriptions() {
        mMasterItemView.setContentDescription(mContext.getString(R.string.detail_list_item_content_description, mDetailItemView.getTitleText()));
        mDetailItemView.setContentDescriptions(mContext);
    }

    private void bindViews(View view) {
        ListItemDetailItemViewBinding binding = ListItemDetailItemViewBinding.bind(view);

        mDetailItemView = binding.listItemDetailItemView;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            if (v.getId() == R.id.detail_item_view_action1_image_button) {
                mMasterListListener.onDetailItemViewListItemAction1ButtonClicked(mListItem);

            } else if (v.getId() == R.id.detail_item_view_action2_image_button) {
                mMasterListListener.onDetailItemViewListItemAction2ButtonClicked(mListItem);

            } else {
                mMasterListListener.onDetailItemViewListItemClicked(mListItem);
            }
        }
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // View.OnLongClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean onLongClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onDetailItemViewListItemLongClicked(mListItem);
        }

        return true;
    }
    // --------------------------------------------------------------------------------------------
}

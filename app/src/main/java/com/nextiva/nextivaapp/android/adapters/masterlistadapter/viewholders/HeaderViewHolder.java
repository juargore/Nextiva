package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.HeaderListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemGroupHeaderBinding;

/**
 * Created by Thaddeus Dannar on 2/28/18.
 */

public class HeaderViewHolder extends BaseViewHolder<HeaderListItem> implements
        View.OnClickListener,
        View.OnLongClickListener {

    private final View mMasterItemView;
    protected TextView mHeaderTextView;
    protected ImageSwitcher mHeaderArrowImageSwitcher;

    public HeaderViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_group_header, parent, false),
             context,
             masterListListener);
    }

    private HeaderViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);

        mMasterItemView = itemView;
        mMasterItemView.setOnClickListener(this);
        mMasterItemView.setOnLongClickListener(this);
    }

    @Override
    public void bind(@NonNull HeaderListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        mMasterItemView.setClickable(mListItem.isClickable());
        mMasterItemView.setLongClickable(mListItem.isListItemLongClickable());

        mHeaderTextView.setText(mListItem.getData().getTitle());
        mHeaderTextView.setContentDescription(mContext.getString(R.string.contacts_list_group_content_description, mListItem.getData().getTitle()));

        if (mListItem.getBaseListItemsList() != null && !mListItem.getBaseListItemsList().isEmpty()) {
            mHeaderArrowImageSwitcher.getCurrentView().setScaleY(mListItem.isExpanded() ? 1 : -1);
            mHeaderArrowImageSwitcher.setVisibility(View.VISIBLE);

        } else {
            mHeaderArrowImageSwitcher.setVisibility(View.GONE);
        }
    }

    private void bindViews(View view) {
        ListItemGroupHeaderBinding binding = ListItemGroupHeaderBinding.bind(view);

        mHeaderTextView = binding.listItemGroupHeaderTextView;
        mHeaderArrowImageSwitcher = binding.listItemGroupHeaderArrowImageSwitcher;
    }

    // --------------------------------------------------------------------------------------------
    // View.OnClickListener Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public void onClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mListItem.setExpanded(!mListItem.isExpanded());
            mHeaderArrowImageSwitcher.getCurrentView().setScaleY(mListItem.isExpanded() ? 1 : -1);
            mMasterListListener.onContactHeaderListItemClicked(mListItem);
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if (mMasterListListener != null && mListItem != null) {
            mMasterListListener.onContactHeaderListItemLongClicked(mListItem);
        }

        return true;
    }
}

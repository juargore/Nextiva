package com.nextiva.nextivaapp.android.features.messaging.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.MasterListListener;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.MessageHeaderListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.BaseViewHolder;
import com.nextiva.nextivaapp.android.databinding.ListItemConnectChatHeaderBinding;

public class ConversationHeaderViewHolder extends BaseViewHolder<MessageHeaderListItem> {

    protected TextView mTitleTextView;

    public ConversationHeaderViewHolder(@NonNull ViewGroup parent, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        this(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_connect_chat_header, parent, false),
                context,
                masterListListener);
    }

    private ConversationHeaderViewHolder(View itemView, @NonNull Context context, @Nullable MasterListListener masterListListener) {
        super(itemView, context, masterListListener);
        bindViews(itemView);
    }

    @Override
    public void bind(@NonNull MessageHeaderListItem listItem) {
        removeItemViewFromParent();

        mListItem = listItem;

        String title = mListItem.getData();
        if (mListItem.getConnectData() != null) {
            title = mListItem.getConnectData();
        }
        mTitleTextView.setText(title);
    }

    private void bindViews(View view) {
        ListItemConnectChatHeaderBinding binding = ListItemConnectChatHeaderBinding.bind(view);

        mTitleTextView = binding.listItemChatHeaderTitleTextView;
    }
}

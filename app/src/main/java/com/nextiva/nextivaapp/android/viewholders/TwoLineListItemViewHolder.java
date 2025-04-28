package com.nextiva.nextivaapp.android.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.R;

/**
 * Created by Thaddeus Dannar on 2019-09-04.
 */
public class TwoLineListItemViewHolder extends RecyclerView.ViewHolder {
    public final TextView mTitleTextView;
    public final TextView mSubTitleTextView;

    public TwoLineListItemViewHolder(View view) {
        super(view);
        mTitleTextView = view.findViewById(R.id.list_item_title_text_view);
        mSubTitleTextView = view.findViewById(R.id.list_item_sub_title_text_view);
    }
}

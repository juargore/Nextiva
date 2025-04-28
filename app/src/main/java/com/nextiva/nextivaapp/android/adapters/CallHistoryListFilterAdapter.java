/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.nextiva.nextivaapp.android.R;

/**
 * Created by Thaddeus Dannar on 2/15/18.
 */

public class CallHistoryListFilterAdapter extends ArrayAdapter<Integer> {

    public static final int FILTER_ALL = 0;
    public static final int FILTER_MISSED = 1;

    public CallHistoryListFilterAdapter(Context context) {
        super(context,
              R.layout.list_item_filter,
              new Integer[] {FILTER_ALL, FILTER_MISSED});
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        return getListItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getListItemView(position, convertView, parent);
    }

    private View getListItemView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (getItem(position) != null) {
            Integer filter = getItem(position);

            if (filter != null) {

                CallHistoryFilterViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_call_history_filter, parent, false);
                    viewHolder = new CallHistoryFilterViewHolder();
                    viewHolder.imageView = convertView.findViewById(R.id.call_history_filter_image_view);
                    viewHolder.textView = convertView.findViewById(R.id.call_history_filter_name_text_view);
                    viewHolder.selectedRowImageView = convertView.findViewById(R.id.call_history_filter_selected_row_image_view);
                    convertView.setTag(viewHolder);

                } else {
                    viewHolder = (CallHistoryFilterViewHolder) convertView.getTag();
                }

                switch (filter) {
                    case FILTER_ALL: {
                        viewHolder.imageView.setImageResource(R.drawable.ic_call_all);
                        viewHolder.imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.nextivaOrange));
                        viewHolder.textView.setText(R.string.call_history_list_filter_all);
                        break;
                    }
                    case FILTER_MISSED: {
                        viewHolder.imageView.setImageResource(R.drawable.ic_call_missed);
                        viewHolder.imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.statusBusyRed));
                        viewHolder.textView.setText(R.string.call_history_list_filter_missed);
                        break;
                    }
                }
            }
        }
        return convertView;
    }

    @SuppressWarnings("unused")
    private static class CallHistoryFilterViewHolder {
        ImageView imageView;
        TextView textView;
        ImageView selectedRowImageView;
    }
}

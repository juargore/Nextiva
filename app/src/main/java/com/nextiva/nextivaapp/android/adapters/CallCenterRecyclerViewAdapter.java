package com.nextiva.nextivaapp.android.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallCenterListItem;
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders.CallCenterStatusViewHolder;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Thaddeus Dannar on 11/7/20.
 */
public class CallCenterRecyclerViewAdapter extends RecyclerView.Adapter<CallCenterStatusViewHolder> {

    private final List<CallCenterListItem> mCallCenterListItems;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final ArrayList<CallCenterStatusViewHolder> mCallCenterStatusViewHolders = new ArrayList<>();
    private final Context mContext;
    private final SettingsManager mSettingsManager;

    @Inject
    public CallCenterRecyclerViewAdapter(Context context, List<CallCenterListItem> callCenterListItems, SettingsManager settingsManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mCallCenterListItems = callCenterListItems;
        this.mContext = context;
        this.mSettingsManager = settingsManager;
    }

    @NonNull
    @Override
    public CallCenterStatusViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View view = mInflater.inflate(R.layout.list_item_two_line_checkbox, parent, false);
        return new CallCenterStatusViewHolder(view, mContext, mSettingsManager);
    }

    @Override
    public void onBindViewHolder(@NonNull final CallCenterStatusViewHolder holder, final int position) {
        holder.bind(mCallCenterListItems.get(position));
        mCallCenterStatusViewHolders.add(holder);
        LogUtil.d("onBindViewHolder: " + mCallCenterListItems.get(position).getTitle());
        LogUtil.d("onBindViewHolder: " + mCallCenterListItems.get(position).getSubTitle());
    }

    @Override
    public int getItemCount() {
        return mCallCenterListItems.size();
    }

    CallCenterListItem getItem(int id) {
        return mCallCenterListItems.get(id);
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
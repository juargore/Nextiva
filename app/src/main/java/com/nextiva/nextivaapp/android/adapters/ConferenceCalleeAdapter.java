package com.nextiva.nextivaapp.android.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.models.ConferenceCallee;
import com.nextiva.nextivaapp.android.viewholders.TwoLineListItemViewHolder;

import java.util.List;

/**
 * Created by Thaddeus Dannar on 2019-09-04.
 */
public class ConferenceCalleeAdapter extends RecyclerView.Adapter<TwoLineListItemViewHolder> {
    private final List<ConferenceCallee> mConferenceCalleesList;

    public ConferenceCalleeAdapter(List<ConferenceCallee> conferenceCalleesList) {
        mConferenceCalleesList = conferenceCalleesList;
    }

    @NonNull
    @Override
    public TwoLineListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TwoLineListItemViewHolder(LayoutInflater.from(parent.getContext())
                                                      .inflate(R.layout.list_item_two_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TwoLineListItemViewHolder twoLineListItemViewHolder, int position) {
        twoLineListItemViewHolder.mTitleTextView.setText(mConferenceCalleesList.get(position).getName());
        twoLineListItemViewHolder.mSubTitleTextView.setText(mConferenceCalleesList.get(position).getNumber());
    }

    @Override
    public int getItemCount() {
        return mConferenceCalleesList.size();
    }
}

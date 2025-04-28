package com.nextiva.nextivaapp.android.adapters.masterlistadapter.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.CallCenterListItem;
import com.nextiva.nextivaapp.android.databinding.ListItemTwoLineCheckboxBinding;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterList;
import com.nextiva.nextivaapp.android.util.GsonUtil;


public class CallCenterStatusViewHolder extends BaseViewHolder<CallCenterListItem> {

    protected TextView mTitleTextView;
    protected TextView mSubTitleTextView;
    protected CheckBox mCheckBox;

    private SettingsManager mSettingsManager;
    private BroadsoftCallCenter mBroadsoftCallCenter;

    CheckBox.OnCheckedChangeListener mCheckedChangeListener = (buttonView, isChecked) -> {
        mBroadsoftCallCenter = GsonUtil.getObject(BroadsoftCallCenter.class, mSettingsManager.getCallCenterStatus());
        if (mBroadsoftCallCenter != null && mBroadsoftCallCenter.getBroadsoftCallCenterList() != null && !mBroadsoftCallCenter.getBroadsoftCallCenterList().isEmpty()) {
            for (BroadsoftCallCenterList broadsoftCallCenterList : mBroadsoftCallCenter.getBroadsoftCallCenterList()) {
                if (broadsoftCallCenterList.getServiceUserId().equals(mTitleTextView.getText()))
                    broadsoftCallCenterList.setAvailable(isChecked);
            }
            mSettingsManager.setCallCenterStatus(GsonUtil.getJSON(mBroadsoftCallCenter));
        }
    };

    public CallCenterStatusViewHolder(View itemView, @NonNull Context context, SettingsManager settingsManager) {
        super(itemView, context, null);
        this.mSettingsManager = settingsManager;
        bindViews(itemView);
    }

    @Override
    public void bind(@NonNull CallCenterListItem listItem) {
        removeItemViewFromParent();
        mBroadsoftCallCenter = GsonUtil.getObject(BroadsoftCallCenter.class, mSettingsManager.getCallCenterStatus());

        mListItem = listItem;
        mTitleTextView.setText(mListItem.getTitle());
        mSubTitleTextView.setText(mListItem.getSubTitle());
        mCheckBox.setChecked(mListItem.getIsJoined());
        mCheckBox.setEnabled(mListItem.getCheckboxEnabled());

        mCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    private void bindViews(View view) {
        ListItemTwoLineCheckboxBinding binding = ListItemTwoLineCheckboxBinding.bind(view);

        mTitleTextView = binding.listItemTitleTextView;
        mSubTitleTextView = binding.listItemSubTitleTextView;
        mCheckBox = binding.listItemCheckbox;
    }
}
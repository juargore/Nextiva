package com.nextiva.nextivaapp.android.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.FeatureFlagListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.models.net.platform.featureFlags.FeatureFlag
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FeatureFlagsViewModel @Inject constructor(application: Application, var sessionManager: SessionManager) : BaseViewModel(application) {

    private var baseListItemsLiveData: MutableLiveData<ArrayList<BaseListItem>?> = MutableLiveData()

    private val featureFlags: Array<String>? = application.resources.getStringArray(R.array.feature_flags)

    fun loadListItems() {
        if (featureFlags.isNullOrEmpty()) {
            baseListItemsLiveData.value = null

        } else {
            val savedFeatureFlags = sessionManager.featureFlags?.featureFlags

            val listItems: ArrayList<BaseListItem> = ArrayList()
            var savedFeatureFlag: FeatureFlag?

            for (featureFlag in featureFlags) {
                savedFeatureFlag = savedFeatureFlags?.firstOrNull { it.name == featureFlag }
                listItems.add(FeatureFlagListItem(featureFlag,
                        when (savedFeatureFlag?.isEnabled) {
                            false -> Enums.Platform.FeatureFlagState.DISABLED
                            true -> Enums.Platform.FeatureFlagState.ENABLED
                            else -> Enums.Platform.FeatureFlagState.NOT_FOUND
                        },
                        savedFeatureFlag?.isManuallyDisabled ?: false))
            }

            baseListItemsLiveData.value = listItems
        }
    }

    fun setFeatureFlagDisabled(listItem: FeatureFlagListItem) {
        sessionManager.featureFlags?.let { featureFlags ->
            featureFlags.featureFlags?.firstOrNull { it.name == listItem.data }?.let {
                it.isManuallyDisabled = listItem.manuallyDisabled
            }

            sessionManager.featureFlags = featureFlags
        }
    }

    fun getListItemLiveData(): LiveData<ArrayList<BaseListItem>?> {
        return baseListItemsLiveData
    }
}
package com.nextiva.nextivaapp.android.managers.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.BaseListItem;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.ServiceSettings;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenter;
import com.nextiva.nextivaapp.android.models.net.broadsoft.services.BroadsoftCallCenterUnavailableCodes;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface CallSettingsManager {

    LiveData<Resource<List<BaseListItem>>> getListItemsListLiveData();

    Disposable getFilteredServiceSettings();

    void clearServiceSettings();

    void processServiceSettings();

    void putServiceSetting(@NonNull @Enums.Service.Type String type, @Nullable ServiceSettings serviceSettings);

    void setBroadsoftCallCenter(BroadsoftCallCenter broadsoftCallCenter);

    BroadsoftCallCenter getBroadsoftCallCenter();

    void setBroadsoftCallCenterUnavailableCodes(BroadsoftCallCenterUnavailableCodes broadsoftCallCenterUnavailableCodes);

    BroadsoftCallCenterUnavailableCodes getBroadsoftCallCenterUnavailableCodes();
}

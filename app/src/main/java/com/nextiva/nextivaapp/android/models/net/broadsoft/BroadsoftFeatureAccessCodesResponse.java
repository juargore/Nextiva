package com.nextiva.nextivaapp.android.models.net.broadsoft;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root
public class BroadsoftFeatureAccessCodesResponse {

    @ElementList(name = "featureAccessCode", inline = true, required = false)
    private ArrayList<BroadsoftFeatureAccessCode> mBroadsoftFeatureAccessCodes;

    public BroadsoftFeatureAccessCodesResponse() {
    }

    public BroadsoftFeatureAccessCodesResponse(ArrayList<BroadsoftFeatureAccessCode> broadsoftFeatureAccessCodes) {
        mBroadsoftFeatureAccessCodes = broadsoftFeatureAccessCodes;
    }

    public ArrayList<BroadsoftFeatureAccessCode> getBroadsoftFeatureAccessCodes() {
        return mBroadsoftFeatureAccessCodes;
    }

    public void setBroadsoftFeatureAccessCodes(ArrayList<BroadsoftFeatureAccessCode> broadsoftFeatureAccessCodes) {
        mBroadsoftFeatureAccessCodes = broadsoftFeatureAccessCodes;
    }
}

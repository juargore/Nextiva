package com.nextiva.nextivaapp.android.models.net.broadsoft;

import com.nextiva.nextivaapp.android.constants.Enums;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name = "featureAccessCode", strict = false)
public class BroadsoftFeatureAccessCode {

    @Element(name = "code", required = false)
    @Enums.Service.FeatureAccessCodes.FeatureAccessCode
    private String mCode;
    @Element(name = "codeName", required = false)
    private String mCodeName;

    public BroadsoftFeatureAccessCode() {
    }

    public BroadsoftFeatureAccessCode(String code, String codeName) {
        mCode = code;
        mCodeName = codeName;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getCodeName() {
        return mCodeName;
    }

    public void setCodeName(String codeName) {
        mCodeName = codeName;
    }
}

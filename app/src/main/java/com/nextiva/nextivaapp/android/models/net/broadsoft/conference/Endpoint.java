package com.nextiva.nextivaapp.android.models.net.broadsoft.conference;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 2020-02-19.
 */
@Root(name = "endpoint", strict = false)
public class Endpoint {

    @Nullable
    @Element(name = "addressOfRecord", required = false)
    private String mAddressOfRecord;

    public Endpoint() {
    }

    @Nullable
    public String getAddressOfRecord() {
        return mAddressOfRecord;
    }

    public void setAddressOfRecord(@Nullable final String addressOfRecord) {
        mAddressOfRecord = addressOfRecord;
    }
}

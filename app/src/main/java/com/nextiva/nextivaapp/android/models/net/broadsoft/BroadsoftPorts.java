package com.nextiva.nextivaapp.android.models.net.broadsoft;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 5/1/20.
 */
@Root(name = "ports", strict = false)
public class BroadsoftPorts {


    @Nullable
    @Element(name = "quantity", required = false)
    private String mQuantity;

    public BroadsoftPorts() {
    }

    @Nullable
    public String getQuantity() {
        return mQuantity;
    }

    public void setQuantity(@Nullable final String quantity) {
        mQuantity = quantity;
    }
}

package com.nextiva.nextivaapp.android.models;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Order;
import org.simpleframework.xml.Root;

/**
 * Created by Thaddeus Dannar on 11/9/20.
 */
@Order(elements = {"serviceUserId", "available"})
@Root(name = "callCenterDetails", strict = false)
public class CallCenterDetailModel {
    @Element(name = "serviceUserId")
    public String serviceUserId;
    @Element(name = "available")
    public Boolean available;
    public Boolean isLogOffAllowed;


    public String getServiceUserId() {
        return serviceUserId;
    }

    public void setServiceUserId(final String serviceUserId) {
        this.serviceUserId = serviceUserId;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(final Boolean available) {
        this.available = available;
    }

    public Boolean getLogOffAllowed() {
        return isLogOffAllowed;
    }

    public void setLogOffAllowed(final Boolean logOffAllowed) {
        isLogOffAllowed = logOffAllowed;
    }
}

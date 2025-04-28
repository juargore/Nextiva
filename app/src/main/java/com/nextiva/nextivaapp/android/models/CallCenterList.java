package com.nextiva.nextivaapp.android.models;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Thaddeus Dannar on 11/9/20.
 */
@Root(name="callCenterList", strict = false)
public class CallCenterList {

    @ElementList(required=false)
    List<CallCenterDetailModel> callCenterDetails;

    public CallCenterList() {
    }

    public List<CallCenterDetailModel> getCallCenterDetails() {
        return callCenterDetails;
    }

    public void setCallCenterDetails(final List<CallCenterDetailModel> callCenterDetails) {
        this.callCenterDetails = callCenterDetails;
    }
}

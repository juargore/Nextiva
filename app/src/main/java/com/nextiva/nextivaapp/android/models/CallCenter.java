package com.nextiva.nextivaapp.android.models;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 11/9/20.
 */

@Root(name = "CallCenter", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class CallCenter implements Serializable {

    @Nullable
    @Element(name = "agentACDState", required = false)
    public String agentACDState;

    @Nullable
    @Element(name = "agentUnavailableCode", required = false)
    public String agentUnavailableCode;

    @Nullable
    @ElementList(name = "callCenterList", required = false)
    public ArrayList<CallCenterDetailModel> callCenterList;

    public CallCenter() {
    }


    @Nullable
    public String getAgentACDState() {
        return agentACDState;
    }

    public void setAgentACDState(@Nullable final String agentACDState) {
        this.agentACDState = agentACDState;
    }

    public String getAgentUnavailableCode() {
        return agentUnavailableCode;
    }

    public void setAgentUnavailableCode(final String agentUnavailableCode) {
        this.agentUnavailableCode = agentUnavailableCode;
    }


    @Nullable
    public ArrayList<CallCenterDetailModel> getCallCenterList() {
        return callCenterList;
    }

    public void setCallCenterList(@Nullable final ArrayList<CallCenterDetailModel> callCenterList) {
        this.callCenterList = callCenterList;
    }
}

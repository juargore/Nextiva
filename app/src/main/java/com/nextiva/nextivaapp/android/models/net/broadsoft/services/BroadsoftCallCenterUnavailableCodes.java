package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 11/8/20.
 */

@Root(name = "ACDAgentUnavailableCodes", strict = false)
public class BroadsoftCallCenterUnavailableCodes {

    @Nullable
    @Element(name = "enableAgentUnavailableCodes", required = false)
    private Boolean enableAgentUnavailableCodes;

    @Nullable
    @Element(name = "defaultAgentUnavailableCodeOnDND", required = false)
    private String defaultAgentUnavailableCodeOnDND;

    @Nullable
    @Element(name = "defaultAgentUnavailableCodeOnPersonalCalls", required = false)
    private String defaultAgentUnavailableCodeOnPersonalCalls;

    @Nullable
    @Element(name = "defaultAgentUnavailableCodeOnConsecutiveBounces", required = false)
    private String defaultAgentUnavailableCodeOnConsecutiveBounces;

    @Nullable
    @Element(name = "forceUseOfAgentUnavailableCodes", required = false)
    private String forceUseOfAgentUnavailableCodes;

    @Nullable
    @ElementList(name = "unavailableCodes", required = false)
    private ArrayList<BroadsoftUnavailableDetail> unavailableCodes;

    @Nullable
    public Boolean getEnableAgentUnavailableCodes() {
        return enableAgentUnavailableCodes;
    }

    public void setEnableAgentUnavailableCodes(@Nullable final Boolean enableAgentUnavailableCodes) {
        this.enableAgentUnavailableCodes = enableAgentUnavailableCodes;
    }

    @Nullable
    public String getDefaultAgentUnavailableCodeOnDND() {
        return defaultAgentUnavailableCodeOnDND;
    }

    public void setDefaultAgentUnavailableCodeOnDND(@Nullable final String defaultAgentUnavailableCodeOnDND) {
        this.defaultAgentUnavailableCodeOnDND = defaultAgentUnavailableCodeOnDND;
    }

    @Nullable
    public String getDefaultAgentUnavailableCodeOnPersonalCalls() {
        return defaultAgentUnavailableCodeOnPersonalCalls;
    }

    public void setDefaultAgentUnavailableCodeOnPersonalCalls(@Nullable final String defaultAgentUnavailableCodeOnPersonalCalls) {
        this.defaultAgentUnavailableCodeOnPersonalCalls = defaultAgentUnavailableCodeOnPersonalCalls;
    }

    @Nullable
    public String getDefaultAgentUnavailableCodeOnConsecutiveBounces() {
        return defaultAgentUnavailableCodeOnConsecutiveBounces;
    }

    public void setDefaultAgentUnavailableCodeOnConsecutiveBounces(@Nullable final String defaultAgentUnavailableCodeOnConsecutiveBounces) {
        this.defaultAgentUnavailableCodeOnConsecutiveBounces = defaultAgentUnavailableCodeOnConsecutiveBounces;
    }

    @Nullable
    public String getForceUseOfAgentUnavailableCodes() {
        return forceUseOfAgentUnavailableCodes;
    }

    public void setForceUseOfAgentUnavailableCodes(@Nullable final String forceUseOfAgentUnavailableCodes) {
        this.forceUseOfAgentUnavailableCodes = forceUseOfAgentUnavailableCodes;
    }

    @Nullable
    public ArrayList<BroadsoftUnavailableDetail> getUnavailableCodes() {
        return unavailableCodes;
    }

    public void setUnavailableCodes(@Nullable final ArrayList<BroadsoftUnavailableDetail> unavailableCodes) {
        this.unavailableCodes = unavailableCodes;
    }
}

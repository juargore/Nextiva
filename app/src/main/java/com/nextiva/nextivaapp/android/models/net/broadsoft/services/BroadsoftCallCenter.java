package com.nextiva.nextivaapp.android.models.net.broadsoft.services;

import androidx.annotation.Nullable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

/**
 * Created by Thaddeus Dannar on 11/6/20.
 */

@Root(name = "CallCenter", strict = false)
@Namespace(reference = "http://schema.broadsoft.com/xsi")
public class BroadsoftCallCenter {

    @Nullable
    @Element(name = "agentACDState", required = false)
    private String agentACDState;

    @Nullable
    @Element(name = "agentUnavailableCode", required = false)
    private String agentUnavailableCode;

    @Nullable
    @Element(name = "useDefaultGuardTimer", required = false)
    private Boolean useDefaultGuardTimer;

    @Nullable
    @Element(name = "enableGuardTimer", required = false)
    private Boolean enableGuardTimer;

    @Nullable
    @Element(name = "guardTimerSeconds", required = false)
    private int guardTimerSeconds;

    @Nullable
    @Element(name = "useSystemDefaultUnavailableSettings", required = false)
    private Boolean useSystemDefaultUnavailableSettings;

    @Nullable
    @Element(name = "forceAgentUnavailableOnDNDActivation", required = false)
    private Boolean forceAgentUnavailableOnDNDActivation;

    @Nullable
    @Element(name = "forceUnavailableOnPersonalCalls", required = false)
    private Boolean forceUnavailableOnPersonalCalls;

    @Nullable
    @Element(name = "forceAgentUnavailableOnBouncedCallLimit", required = false)
    private Boolean forceAgentUnavailableOnBouncedCallLimit;

    @Nullable
    @Element(name = "numberConsecutiveBouncedCallsToForceAgentUnavailable", required = false)
    private int numberConsecutiveBouncedCallsToForceAgentUnavailable;

    @Nullable
    @Element(name = "makeOutgoingCallsAsCallCenter", required = false)
    private Boolean makeOutgoingCallsAsCallCenter;

    @Nullable
    @ElementList(name = "callCenterList", required = false)
    private ArrayList<BroadsoftCallCenterList> callCenterList;


    public BroadsoftCallCenter() {
    }

    public BroadsoftCallCenter(final BroadsoftCallCenter broadsoftCallCenter) {
        this.setAgentACDState(broadsoftCallCenter.getAgentACDState());
        this.setAgentUnavailableCode(broadsoftCallCenter.getAgentUnavailableCode());
        this.setUseDefaultGuardTimer(broadsoftCallCenter.getUseDefaultGuardTimer());
        this.setEnableGuardTimer(broadsoftCallCenter.getEnableGuardTimer());
        this.setGuardTimerSeconds(broadsoftCallCenter.getGuardTimerSeconds());
        this.setUseSystemDefaultUnavailableSettings(broadsoftCallCenter.getUseSystemDefaultUnavailableSettings());
        this.setForceAgentUnavailableOnDNDActivation(broadsoftCallCenter.getForceAgentUnavailableOnDNDActivation());
        this.setForceUnavailableOnPersonalCalls(broadsoftCallCenter.getForceUnavailableOnPersonalCalls());
        this.setForceAgentUnavailableOnBouncedCallLimit(broadsoftCallCenter.getForceAgentUnavailableOnBouncedCallLimit());
        this.setNumberConsecutiveBouncedCallsToForceAgentUnavailable(broadsoftCallCenter.getNumberConsecutiveBouncedCallsToForceAgentUnavailable());
        this.setMakeOutgoingCallsAsCallCenter(broadsoftCallCenter.getMakeOutgoingCallsAsCallCenter());
        this.setBroadsoftCallCenterList(broadsoftCallCenter.callCenterList);
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
    public Boolean getUseDefaultGuardTimer() {
        return useDefaultGuardTimer;
    }

    public void setUseDefaultGuardTimer(@Nullable final Boolean useDefaultGuardTimer) {
        this.useDefaultGuardTimer = useDefaultGuardTimer;
    }

    @Nullable
    public Boolean getEnableGuardTimer() {
        return enableGuardTimer;
    }

    public void setEnableGuardTimer(@Nullable final Boolean enableGuardTimer) {
        this.enableGuardTimer = enableGuardTimer;
    }

    public int getGuardTimerSeconds() {
        return guardTimerSeconds;
    }

    public void setGuardTimerSeconds(final int guardTimerSeconds) {
        this.guardTimerSeconds = guardTimerSeconds;
    }

    @Nullable
    public Boolean getUseSystemDefaultUnavailableSettings() {
        return useSystemDefaultUnavailableSettings;
    }

    public void setUseSystemDefaultUnavailableSettings(@Nullable final Boolean useSystemDefaultUnavailableSettings) {
        this.useSystemDefaultUnavailableSettings = useSystemDefaultUnavailableSettings;
    }

    @Nullable
    public Boolean getForceAgentUnavailableOnDNDActivation() {
        return forceAgentUnavailableOnDNDActivation;
    }

    public void setForceAgentUnavailableOnDNDActivation(@Nullable final Boolean forceAgentUnavailableOnDNDActivation) {
        this.forceAgentUnavailableOnDNDActivation = forceAgentUnavailableOnDNDActivation;
    }

    @Nullable
    public Boolean getForceUnavailableOnPersonalCalls() {
        return forceUnavailableOnPersonalCalls;
    }

    public void setForceUnavailableOnPersonalCalls(@Nullable final Boolean forceUnavailableOnPersonalCalls) {
        this.forceUnavailableOnPersonalCalls = forceUnavailableOnPersonalCalls;
    }

    @Nullable
    public Boolean getForceAgentUnavailableOnBouncedCallLimit() {
        return forceAgentUnavailableOnBouncedCallLimit;
    }

    public void setForceAgentUnavailableOnBouncedCallLimit(@Nullable final Boolean forceAgentUnavailableOnBouncedCallLimit) {
        this.forceAgentUnavailableOnBouncedCallLimit = forceAgentUnavailableOnBouncedCallLimit;
    }

    public int getNumberConsecutiveBouncedCallsToForceAgentUnavailable() {
        return numberConsecutiveBouncedCallsToForceAgentUnavailable;
    }

    public void setNumberConsecutiveBouncedCallsToForceAgentUnavailable(final int numberConsecutiveBouncedCallsToForceAgentUnavailable) {
        this.numberConsecutiveBouncedCallsToForceAgentUnavailable = numberConsecutiveBouncedCallsToForceAgentUnavailable;
    }

    @Nullable
    public Boolean getMakeOutgoingCallsAsCallCenter() {
        return makeOutgoingCallsAsCallCenter;
    }

    public void setMakeOutgoingCallsAsCallCenter(@Nullable final Boolean makeOutgoingCallsAsCallCenter) {
        this.makeOutgoingCallsAsCallCenter = makeOutgoingCallsAsCallCenter;
    }

    @Nullable
    public ArrayList<BroadsoftCallCenterList> getBroadsoftCallCenterList() {
        return callCenterList;
    }

    public void setBroadsoftCallCenterList(@Nullable final ArrayList<BroadsoftCallCenterList> broadsoftCallCenterList) {
        callCenterList = broadsoftCallCenterList;
    }
}

package com.nextiva.nextivaapp.android.models;

public class ChatContactChipState {
    private NextivaContact mNextivaContact;
    private boolean isRemoveable;
    private boolean isChipChange;

    public ChatContactChipState(NextivaContact mNextivaContact, boolean isRemoveable, boolean isChipChange) {
        this.mNextivaContact = mNextivaContact;
        this.isRemoveable = isRemoveable;
        this.isChipChange = isChipChange;
    }

    public NextivaContact getmNextivaContact() {
        return mNextivaContact;
    }

    public void setmNextivaContact(NextivaContact mNextivaContact) {
        this.mNextivaContact = mNextivaContact;
    }

    public boolean isRemoveable() {
        return isRemoveable;
    }

    public void setRemoveable(boolean removeable) {
        isRemoveable = removeable;
    }

    public boolean isChipChange() {
        return isChipChange;
    }

    public void setChipChange(boolean chipChange) {
        isChipChange = chipChange;
    }
}

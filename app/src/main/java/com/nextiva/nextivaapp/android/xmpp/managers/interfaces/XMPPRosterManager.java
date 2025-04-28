/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers.interfaces;

import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by joedephillipo on 3/30/18.
 */

public interface XMPPRosterManager {

    void init(XMPPTCPConnection connection, CompositeDisposable compositeDisposable);

    void refreshRoster(boolean isUpdateFromContactDetail);

    void subscribeToContact(String jid);

    void unsubscribeFromContact(String jid);

    void acceptSubscriptionRequest(String jid);

    void declineSubscriptionRequest(String jid);

    void updateBroadsoftRosterContacts(ArrayList<NextivaContact> updatedContacts);

    void updateXMPPRosterContacts(ArrayList<NextivaContact> contactsAdded, @Enums.Contacts.UpdateActions.UpdateAction int actionType);

}

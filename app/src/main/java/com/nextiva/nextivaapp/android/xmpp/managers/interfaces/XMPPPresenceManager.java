/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.xmpp.managers.interfaces;

import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.models.NextivaContact;

import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.util.ArrayList;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by joedephillipo on 3/30/18.
 */

public interface XMPPPresenceManager {

    void init(XMPPTCPConnection connection, CompositeDisposable compositeDisposable);

    void setPresence(DbPresence nextivaPresence);

    void sendVCardUpdatePresence();

    void refreshPresences(ArrayList<String> jids);

    void refreshPresences(ArrayList<NextivaContact> contactList, ArrayList<String> jidList, boolean isUpdateFromContactDetails);

}

/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ContactManagementRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ContactManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.EnterpriseContactByNumberResponseEvent;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.CallUtil;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;

@Singleton
public class NextivaContactManager implements ContactManager {

    private final SchedulerProvider mSchedulerProvider;
    private final DbManager mDbManager;
    private final ContactManagementRepository mContactManagementRepository;

    @Inject
    public NextivaContactManager(SchedulerProvider schedulerProvider,
                                 DbManager dbManager,
                                 ContactManagementRepository contactManagementRepository) {

        mSchedulerProvider = schedulerProvider;
        mDbManager = dbManager;
        mContactManagementRepository = contactManagementRepository;
    }

    private NextivaContact searchContactsForMatch(@NonNull ArrayList<NextivaContact> contactList, String phoneNumber) {
        for (NextivaContact nextivaContact : contactList) {
            if (nextivaContact.getAllPhoneNumbers() != null) {
                for (PhoneNumber number : nextivaContact.getAllPhoneNumbers()) {
                    if (!TextUtils.isEmpty(number.getNumber()) &&
                            TextUtils.equals(CallUtil.getSearchFormattedPhoneNumber(number.getNumber()), CallUtil.getSearchFormattedPhoneNumber(phoneNumber))) {
                        return nextivaContact;
                    }
                }
            }
        }

        return null;
    }

    // --------------------------------------------------------------------------------------------
    // ContactManager Methods
    // --------------------------------------------------------------------------------------------

    //TODO: No longer used.

    /**
     * @param phoneNumber         the phone number to search for a contact on
     * @param callType            the enum for the call type of <b>VOICE</b> or <b>VIDEO</b> call
     * @param compositeDisposable the Composite Disposable to be added to
     * @param getContactCallback  the Callback to be run on completion
     */
    @Override
    public void getContactByPhoneNumberFromApi(@NonNull final String phoneNumber,
                                               final int callType,
                                               @NonNull final CompositeDisposable compositeDisposable,
                                               @NonNull final GetContactCallback getContactCallback) {

        compositeDisposable.add(
                Single
                        .create((SingleOnSubscribe<EnterpriseContactByNumberResponseEvent>) emitter -> emitter.onSuccess(new FoundContactResponseEvent(true, getContactByPhoneNumber(phoneNumber), phoneNumber, callType)))
                        .flatMap((Function<EnterpriseContactByNumberResponseEvent, SingleSource<EnterpriseContactByNumberResponseEvent>>) responseEvent -> {
                            if (responseEvent.getNextivaContact() == null) {
                                if (phoneNumber.length() >= Constants.DEFAULT_PHONE_NUMBER_LENGTH) {
                                    return mContactManagementRepository.getEnterpriseContactByPhoneNumber(phoneNumber, callType);
                                } else {
                                    return mContactManagementRepository.getEnterpriseContactByExtension(phoneNumber, callType);
                                }

                            } else {
                                return Single.just(responseEvent);
                            }
                        })
                        .map(responseEvent -> {
                            //TODO Maybe we can get rid of this by adding to the DB from within the ContactManagementRepository (need to add a addContactOnThread method)
                            if (!FoundContactResponseEvent.class.isAssignableFrom(responseEvent.getClass()) &&
                                    responseEvent.getNextivaContact() != null) {
                                mDbManager.addContact(responseEvent.getNextivaContact(), compositeDisposable);
                            }

                            return responseEvent.getNextivaContact();
                        })
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(
                                contact -> getContactCallback.onNextivaContactReturned(contact, phoneNumber, callType),
                                throwable -> getContactCallback.onFailure(phoneNumber, callType)));
    }

    private NextivaContact getContactByPhoneNumber(@NonNull String phoneNumber) {
        NextivaContact nextivaContact;

        ArrayList<NextivaContact> rosterContacts = new ArrayList<>(mDbManager.getDbContactsInThread(Enums.Contacts.CacheTypes.ALL_ROSTER));

        if (!rosterContacts.isEmpty()) {
            nextivaContact = searchContactsForMatch(rosterContacts, phoneNumber);

            if (nextivaContact != null) {
                return nextivaContact;
            }
        }

        ArrayList<NextivaContact> enterpriseContacts = new ArrayList<>(mDbManager.getDbContactsInThread(Enums.Contacts.CacheTypes.ENTERPRISE));

        if (!enterpriseContacts.isEmpty()) {
            nextivaContact = searchContactsForMatch(enterpriseContacts, phoneNumber);

            if (nextivaContact != null) {
                return nextivaContact;
            }
        }

        ArrayList<NextivaContact> localContacts = new ArrayList<>(mDbManager.getDbContactsInThread(Enums.Contacts.CacheTypes.LOCAL));

        if (!localContacts.isEmpty()) {
            nextivaContact = searchContactsForMatch(localContacts, phoneNumber);

            return nextivaContact;
        }

        return null;
    }
    // --------------------------------------------------------------------------------------------

    private class FoundContactResponseEvent extends EnterpriseContactByNumberResponseEvent {
        @SuppressWarnings("SameParameterValue")
        FoundContactResponseEvent(boolean isSuccessful,
                                  @Nullable NextivaContact nextivaContact,
                                  @NonNull String phoneNumber,
                                  @NonNull Integer callType) {

            super(isSuccessful, nextivaContact, phoneNumber, callType);
        }
    }
}

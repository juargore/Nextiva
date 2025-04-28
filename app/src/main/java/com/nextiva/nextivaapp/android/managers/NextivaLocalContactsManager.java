/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Application;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager;
import com.nextiva.nextivaapp.android.managers.interfaces.LocalContactsManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Single;

/**
 * Created by adammacdonald on 2/27/18.
 */

//TODO This class should be merged into the ContactsManager


@Singleton
public class NextivaLocalContactsManager implements LocalContactsManager {

    private final Application mApplication;
    private final DbManager mDbManager;
    private final SchedulerProvider mSchedulerProvider;
    private final AvatarManager mAvatarManager;

    @Inject
    public NextivaLocalContactsManager(@NonNull Application application,
                                       @NonNull DbManager dbManager,
                                       @NonNull SchedulerProvider schedulerProvider,
                                       @NonNull AvatarManager avatarManager) {

        mApplication = application;
        mDbManager = dbManager;
        mSchedulerProvider = schedulerProvider;
        mAvatarManager = avatarManager;
    }

    private static void getContactInfo(@NonNull ContentResolver contentResolver,
                                       @NonNull String contactId,
                                       @NonNull NextivaContact nextivaContact,
                                       @NonNull AvatarManager avatarManager) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.StructuredName.PHOTO_URI,
                ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME};

        final String selection = ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=? AND "
                + ContactsContract.Data.MIMETYPE + "=?";

        final Cursor contactCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                new String[] {contactId, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE},
                null);

        if (contactCursor != null) {
            if (contactCursor.moveToFirst()) {
                final int photoIndex = contactCursor.getColumnIndex(projection[0]);
                final int displayNameIndex = contactCursor.getColumnIndex(projection[1]);
                final int firstNameIndex = contactCursor.getColumnIndex(projection[2]);
                final int lastNameIndex = contactCursor.getColumnIndex(projection[3]);

                nextivaContact.setDisplayName(contactCursor.getString(displayNameIndex));
                nextivaContact.setFirstName(contactCursor.getString(firstNameIndex));
                nextivaContact.setLastName(contactCursor.getString(lastNameIndex));

                if (!TextUtils.isEmpty(contactCursor.getString(photoIndex))) {
                    try {
                        Uri photoUri = Uri.parse(contactCursor.getString(photoIndex));

                        if(photoUri != null) {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri);
                            if(bitmap != null) {
                                nextivaContact.setVCard(new DbVCard(null, avatarManager.bitmapToScaledDownByteArray(bitmap)));
                            }
                        }

                    } catch (IOException | SecurityException e) {
                        e.printStackTrace();
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                }
            }

            contactCursor.close();
        }
    }

    private static void getImAddress(@NonNull ContentResolver contentResolver,
                                     @NonNull String contactId,
                                     @NonNull NextivaContact nextivaContact) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Im.DATA,
                ContactsContract.CommonDataKinds.Im.PROTOCOL};

        final String selection = ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID + "=? AND "
                + ContactsContract.Data.MIMETYPE + "=?";

        final Cursor imCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                new String[] {contactId, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE},
                null);

        if (imCursor != null) {
            if (imCursor.moveToFirst()) {
                final int imAddressIndex = imCursor.getColumnIndex(projection[0]);
                final int protocolIndex = imCursor.getColumnIndex(projection[1]);

                if (ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER == imCursor.getInt(protocolIndex)) {
                    nextivaContact.setJid(imCursor.getString(imAddressIndex));
                }
            }

            imCursor.close();
        }
    }

    private static void getEmails(@NonNull ContentResolver contentResolver,
                                  @NonNull String contactId,
                                  @NonNull NextivaContact nextivaContact) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.TYPE,
                ContactsContract.CommonDataKinds.Email.LABEL};

        final Cursor emailCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                projection,
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[] {contactId},
                null);

        if (emailCursor != null) {
            if (emailCursor.moveToFirst()) {
                ArrayList<EmailAddress> listEmailAddresses = new ArrayList<>();
                final int emailIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS);
                final int typeIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE);
                final int labelIndex = emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL);

                while (!emailCursor.isAfterLast()) {
                    if (!TextUtils.isEmpty(emailCursor.getString(emailIndex))) {

                        listEmailAddresses.add(getEmailAddressFromOsValues(emailCursor.getInt(typeIndex),
                                                                           emailCursor.getString(emailIndex),
                                                                           emailCursor.getString(labelIndex)));
                    }
                    emailCursor.moveToNext();
                }

                nextivaContact.setEmailAddresses(listEmailAddresses);
            }

            emailCursor.close();
        }
    }

    private static void getPhoneNumbers(@NonNull ContentResolver contentResolver,
                                        @NonNull String contactId,
                                        @NonNull NextivaContact nextivaContact) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.LABEL};

        final Cursor phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                ContactsContract.Data.CONTACT_ID + "=?",
                new String[] {contactId},
                null);

        if (phoneCursor != null) {
            if (phoneCursor.moveToFirst()) {
                ArrayList<PhoneNumber> listPhoneNumbers = new ArrayList<>();
                final int phoneNumberIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                final int typeIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                final int labelIndex = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL);

                while (!phoneCursor.isAfterLast()) {
                    if (!TextUtils.isEmpty(phoneCursor.getString(phoneNumberIndex))) {
                        listPhoneNumbers.add(getPhoneNumberFromOsValues(phoneCursor.getString(typeIndex),
                                                                        phoneCursor.getString(phoneNumberIndex),
                                                                        phoneCursor.getString(labelIndex)));
                    }

                    phoneCursor.moveToNext();
                }

                nextivaContact.setPhoneNumbers(listPhoneNumbers);
            }

            phoneCursor.close();
        }
    }

    private static void getCompany(@NonNull ContentResolver contentResolver,
                                   @NonNull String contactId,
                                   @NonNull NextivaContact nextivaContact) {

        final String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Organization.COMPANY};

        final String selection = ContactsContract.Data.CONTACT_ID + "=? " +
                "AND " + ContactsContract.Data.MIMETYPE + "=? ";

        final Cursor companyCursor = contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                projection,
                selection,
                new String[] {contactId, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE},
                null);

        if (companyCursor != null) {
            if (companyCursor.moveToFirst()) {
                nextivaContact.setCompany(companyCursor.getString(companyCursor.getColumnIndex(projection[0])));
            }

            companyCursor.close();
        }
    }

    private static EmailAddress getEmailAddressFromOsValues(int type, String address, String label) {

        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                return new EmailAddress(Enums.Contacts.EmailTypes.HOME_EMAIL, address, null);

            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                return new EmailAddress(Enums.Contacts.EmailTypes.MOBILE_EMAIL, address, null);

            case ContactsContract.CommonDataKinds.Email.TYPE_OTHER:
                return new EmailAddress(Enums.Contacts.EmailTypes.OTHER_EMAIL, address, null);

            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                return new EmailAddress(Enums.Contacts.EmailTypes.WORK_EMAIL, address, null);

            case ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM:
                return new EmailAddress(Enums.Contacts.EmailTypes.CUSTOM_EMAIL, address, label);

            default:
                return new EmailAddress(Enums.Contacts.EmailTypes.EMAIL, address, null);
        }
    }

    private static PhoneNumber getPhoneNumberFromOsValues(String type, String number, String label) {
        if (TextUtils.isEmpty(type)) {
            return new PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, number, null);
        }

        try {
            switch (Integer.valueOf(type)) {
                case ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.CUSTOM_PHONE, number, label);

                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_FAX, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_PAGER:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PAGER, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.MOBILE_PHONE, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_PAGER:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.PAGER, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_FAX, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_MAIN:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.MAIN_PHONE, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_COMPANY_MAIN:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.COMPANY_MAIN, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_ASSISTANT:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.ASSISTANT, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_CAR:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.CAR, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_RADIO:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.RADIO, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.CALLBACK, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_ISDN:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.ISDN, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_TELEX:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.TELEX, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_TTY_TDD:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.TTY_TDD, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_MMS:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.MMS, number, null);

                case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.OTHER_PHONE, number, null);

                default:
                    return new PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, number, null);
            }
        } catch (NumberFormatException e) {
            return new PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, number, null);
        }
    }

    // --------------------------------------------------------------------------------------------
    // LocalContactsManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public Completable getLocalContacts() {
        return Completable
                .fromAction(() -> {
                    ArrayList<NextivaContact> contacts = new ArrayList<>();
                    final String[] projection = new String[] {
                            ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                            ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY
                    };

                    final String selection = ContactsContract.CommonDataKinds.StructuredName.HAS_PHONE_NUMBER + ">0 OR "
                            + ContactsContract.CommonDataKinds.Email.DATA + " NOT LIKE '' ";

                    final Cursor rawContactsCursor = mApplication.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Contactables.CONTENT_URI,
                            projection,
                            selection,
                            null,
                            null);

                    if (rawContactsCursor != null) {
                        final int contactIdIndex = rawContactsCursor.getColumnIndex(projection[0]);
                        final int lookupKeyIndex = rawContactsCursor.getColumnIndex(projection[1]);

                        if (rawContactsCursor.moveToFirst()) {
                            NextivaContact nextivaContact;

                            while (!rawContactsCursor.isAfterLast()) {
                                final String contactId = rawContactsCursor.getString(contactIdIndex);
                                final String lookupKey = rawContactsCursor.getString(lookupKeyIndex);

                                if (!TextUtils.isEmpty(contactId)) {
                                    nextivaContact = new NextivaContact(contactId);
                                    nextivaContact.setContactType(Enums.Contacts.ContactTypes.LOCAL);
                                    nextivaContact.setLookupKey(lookupKey);

                                    getContactInfo(mApplication.getContentResolver(), contactId, nextivaContact, mAvatarManager);
                                    getEmails(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getPhoneNumbers(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getCompany(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getImAddress(mApplication.getContentResolver(), contactId, nextivaContact);

                                    contacts.add(nextivaContact);
                                }

                                rawContactsCursor.moveToNext();
                            }
                        }

                        rawContactsCursor.close();
                    }

                    mDbManager.saveLocalContactsInThread(contacts);
                })
                .subscribeOn(mSchedulerProvider.io())
                .onErrorComplete();
    }

    @Override
    public Single<ArrayList<NextivaContact>> getLocalContactsWithReturn() {
        return Single.just(new ArrayList<NextivaContact>())
                .map(contacts -> {
                    final String[] projection = new String[]{
                            ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                            ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY
                    };

                    final String selection = ContactsContract.CommonDataKinds.StructuredName.HAS_PHONE_NUMBER + ">0";

                    final Cursor rawContactsCursor = mApplication.getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            projection,
                            selection,
                            null,
                            null);

                    if (rawContactsCursor != null) {
                        final int contactIdIndex = rawContactsCursor.getColumnIndex(projection[0]);
                        final int lookupKeyIndex = rawContactsCursor.getColumnIndex(projection[1]);

                        if (rawContactsCursor.moveToFirst()) {
                            NextivaContact nextivaContact;

                            while (!rawContactsCursor.isAfterLast()) {
                                final String contactId = rawContactsCursor.getString(contactIdIndex);
                                final String lookupKey = rawContactsCursor.getString(lookupKeyIndex);

                                if (!TextUtils.isEmpty(contactId)) {
                                    nextivaContact = new NextivaContact(contactId);
                                    nextivaContact.setContactType(Enums.Contacts.ContactTypes.LOCAL);
                                    nextivaContact.setLookupKey(lookupKey);

                                    getContactInfo(mApplication.getContentResolver(), contactId, nextivaContact, mAvatarManager);
                                    getEmails(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getPhoneNumbers(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getCompany(mApplication.getContentResolver(), contactId, nextivaContact);
                                    getImAddress(mApplication.getContentResolver(), contactId, nextivaContact);

                                    contacts.add(nextivaContact);
                                }

                                rawContactsCursor.moveToNext();
                            }
                        }

                        rawContactsCursor.close();
                    }

                    mDbManager.saveLocalContactsInThread(contacts);
                    return contacts;
                })
                .subscribeOn(mSchedulerProvider.io())
                .observeOn(mSchedulerProvider.ui());
    }
    // --------------------------------------------------------------------------------------------
}

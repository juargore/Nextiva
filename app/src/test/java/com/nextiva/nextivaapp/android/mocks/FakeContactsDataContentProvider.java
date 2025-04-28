/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.mocks;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by adammacdonald on 3/26/18.
 */

public class FakeContactsDataContentProvider extends ContentProvider {

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (ContactsContract.Data.CONTENT_URI.equals(uri)) {
            if (projection.length == 1 &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Organization.COMPANY, projection[0])) {

                MatrixCursor cursor = new MatrixCursor(new String[] {
                        ContactsContract.CommonDataKinds.Organization.COMPANY});

                if (selectionArgs[0].equals("1")) {
                    cursor.addRow(new Object[] {"Company One"});

                } else if (selectionArgs[0].equals("2")) {
                    cursor.addRow(new Object[] {"Company Two"});

                } else if (selectionArgs[0].equals("3")) {
                    cursor.addRow(new Object[] {"Company Three"});

                } else if (selectionArgs[0].equals("4")) {
                    cursor.addRow(new Object[] {"Company Four"});

                } else if (selectionArgs[0].equals("5")) {
                    cursor.addRow(new Object[] {"Company Five"});
                }

                return cursor;

            } else if (projection.length == 2 &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Im.DATA, projection[0]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Im.PROTOCOL, projection[1])) {

                MatrixCursor cursor = new MatrixCursor(new String[] {
                        ContactsContract.CommonDataKinds.Im.DATA,
                        ContactsContract.CommonDataKinds.Im.PROTOCOL});

                if (selectionArgs[0].equals("1")) {
                    cursor.addRow(new Object[] {"im@address.im", ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER});

                } else if (selectionArgs[0].equals("2")) {
                    cursor.addRow(new Object[] {"bad@address.im", ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM});

                } else if (selectionArgs[0].equals("3")) {
                    cursor.addRow(new Object[] {null, null});

                } else if (selectionArgs[0].equals("4")) {
                    cursor.addRow(new Object[] {null, null});

                } else if (selectionArgs[0].equals("5")) {
                    cursor.addRow(new Object[] {null, null});

                } else if (selectionArgs[0].equals("6")) {
                    cursor.addRow(new Object[] {null, null});
                }

                return cursor;
            } else if (projection.length == 4 &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.PHOTO_URI, projection[0]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, projection[1]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, projection[2]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, projection[3])) {

                MatrixCursor cursor = new MatrixCursor(new String[] {
                        ContactsContract.CommonDataKinds.StructuredName.PHOTO_URI,
                        ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                        ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME});

                if (selectionArgs[0].equals("1")) {
                    cursor.addRow(new Object[] {null, null, null, null});

                } else if (selectionArgs[0].equals("2")) {
                    cursor.addRow(new Object[] {"ABC123", null, null, null});

                } else if (selectionArgs[0].equals("3")) {
                    cursor.addRow(new Object[] {null, "Display Name", null, null});

                } else if (selectionArgs[0].equals("4")) {
                    cursor.addRow(new Object[] {null, null, "Jim", null});

                } else if (selectionArgs[0].equals("5")) {
                    cursor.addRow(new Object[] {null, null, null, "Smith"});

                } else if (selectionArgs[0].equals("6")) {
                    cursor.addRow(new Object[] {null, null, "Bob", "Jones"});
                }

                return cursor;
            }

        } else if (ContactsContract.CommonDataKinds.Phone.CONTENT_URI.equals(uri) || ContactsContract.CommonDataKinds.Contactables.CONTENT_URI.equals(uri)) {
            if (projection.length == 2 &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID, projection[0]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY, projection[1])) {

                MatrixCursor cursor = new MatrixCursor(new String[] {
                        ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID,
                        ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY
                });

                cursor.addRow(new Object[] {"1", "lk1"});
                cursor.addRow(new Object[] {"2", "lk2"});
                cursor.addRow(new Object[] {"3", "lk3"});
                cursor.addRow(new Object[] {"4", "lk4"});
                cursor.addRow(new Object[] {"5", "lk5"});
                cursor.addRow(new Object[] {"6", "lk6"});

                return cursor;

            } else if (projection.length == 3 &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Phone.NUMBER, projection[0]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Phone.TYPE, projection[1]) &&
                    TextUtils.equals(ContactsContract.CommonDataKinds.Phone.LABEL, projection[2])) {

                MatrixCursor cursor = new MatrixCursor(new String[] {
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.LABEL});

                if (selectionArgs != null) {
                    if (selectionArgs[0].equals("1")) {
                        cursor.addRow(new Object[] {"1111111111", null, null});
                        cursor.addRow(new Object[] {"2222222222", 1, null});
                        cursor.addRow(new Object[] {"3333333333", 1, "Should Not Show"});
                        cursor.addRow(new Object[] {"4444444444", 0, "Custom Name"});
                        cursor.addRow(new Object[] {"5555555555", null, "Null Value Name"});
                        cursor.addRow(new Object[] {null, 1, null});
                        cursor.addRow(new Object[] {null, 0, "Another Custom"});
                        cursor.addRow(new Object[] {null, null, "Return of Custom"});
                        cursor.addRow(new Object[] {null, null, null});
                    }

                    if (selectionArgs[0].equals("2")) {
                        cursor.addRow(new Object[] {"5555555555", 1, null});
                        cursor.addRow(new Object[] {"5555556666", 2, null});
                    }

                    if (selectionArgs[0].equals("3")) {
                        cursor.addRow(new Object[] {"6666666666", 6, null});
                        cursor.addRow(new Object[] {"6666667777", 5, null});
                    }

                    if (selectionArgs[0].equals("4")) {
                        cursor.addRow(new Object[] {"7777777777", 3, null});
                        cursor.addRow(new Object[] {"7777778888", 17, null});
                    }

                    if (selectionArgs[0].equals("5")) {
                        cursor.addRow(new Object[] {"8888888888", 18, null});
                        cursor.addRow(new Object[] {"8888889999", 4, null});
                        cursor.addRow(new Object[] {"9999999999", 7, null});
                        cursor.addRow(new Object[] {"9999990000", 0, "Custom Label"});
                    }
                }

                return cursor;
            }

        } else if (ContactsContract.CommonDataKinds.Email.CONTENT_URI.equals(uri)) {
            MatrixCursor cursor = new MatrixCursor(new String[] {
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.CommonDataKinds.Email.TYPE,
                    ContactsContract.CommonDataKinds.Email.LABEL});

            if (selectionArgs != null) {
                if (selectionArgs[0].equals("1")) {
                    cursor.addRow(new Object[] {"fake1@email.com", null, null});
                    cursor.addRow(new Object[] {"fake2@email.com", 1, null});
                    cursor.addRow(new Object[] {"fake3@email.com", 1, "Should Not Show"});
                    cursor.addRow(new Object[] {"fake4@email.com", 0, "Custom Name"});
                    cursor.addRow(new Object[] {"fake5@email.com", null, "Null Value Name"});
                    cursor.addRow(new Object[] {null, 1, null});
                    cursor.addRow(new Object[] {null, 0, "Another Custom"});
                    cursor.addRow(new Object[] {null, null, "Return of Custom"});
                    cursor.addRow(new Object[] {null, null, null});
                }

                if (selectionArgs[0].equals("2")) {
                    cursor.addRow(new Object[] {"contactId2@email.com", 1, null});
                }

                if (selectionArgs[0].equals("3")) {
                    cursor.addRow(new Object[] {"contactId3@email.com", 2, null});
                }

                if (selectionArgs[0].equals("4")) {
                    cursor.addRow(new Object[] {"contactId4@email.com", 3, null});
                }

                if (selectionArgs[0].equals("5")) {
                    cursor.addRow(new Object[] {"contactId5@email.com", 4, null});
                    cursor.addRow(new Object[] {"contactId6@email.com", 0, "Custom Label"});
                }
            }

            return cursor;
        }

        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}

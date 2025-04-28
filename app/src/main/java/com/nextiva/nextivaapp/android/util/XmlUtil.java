/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.util;

import android.text.TextUtils;
import android.util.Base64;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.Address;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.FormatterManager;
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.NextivaVCard;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftErrorResponseBody;
import com.nextiva.nextivaapp.android.models.net.broadsoft.BroadsoftVCard;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbook;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbookContact;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbookFavorite;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftAddressbookGroup;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactCommunication;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorage;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftContactStorageTimestamp;
import com.nextiva.nextivaapp.android.models.net.broadsoft.contactstorage.BroadsoftGroupMember;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.xmpp.util.NextivaXMPPConstants;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

public class XmlUtil {

    public static NextivaVCard toNextivaVCard(BroadsoftVCard vCard) {
        NextivaVCard nextivaVCard = null;
        Serializer serializer = new Persister();

        if (vCard != null && vCard.getVCard() != null && !TextUtils.isEmpty(vCard.getJid())) {
            Reader reader = new StringReader(new String(Base64.decode(vCard.getVCard(), Base64.DEFAULT)));
            try {
                nextivaVCard = serializer.read(NextivaVCard.class, reader, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        return nextivaVCard;
    }

    public static String deserializeNextivaVCard(NextivaVCard vCard) {
        Serializer serializer = new Persister();
        ByteArrayOutputStream choiceOutputStream = new ByteArrayOutputStream();

        try {
            serializer.write(vCard, choiceOutputStream);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        return Base64.encodeToString(choiceOutputStream.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");
    }


    // Get XML for Broadsoft
    public static BroadsoftContactStorage toBroadsoftContactStorage(String contactStorage) {
        BroadsoftContactStorage broadsoftContactStorage = null;
        Serializer serializer = new Persister();

        if (!TextUtils.isEmpty(contactStorage)) {
            Reader reader = new StringReader(new String(Base64.decode(contactStorage, Base64.DEFAULT)));

            try {
                broadsoftContactStorage = serializer.read(BroadsoftContactStorage.class, reader, false);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                RxBus.INSTANCE.publish(new RxEvents.ContactUpdatedResponseEvent(false));
                e.printStackTrace();
            }
        }

        return broadsoftContactStorage;
    }

    public static String deserializeContactStorage(ArrayList<NextivaContact> contacts, ArrayList<DbGroup> groups, CalendarManager calendarManager, String resource) {
        Serializer serializer = new Persister();
        ByteArrayOutputStream choiceOutputStream = new ByteArrayOutputStream();

        BroadsoftContactStorage contactStorage = new BroadsoftContactStorage();
        BroadsoftAddressbook addressbook = new BroadsoftAddressbook();
        addressbook.setNoNamespaceSchemaLocation("contact_store.xsd");
        addressbook.setSchemaVersion("5");
        addressbook.setXsiNamespace("http://www.w3.org/2001/XMLSchema-instance");
        addressbook.setVersion("1.0");
        addressbook.setTimestamp(XmlUtil.getNewIQTimestamp(resource, calendarManager));

        ArrayList<BroadsoftAddressbookContact> addressbookContacts = new ArrayList<>();
        ArrayList<BroadsoftAddressbookGroup> addressbookGroups = new ArrayList<>();
        ArrayList<BroadsoftAddressbookFavorite> favorites = new ArrayList<>();
        BroadsoftAddressbookContact addressbookContact;

        int favoritePosition = 0;

        for (DbGroup group : groups) {
            addressbookGroups.add(new BroadsoftAddressbookGroup(group.getGroupId(),
                                                                group.getOrder(),
                                                                group.getName(),
                                                                new ArrayList<>()));
        }

        for (NextivaContact contact : contacts) {
            addressbookContact = new BroadsoftAddressbookContact();
            addressbookContact.setId(contact.getUserId());
            addressbookContact.setType(contact.getContactType() == Enums.Contacts.ContactTypes.CONFERENCE ? "conference" : null);
            addressbookContact.setFirstName(contact.getFirstName());
            addressbookContact.setLastName(contact.getLastName());
            addressbookContact.setDisplayName(contact.getDisplayName());
            addressbookContact.setTitle(contact.getTitle());
            addressbookContact.setHiraganaFirstName(contact.getHiraganaFirstName());
            addressbookContact.setHiraganaLastName(contact.getHiraganaLastName());
            addressbookContact.setGroupId(contact.getGroupId());

            if (contact.getEmailAddresses() != null && !contact.getEmailAddresses().isEmpty()) {
                addressbookContact.setEmail(contact.getEmailAddresses().get(0).getAddress());
            }

            if (contact.getAddresses() != null) {
                for (Address address : contact.getAddresses()) {
                    addressbookContact.setStreet(address.getAddressLineOne());
                    addressbookContact.setPostalCode(address.getPostalCode());
                    addressbookContact.setCity(address.getCity());
                    addressbookContact.setRegion(address.getRegion());
                    addressbookContact.setCountry(address.getCountry());
                    addressbookContact.setLocation(address.getLocation());
                }
            }

            ArrayList<BroadsoftContactCommunication> communications = new ArrayList<>();

            if (!TextUtils.isEmpty(contact.getJid())) {
                communications.add(new BroadsoftContactCommunication(NextivaXMPPConstants.CONTACTS_IQ_JID_TYPE, contact.getJid()));
            }

            if (!TextUtils.isEmpty(contact.getServerUserId())) {
                communications.add(new BroadsoftContactCommunication(NextivaXMPPConstants.CONTACTS_IQ_BW_USERID_TAGNAME, contact.getServerUserId()));
            }

            if (contact.getAllPhoneNumbers() != null) {
                for (PhoneNumber phoneNumber : contact.getAllPhoneNumbers()) {
                    String xmppConstantType;

                    switch (phoneNumber.getType()) {
                        case Enums.Contacts.PhoneTypes.WORK_EXTENSION:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_EXTENSION_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.PAGER:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_PAGER_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.CONFERENCE_PHONE:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_CONFERENCE_NUMBER_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.MOBILE_PHONE:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_MOBILE_PHONE_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.HOME_PHONE:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_HOME_PHONE_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.WORK_PHONE:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_WORK_PHONE_TYPE;
                            break;
                        case Enums.Contacts.PhoneTypes.CUSTOM_PHONE:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_CUSTOM_PHONE_TYPE;
                            break;
                        default:
                            xmppConstantType = NextivaXMPPConstants.CONTACTS_IQ_HOME_PHONE_TYPE;
                            break;
                    }

                    if (!TextUtils.isEmpty(phoneNumber.getNumber())) {
                        communications.add(new BroadsoftContactCommunication(xmppConstantType, phoneNumber.getPinOne(), phoneNumber.getPinTwo(), phoneNumber.getNumber()));
                    }
                }
            }

            addressbookContact.setCommunications(communications);
            addressbookContacts.add(addressbookContact);

            if (contact.getGroups() != null) {
                for (DbGroup dbGroup : contact.getGroups()) {
                    for (BroadsoftAddressbookGroup group : addressbookGroups) {
                        if (TextUtils.equals(dbGroup.getGroupId(), group.getId()) && group.getMembers() != null) {
                            group.getMembers().add(new BroadsoftGroupMember(contact.getUserId()));
                            break;
                        }
                    }
                }
            }

            if (contact.isFavorite()) {
                favorites.add(new BroadsoftAddressbookFavorite(contact.getUserId(), String.valueOf(favoritePosition)));
                favoritePosition++;
            }
        }

        addressbook.setContacts(addressbookContacts);
        addressbook.setGroups(addressbookGroups);
        addressbook.setFavorites(favorites);
        contactStorage.setAddressbook(addressbook);

        try {
            serializer.write(contactStorage, choiceOutputStream);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            RxBus.INSTANCE.publish(new RxEvents.ContactUpdatedResponseEvent(false));
            e.printStackTrace();
        }

        return Base64.encodeToString(choiceOutputStream.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");
    }

    public static String deserializeContactStorageTimestamp(String resource, CalendarManager calendarManager) {
        Serializer serializer = new Persister();
        ByteArrayOutputStream choiceOutputStream = new ByteArrayOutputStream();

        try {
            serializer.write(new BroadsoftContactStorageTimestamp(getNewIQTimestamp(resource, calendarManager)), choiceOutputStream);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
            e.printStackTrace();
        }

        return Base64.encodeToString(choiceOutputStream.toByteArray(), Base64.DEFAULT).replaceAll("\n", "");

    }

    private static String getNewIQTimestamp(String resource, CalendarManager calendarManager) {
        return FormatterManager.getInstance().getDateFormatter_8601ExtendedDatetimeTimeZoneNoMs()
                .format(calendarManager.getNowInstant()) + " " + resource;
    }

    public static BroadsoftErrorResponseBody getBroadsoftErrorResponseBody(String xmlString) {
        Serializer serializer = new Persister();

        if (!TextUtils.isEmpty(xmlString)) {
            try {
                return serializer.read(BroadsoftErrorResponseBody.class, xmlString.replaceAll("[\r\n]+", ""), false);

            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
                e.printStackTrace();
            }
        }

        return null;
    }
}

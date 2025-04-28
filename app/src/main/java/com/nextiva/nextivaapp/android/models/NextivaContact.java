/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.models;

import static com.nextiva.nextivaapp.android.constants.Constants.Contacts.Aliases.XBERT_ALIASES;

import android.annotation.SuppressLint;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.Junction;
import androidx.room.Relation;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.model.Address;
import com.nextiva.nextivaapp.android.db.model.DbDate;
import com.nextiva.nextivaapp.android.db.model.DbGroup;
import com.nextiva.nextivaapp.android.db.model.DbGroupRelation;
import com.nextiva.nextivaapp.android.db.model.DbPresence;
import com.nextiva.nextivaapp.android.db.model.DbVCard;
import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.db.model.SmsTeam;
import com.nextiva.nextivaapp.android.db.model.SocialMediaAccount;
import com.nextiva.nextivaapp.android.db.util.DbConstants;
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectEmailType;
import com.nextiva.nextivaapp.android.models.net.platform.contacts.ConnectPhoneType;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.xmpp.util.PresenceUtil;
import com.nextiva.pjsip.pjsip_lib.sipservice.ParticipantInfo;

import org.jivesoftware.smack.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by joedephillipo on 2/21/18.
 */

public class NextivaContact implements Serializable, Cloneable {

    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_ID)
    private Long mDbId;
    @NonNull
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE_ID)
    private String mUserId;
    @Enums.Contacts.ContactTypes.Type
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CONTACT_TYPE)
    private Integer mContactType;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_FIRST_NAME)
    private String mFirstName;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_NAME)
    private String mLastName;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_FIRST_NAME)
    private String mHiraganaFirstName;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_HIRAGANA_LAST_NAME)
    private String mHiraganaLastName;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DISPLAY_NAME)
    private String mDisplayName;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_TITLE)
    private String mTitle;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_COMPANY)
    private String mCompany;
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_IS_FAVORITE)
    private Boolean mIsFavorite;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_GROUP_ID)
    private String mGroupId;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.POSTAL_ADDRESSES_COLUMN_NAME_CONTACT_ID, entity = Address.class)
    private List<Address> mAddresses;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SHORT_JID)
    private String mServerUserId;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.PRESENCES_COLUMN_NAME_CONTACT_ID, entity = DbPresence.class)
    private List<DbPresence> mPresences;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.PHONES_COLUMN_NAME_CONTACT_ID, entity = PhoneNumber.class)
    private List<PhoneNumber> mAllPhoneNumbers;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.EMAILS_COLUMN_NAME_CONTACT_ID, entity = EmailAddress.class)
    private List<EmailAddress> mEmailAddresses;
    @Enums.Contacts.SubscriptionStates.SubscriptionState
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SUBSCRIPTION_STATE)
    private Integer mSubscriptionState;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_JID)
    private String mJid;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.GROUPS_COLUMN_NAME_GROUP_ID, entity = DbGroup.class, associateBy =
    @Junction(value = DbGroupRelation.class, parentColumn = DbConstants.GROUPS_RELATION_COLUMN_NAME_CONTACT_ID, entityColumn = DbConstants.GROUPS_RELATION_COLUMN_NAME_GROUP_ID))
    private List<DbGroup> mGroups;
    @Nullable
    @ColumnInfo(name = DbConstants.VCARDS_COLUMN_NAME_PHOTO_DATA)
    private byte[] mPhotoData;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_WEBSITE)
    private String mWebsite;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DEPARTMENT)
    private String mDepartment;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_DESCRIPTION)
    private String mDescription;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_ALIASES)
    private String mAliases;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_CREATED_BY)
    private String mCreatedBy;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_BY)
    private String mLastModifiedBy;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LAST_MODIFIED_ON)
    private String mLastModifiedOn;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.SOCIAL_MEDIA_ACCOUNT_COLUMN_NAME_CONTACT_ID, entity = SocialMediaAccount.class)
    private List<SocialMediaAccount> mSocialMediaAccounts;
    @Nullable
    @Relation(parentColumn = DbConstants.CONTACTS_COLUMN_NAME_ID, entityColumn = DbConstants.DATE_COLUMN_NAME_CONTACT_ID, entity = DbDate.class)
    private List<DbDate> mDates;
    @Nullable
    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_LOOKUP_KEY)
    private String mLookupKey;
    @Nullable
    @Ignore
    private SmsTeam mRepresentingTeam;

    public void setSortGroup(Integer sortGroup) {
        if (!StringUtils.isNullOrEmpty(mDisplayName)) {
            if (mContactType == 9 || mContactType == 10 || mContactType == 11) {
                mSortGroup = 4;
            } else if (mDisplayName.matches(".*[a-zA-Z]+.*")) {
                mSortGroup = 1;
            } else if (mDisplayName.matches("^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$'")) {
                mSortGroup = 2;
            } else if (mDisplayName.equalsIgnoreCase("Unknown")) {
                mSortGroup = 3;
            } else {
                mSortGroup = sortGroup;
            }
        }
    }

    @ColumnInfo(name = DbConstants.CONTACTS_COLUMN_NAME_SORT_GROUP)
    private Integer mSortGroup;

    public NextivaContact() {
    }

    public NextivaContact(@NonNull String userId) {
        mUserId = userId;
    }

    public NextivaContact(String contactNumber, String ad) {
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, contactNumber));
        mAllPhoneNumbers = phoneNumbers;
    }

    public NextivaContact(String contactNumber, int contactType) {
        mContactType = contactType;

        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, contactNumber));
        mAllPhoneNumbers = phoneNumbers;
    }

    public NextivaContact(String userId, String contactNumber, int contactType) {
        mUserId = userId;
        mContactType = contactType;

        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
        phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, contactNumber));
        mAllPhoneNumbers = phoneNumbers;
    }

    public NextivaContact(SmsParticipant smsParticipant) {
        mContactType = Enums.Contacts.ContactTypes.CONNECT_UNKNOWN;

        if (!TextUtils.isEmpty(smsParticipant.getPhoneNumber())) {
            ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();
            phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.WORK_PHONE, smsParticipant.getPhoneNumber()));
            mAllPhoneNumbers = phoneNumbers;
        }

        if (!TextUtils.isEmpty(smsParticipant.getUserUUID())) {
            mUserId = Objects.requireNonNull(smsParticipant.getUserUUID());
        }

        mDisplayName = smsParticipant.getUiName();
    }

    @SuppressLint("UseValueOf")
    @SuppressWarnings( {"StringOperationCanBeSimplified", "UnnecessaryBoxing", "BoxingBoxedValue"})
    public NextivaContact(@NonNull NextivaContact nextivaContact) {
        mDbId = nextivaContact.getDbId() != null ? new Long(nextivaContact.getDbId()) : null;
        mUserId = new String(nextivaContact.getUserId());
        mContactType = nextivaContact.getContactType();
        mFirstName = nextivaContact.getFirstName() != null ? new String(nextivaContact.getFirstName()) : null;
        mLastName = nextivaContact.getLastName() != null ? new String(nextivaContact.getLastName()) : null;
        mHiraganaFirstName = nextivaContact.getHiraganaFirstName() != null ? new String(nextivaContact.getHiraganaFirstName()) : null;
        mHiraganaLastName = nextivaContact.getHiraganaLastName() != null ? new String(nextivaContact.getHiraganaLastName()) : null;
        mDisplayName = nextivaContact.getDisplayName() != null ? new String(nextivaContact.getDisplayName()) : null;
        mTitle = nextivaContact.getTitle() != null ? new String(nextivaContact.getTitle()) : null;
        mCompany = nextivaContact.getCompany() != null ? new String(nextivaContact.getCompany()) : null;
        //noinspection BooleanConstructorCall
        mIsFavorite = nextivaContact.getIsFavoriteRaw() != null ? new Boolean(nextivaContact.isFavorite()) : null;
        mGroupId = nextivaContact.getGroupId() != null ? new String(nextivaContact.getGroupId()) : null;
        if (nextivaContact.getAddresses() != null) {
            ArrayList<Address> addresses = new ArrayList<>();

            for (Address address : nextivaContact.getAddresses()) {
                addresses.add(new Address(address));
            }

            mAddresses = addresses;
        }
        mServerUserId = nextivaContact.getServerUserId() != null ? new String(nextivaContact.getServerUserId()) : null;
        mPresences = nextivaContact.getPresence() != null ? new ArrayList<DbPresence>() {{
            add(new DbPresence(nextivaContact.getPresence()));
        }} : null;

        if (nextivaContact.getGroups() != null) {
            ArrayList<DbGroup> groups = new ArrayList<>();

            for (DbGroup group : nextivaContact.getGroups()) {
                groups.add(new DbGroup(group));
            }

            mGroups = groups;
        }

        if (nextivaContact.getAllPhoneNumbers() != null) {
            ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

            for (PhoneNumber phoneNumber : nextivaContact.getAllPhoneNumbers()) {
                phoneNumbers.add(new PhoneNumber(phoneNumber));
            }

            mAllPhoneNumbers = phoneNumbers;
        }

        if (nextivaContact.getEmailAddresses() != null) {
            ArrayList<EmailAddress> emailAddresses = new ArrayList<>();

            for (EmailAddress emailAddress : nextivaContact.getEmailAddresses()) {
                emailAddresses.add(new EmailAddress(emailAddress));
            }

            mEmailAddresses = emailAddresses;
        }

        if (nextivaContact.getDates() != null) {
            ArrayList<DbDate> dates = new ArrayList<>();

            for (DbDate date : nextivaContact.getDates()) {
                dates.add(new DbDate(date));
            }

            mDates = dates;
        }

        if (nextivaContact.getSocialMediaAccounts() != null) {
            ArrayList<SocialMediaAccount> socialMediaAccounts = new ArrayList<>();

            for (SocialMediaAccount account : nextivaContact.getSocialMediaAccounts()) {
                socialMediaAccounts.add(new SocialMediaAccount(account));
            }

            mSocialMediaAccounts = socialMediaAccounts;
        }

        mWebsite = nextivaContact.getWebsite() != null ? new String(nextivaContact.getWebsite()) : null;
        mDescription = nextivaContact.getDescription() != null ? new String(nextivaContact.getDescription()) : null;
        mDepartment = nextivaContact.getDepartment() != null ? new String(nextivaContact.getDepartment()) : null;
        mCreatedBy = nextivaContact.getCreatedBy() != null ? new String(nextivaContact.getCreatedBy()) : null;
        mLastModifiedBy = nextivaContact.getLastModifiedBy() != null ? new String(nextivaContact.getLastModifiedBy()) : null;
        mLastModifiedOn = nextivaContact.getLastModifiedOn() != null ? new String(nextivaContact.getLastModifiedOn()) : null;
        mJid = nextivaContact.getJid() != null ? new String(nextivaContact.getJid()) : null;
        mSubscriptionState = nextivaContact.getSubscriptionState();
        mPhotoData = nextivaContact.getVCard() != null ? nextivaContact.getVCard().getPhotoData() : null;
        mAliases = nextivaContact.getAliases() != null ? new String(nextivaContact.getAliases()) : null;
    }

    @Nullable
    public String getLookupKey() {
        return mLookupKey;
    }

    public void setLookupKey(@Nullable String lookupKey) {
        this.mLookupKey = lookupKey;
    }

    @Nullable
    public Long getDbId() {
        return mDbId;
    }

    public void setDbId(@Nullable Long dbId) {
        mDbId = dbId;
    }

    @NonNull
    public String getUserId() {
        return mUserId;
    }

    public void setUserId(@NonNull String userId) {
        mUserId = userId;
    }

    @Enums.Contacts.ContactTypes.Type
    public Integer getContactType() {
        return mContactType == null ? Enums.Contacts.ContactTypes.NONE : mContactType;
    }

    public void setContactType(@Enums.Contacts.ContactTypes.Type Integer contactType) {
        mContactType = contactType;
    }

    @Nullable
    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(@Nullable String firstName) {
        mFirstName = firstName;
    }

    @Nullable
    public String getLastName() {
        return mLastName;
    }

    public void setLastName(@Nullable String lastName) {
        mLastName = lastName;
    }

    @Nullable
    public String getHiraganaFirstName() {
        return mHiraganaFirstName;
    }

    public void setHiraganaFirstName(@Nullable String hiraganaFirstName) {
        mHiraganaFirstName = hiraganaFirstName;
    }

    @Nullable
    public String getHiraganaLastName() {
        return mHiraganaLastName;
    }

    public void setHiraganaLastName(@Nullable String hiraganaLastName) {
        mHiraganaLastName = hiraganaLastName;
    }

    @Nullable
    public String getDisplayName() {
        return mDisplayName;
    }

    public void setDisplayName(@Nullable String displayName) {
        mDisplayName = displayName;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(@Nullable String title) {
        mTitle = title;
    }

    @Nullable
    public String getCompany() {
        return mCompany;
    }

    public void setCompany(@Nullable String company) {
        mCompany = company;
    }

    @Nullable
    public String getWebsite() {
        return mWebsite;
    }

    public void setWebsite(@Nullable String website) {
        mWebsite = website;
    }

    @Nullable
    public String getDepartment() {
        return mDepartment;
    }

    public void setDepartment(@Nullable String department) {
        mDepartment = department;
    }

    @Nullable
    public String getCreatedBy() {
        return mCreatedBy;
    }

    public void setCreatedBy(@Nullable String createdBy) {
        mCreatedBy = createdBy;
    }

    @Nullable
    public String getLastModifiedBy() {
        return mLastModifiedBy;
    }

    public void setLastModifiedBy(@Nullable String lastModifiedBy) {
        mLastModifiedBy = lastModifiedBy;
    }

    @Nullable
    public String getLastModifiedOn() {
        return mLastModifiedOn;
    }

    public void setLastModifiedOn(@Nullable String lastModifiedOn) {
        mLastModifiedOn = lastModifiedOn;
    }

    @Nullable
    public String getDescription() {
        return mDescription;
    }

    public void setDescription(@Nullable String description) {
        mDescription = description;
    }

    public boolean isFavorite() {
        return mIsFavorite == null ? false : mIsFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        mIsFavorite = isFavorite;
    }

    public Boolean getIsFavoriteRaw() {
        return mIsFavorite;
    }

    public void setRepresentingTeam(@Nullable SmsTeam team) {
        mRepresentingTeam = team;
    }

    @Nullable
    public SmsTeam getRepresentingTeam() {
        return mRepresentingTeam;
    }

    @Enums.Contacts.SubscriptionStates.SubscriptionState
    public Integer getSubscriptionState() {
        return mSubscriptionState == null ? Enums.Contacts.SubscriptionStates.UNSUBSCRIBED : mSubscriptionState;
    }

    public void setSubscriptionState(@Enums.Contacts.SubscriptionStates.SubscriptionState Integer subscriptionState) {
        mSubscriptionState = subscriptionState;
    }

    public Integer getSortGroup() {
        if (!TextUtils.isEmpty(mDisplayName)) {
            if (mContactType == Enums.Contacts.ContactTypes.CONNECT_CALL_FLOW || mContactType == Enums.Contacts.ContactTypes.CONNECT_TEAM || mContactType == Enums.Contacts.ContactTypes.CONNECT_CALL_CENTERS) {
                return mSortGroup = 4;
            } else if (TextUtils.isEmpty(mFirstName) && (TextUtils.isEmpty(mLastName)) && mDisplayName.matches("^([0-9]*(\\s)*(\\*)*(#)*(\\+)*(\\()*(\\))*(\\-)*)+$") && !mDisplayName.equalsIgnoreCase("Unknown")) {
                return mSortGroup = 2;
            } else if ((!TextUtils.isEmpty(mFirstName) || (!TextUtils.isEmpty(mLastName)) || (!TextUtils.isEmpty(mCompany)) || ((mEmailAddresses != null && mEmailAddresses.size() > 0)))) {
                return mSortGroup = 1;
            } else if (mDisplayName.equalsIgnoreCase("Unknown")) {
                return mSortGroup = 3;
            }
        }
        return 5;
    }

    public boolean isRosterContact() {
        return mContactType != null && (mContactType == Enums.Contacts.ContactTypes.PERSONAL || mContactType == Enums.Contacts.ContactTypes.CONFERENCE);
    }

    @Nullable
    public String getGroupId() {
        return mGroupId;
    }

    public void setGroupId(@Nullable String groupId) {
        mGroupId = groupId;
    }

    @Nullable
    public String getServerUserId() {
        return mServerUserId;
    }

    public void setServerUserId(@Nullable String bwUserId) {
        mServerUserId = bwUserId;
    }

    @Nullable
    public DbPresence getPresence() {
        if (mPresences != null && !mPresences.isEmpty()) {
            return mPresences.get(0);

        } else if (mSubscriptionState != null && mSubscriptionState == Enums.Contacts.SubscriptionStates.PENDING) {
            return PresenceUtil.getPendingPresence(mJid);

        } else if (mContactType != null && mContactType == Enums.Contacts.ContactTypes.CONNECT_USER) {
            return new DbPresence(Enums.Contacts.PresenceStates.CONNECT_OFFLINE, null);
        }

        return null;
    }

    public void setPresence(@Nullable DbPresence presence) {
        mPresences = new ArrayList<DbPresence>() {{
            add(presence);
        }};
    }

    public void setPresences(@Nullable List<DbPresence> presences) {
        mPresences = presences;
    }

    @SuppressWarnings("WeakerAccess")
    @Nullable
    List<DbPresence> getPresences() {
        return mPresences;
    }

    public void setGroups(@Nullable List<DbGroup> groups) {
        mGroups = groups;
    }

    @Nullable
    public List<DbGroup> getGroups() {
        return mGroups;
    }

    public void addGroup(DbGroup group) {
        if (mGroups != null) {
            mGroups.add(group);

        } else {
            mGroups = new ArrayList<DbGroup>() {{
                add(group);
            }};
        }
    }

    public void removeGroup(DbGroup group) {
        if (mGroups != null) {
            ArrayList<DbGroup> groupsClone = new ArrayList<>(mGroups);

            for (DbGroup contactGroup : groupsClone) {
                if (TextUtils.equals(contactGroup.getGroupId(), group.getGroupId())) {
                    mGroups.remove(contactGroup);
                }
            }
        }
    }

    @Nullable
    public List<Address> getAddresses() {
        return mAddresses;
    }

    public void setAddresses(@Nullable List<Address> addresses) {
        mAddresses = addresses;
    }

    @Nullable
    public ArrayList<PhoneNumber> getPhoneNumbers() {
        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

        if (mAllPhoneNumbers != null) {
            for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
                if (phoneNumber.getType() != Enums.Contacts.PhoneTypes.WORK_EXTENSION && phoneNumber.getType() != Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    phoneNumbers.add(phoneNumber);
                }
            }

            return phoneNumbers;
        } else {
            return null;
        }
    }

    public void setPhoneNumbers(ArrayList<PhoneNumber> phoneNumbers) {
        if (mAllPhoneNumbers == null) {
            mAllPhoneNumbers = phoneNumbers;

        } else if (getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : getPhoneNumbers()) {
                if (phoneNumber.getType() != Enums.Contacts.PhoneTypes.WORK_EXTENSION && phoneNumber.getType() != Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    mAllPhoneNumbers.remove(phoneNumber);
                }
            }

            if (phoneNumbers != null) {
                mAllPhoneNumbers.addAll(phoneNumbers);
            }

        } else if (phoneNumbers != null) {
            mAllPhoneNumbers.addAll(phoneNumbers);
        }
    }

    @Nullable
    public ArrayList<PhoneNumber> getExtensions() {
        ArrayList<PhoneNumber> extensions = new ArrayList<>();

        if (mAllPhoneNumbers != null) {
            for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.WORK_EXTENSION) {
                    extensions.add(phoneNumber);
                }
            }

            return extensions;
        } else {
            return null;
        }
    }

    public void setExtensions(ArrayList<PhoneNumber> extensions) {
        if (mAllPhoneNumbers == null) {
            mAllPhoneNumbers = extensions;

        } else if (getExtensions() != null) {
            for (PhoneNumber phoneNumber : getExtensions()) {
                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.WORK_EXTENSION) {
                    mAllPhoneNumbers.remove(phoneNumber);
                }
            }

            if (extensions != null) {
                mAllPhoneNumbers.addAll(extensions);
            }

        } else if (extensions != null) {
            mAllPhoneNumbers.addAll(extensions);
        }
    }

    @Nullable
    public ArrayList<EmailAddress> getEmailAddresses() {
        if (mEmailAddresses != null) {
            return new ArrayList<>(mEmailAddresses);
        } else {
            return null;
        }
    }

    @Nullable
    public TreeMap<Integer, List<EmailAddress>> getCategorizedEmailAddresses() {

        if (mEmailAddresses != null) {
            TreeMap<Integer, List<EmailAddress>> categories = new TreeMap<>(new Comparator<Integer>() {
                private final List<Integer> mListDefinedOrder = Arrays.asList(
                        Enums.Contacts.EmailTypes.WORK_EMAIL,
                        Enums.Contacts.EmailTypes.HOME_EMAIL,
                        Enums.Contacts.EmailTypes.MOBILE_EMAIL,
                        Enums.Contacts.EmailTypes.CONNECT_PRIMARY_EMAIL,
                        Enums.Contacts.EmailTypes.CONNECT_SECONDARY_EMAIL,
                        Enums.Contacts.EmailTypes.OTHER_EMAIL,
                        Enums.Contacts.EmailTypes.ICLOUD_EMAIL,
                        Enums.Contacts.EmailTypes.CUSTOM_EMAIL
                );

                @Override
                public int compare(Integer o1, Integer o2) {
                    return Integer.compare(mListDefinedOrder.indexOf(o1),
                                           mListDefinedOrder.indexOf(o2));
                }
            });

            for(EmailAddress emailAddress : mEmailAddresses) {
                Integer type = ConnectEmailType.Companion.fromIntType(emailAddress.getType()).getNumericType();
                List<EmailAddress> list = categories.getOrDefault(type, new ArrayList<>());
                list.add(emailAddress);
                categories.put(type, list);
            }
            return categories;
        } else {
            return null;
        }
    }

    public void setEmailAddresses(@Nullable ArrayList<EmailAddress> emailAddresses) {
        mEmailAddresses = emailAddresses;
    }

    @Nullable
    public ArrayList<SocialMediaAccount> getSocialMediaAccounts() {
        if (mSocialMediaAccounts != null) {
            return new ArrayList<>(mSocialMediaAccounts);
        } else {
            return null;
        }
    }

    public void setSocialMediaAccounts(@Nullable ArrayList<SocialMediaAccount> socialMediaAccounts) {
        mSocialMediaAccounts = socialMediaAccounts;
    }

    @Nullable
    public ArrayList<DbDate> getDates() {
        if (mDates != null) {
            return new ArrayList<>(mDates);
        } else {
            return null;
        }
    }

    public void setDates(@Nullable ArrayList<DbDate> dates) {
        mDates = dates;
    }

    public void setConferencePhoneNumbers(ArrayList<PhoneNumber> conferencePhoneNumbers) {
        if (mAllPhoneNumbers == null) {
            mAllPhoneNumbers = conferencePhoneNumbers;

        } else if (getConferencePhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : getConferencePhoneNumbers()) {
                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    mAllPhoneNumbers.remove(phoneNumber);
                }
            }

            if (conferencePhoneNumbers != null) {
                mAllPhoneNumbers.addAll(conferencePhoneNumbers);
            }

        } else if (conferencePhoneNumbers != null) {
            mAllPhoneNumbers.addAll(conferencePhoneNumbers);
        }
    }

    @Nullable
    public ArrayList<PhoneNumber> getConferencePhoneNumbers() {
        ArrayList<PhoneNumber> conferenceNumbers = new ArrayList<>();

        if (mAllPhoneNumbers != null) {
            for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    conferenceNumbers.add(phoneNumber);
                }
            }

            return conferenceNumbers;
        } else {
            return null;
        }
    }

    @Nullable
    public List<PhoneNumber> getAllPhoneNumbers() {
        return mAllPhoneNumbers;
    }

    public void setAllPhoneNumbers(@Nullable List<PhoneNumber> phoneNumbers) {
        mAllPhoneNumbers = phoneNumbers;
    }

    @Nullable
    public String getJid() {
        return mJid;
    }

    public void setJid(@Nullable String jid) {
        mJid = jid;
    }

    @Nullable
    public byte[] getPhotoData() {
        return mPhotoData;
    }

    public void setPhotoData(@Nullable byte[] photoData) {
        mPhotoData = photoData;
    }

    @Nullable
    public DbVCard getVCard() {
        return new DbVCard(mJid, mPhotoData);
    }

    public void setVCard(@Nullable DbVCard vCard) {
        mPhotoData = vCard != null ? vCard.getPhotoData() : null;
    }

    @Nullable
    public String getUiName() {
        if (!TextUtils.isEmpty(mDisplayName)) {
            return mDisplayName;

        } else if (!TextUtils.isEmpty(mFirstName) && !TextUtils.isEmpty(mLastName)) {
            return mFirstName + " " + mLastName;

        } else if (!TextUtils.isEmpty(mLastName)) {
            return mLastName;

        } else if (!TextUtils.isEmpty(mFirstName)) {
            return mFirstName;

        } else if (!TextUtils.isEmpty(mCompany)) {
            return mCompany;

        } else if (!TextUtils.isEmpty(mJid)) {
            return mJid;

        } else if (getPhoneNumbers() != null && !getPhoneNumbers().isEmpty() && !TextUtils.isEmpty(getPhoneNumbers().get(0).getNumber())) {
            return PhoneNumberUtils.formatNumber(getPhoneNumbers().get(0).getNumber(), Locale.getDefault().getCountry());

        } else if (getExtensions() != null && !getExtensions().isEmpty() && !TextUtils.isEmpty(getExtensions().get(0).getNumber())) {
            return getExtensions().get(0).getNumber();

        } else if (getConferencePhoneNumbers() != null && !getConferencePhoneNumbers().isEmpty() && !TextUtils.isEmpty(getConferencePhoneNumbers().get(0).getAssembledPhoneNumber())) {
            return !TextUtils.isEmpty(PhoneNumberUtils.formatNumber(getConferencePhoneNumbers().get(0).getAssembledPhoneNumber(), Locale.getDefault().getCountry())) ?
                    PhoneNumberUtils.formatNumber(getConferencePhoneNumbers().get(0).getAssembledPhoneNumber(), Locale.getDefault().getCountry()) :
                    getConferencePhoneNumbers().get(0).getAssembledPhoneNumber();

        } else if (mEmailAddresses != null && !mEmailAddresses.isEmpty() && !TextUtils.isEmpty(mEmailAddresses.get(0).getAddress())) {
            return mEmailAddresses.get(0).getAddress();

        } else {
            return null;
        }
    }

    public String getSearchMatchText(String searchTerm) {
        return getSearchMatchText(searchTerm, false);
    }

    public String getSearchMatchText(String searchTerm, boolean isConnect) {
        if (!TextUtils.isEmpty(getFirstName()) && !TextUtils.isEmpty(getLastName()) &&
                (getFirstName() + " " + getLastName()).toLowerCase().contains(searchTerm)) {
            return getFirstName() + " " + getLastName();
        }

        if (!TextUtils.isEmpty(getLastName()) && getLastName().toLowerCase().contains(searchTerm)) {
            return getLastName();
        }

        if (!TextUtils.isEmpty(getFirstName()) && getFirstName().toLowerCase().contains(searchTerm)) {
            return getFirstName();
        }

        if (!TextUtils.isEmpty(getCompany()) && getCompany().toLowerCase().contains(searchTerm)) {
            return getCompany();
        }

        if (!TextUtils.isEmpty(getJid()) && getJid().toLowerCase().contains(searchTerm)) {
            return getJid();
        }

        if (getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : getPhoneNumbers()) {
                if (!TextUtils.isEmpty(phoneNumber.getNumber()) && phoneNumber.getNumber().contains(searchTerm)) {
                    return isConnect ? phoneNumber.getNumber() :
                            TextUtils.isEmpty(PhoneNumberUtils.formatNumber(phoneNumber.getNumber(), Locale.getDefault().getCountry())) ?
                                    phoneNumber.getNumber() : PhoneNumberUtils.formatNumber(phoneNumber.getNumber(), Locale.getDefault().getCountry());
                }
            }
        }

        if (getPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : getPhoneNumbers()) {
                if (!TextUtils.isEmpty(phoneNumber.getNumber()) && CallUtil.getStrippedPhoneNumber(phoneNumber.getNumber()).contains(searchTerm)) {
                    return isConnect ? TextUtils.isEmpty(CallUtil.getStrippedPhoneNumber(phoneNumber.getNumber())) ?
                            phoneNumber.getNumber() : CallUtil.getStrippedPhoneNumber(phoneNumber.getNumber()) :
                            TextUtils.isEmpty(PhoneNumberUtils.formatNumber(CallUtil.getStrippedPhoneNumber(phoneNumber.getNumber()), Locale.getDefault().getCountry())) ?
                                    phoneNumber.getNumber() : PhoneNumberUtils.formatNumber(CallUtil.getStrippedPhoneNumber(phoneNumber.getNumber()), Locale.getDefault().getCountry());
                }
            }
        }

        if (getExtensions() != null) {
            for (PhoneNumber extension : getExtensions()) {
                if (!TextUtils.isEmpty(extension.getNumber()) && extension.getNumber().contains(searchTerm)) {
                    return extension.getNumber();
                }
            }
        }

        if (getConferencePhoneNumbers() != null) {
            String strippedPhoneNumber;
            for (PhoneNumber conferenceNumber : getConferencePhoneNumbers()) {
                if (!TextUtils.isEmpty(conferenceNumber.getAssembledPhoneNumber())) {
                    strippedPhoneNumber = CallUtil.getStrippedPhoneNumber(conferenceNumber.getAssembledPhoneNumber());

                    if (strippedPhoneNumber.contains(searchTerm) ||
                            (!TextUtils.isEmpty(conferenceNumber.getAssembledPhoneNumber()) && conferenceNumber.getAssembledPhoneNumber().contains(searchTerm))) {

                        return conferenceNumber.getAssembledPhoneNumber();
                    }
                }
            }
        }

        if (getEmailAddresses() != null) {
            for (EmailAddress emailAddress : getEmailAddresses()) {
                if (!TextUtils.isEmpty(emailAddress.getAddress()) && emailAddress.getAddress().toLowerCase().contains(searchTerm)) {
                    return emailAddress.getAddress();
                }
            }
        }

        return null;
    }

    @Nullable
    public String getAvatarName() {
        if (!TextUtils.isEmpty(mDisplayName) && TextUtils.isEmpty(mCompany)) {
            return mDisplayName;
        } else if (!TextUtils.isEmpty(mCompany) && (TextUtils.isEmpty(mFirstName) || TextUtils.isEmpty(mLastName))) {
            return Enums.AvatarDisplays.COMPANY;
        } else if (!TextUtils.isEmpty(mFirstName) && !TextUtils.isEmpty(mLastName)) {
            return mFirstName + " " + mLastName;

        } else if (!TextUtils.isEmpty(mLastName)) {
            return mLastName;

        } else if (!TextUtils.isEmpty(mFirstName)) {
            return mFirstName;

        } else {
            return null;
        }
    }

    public void setAliases(@Nullable String aliases) {
        mAliases = aliases;
    }

    @Nullable
    public String getAliases() {
        return mAliases;
    }

    public boolean hasValidSMSNumber() {
        if (getAllPhoneNumbers() == null || getAllPhoneNumbers().isEmpty()) {
            return false;
        }

        for (PhoneNumber number : getAllPhoneNumbers()) {
            if (!TextUtils.isEmpty(number.getStrippedNumber()) && CallUtil.isValidSMSNumber(number.getStrippedNumber())) {
                return true;
            }
        }

        return false;
    }

    public boolean containsName(String name) {
        return !TextUtils.isEmpty(getUiName()) && TextUtils.equals(name, getUiName());
    }

    public boolean containsPhoneNumber(String number) {
        ArrayList<PhoneNumber> phoneNumbers = getPhoneNumbers();

        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            for (PhoneNumber phoneNumber : phoneNumbers) {
                if (phoneNumber != null &&
                        !TextUtils.isEmpty(phoneNumber.getNumber()) &&
                        TextUtils.equals(CallUtil.getStrippedPhoneNumber(number), phoneNumber.getStrippedNumber())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsExtension(String number) {
        ArrayList<PhoneNumber> extensions = getExtensions();

        if (extensions != null && !extensions.isEmpty()) {
            for (PhoneNumber extension : extensions) {
                if (extension != null && !TextUtils.isEmpty(extension.getNumber()) && TextUtils.equals(number, extension.getNumber())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean containsConferenceNumber() {
        ArrayList<PhoneNumber> conferenceNumbers = getConferencePhoneNumbers();

        if (conferenceNumbers != null && !conferenceNumbers.isEmpty()) {
            for (PhoneNumber conferencePhoneNumber : conferenceNumbers) {
                if (conferencePhoneNumber != null && conferencePhoneNumber.getType() == Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    return true;
                }
            }
        }

        return false;
    }

    public AvatarInfo getAvatarInfo() {
        return getAvatarInfo(false);
    }

    public AvatarInfo getAvatarInfo(boolean isConnect) {
        AvatarInfo avatarInfo;

        if (getContactType() == Enums.Contacts.ContactTypes.CONFERENCE && TextUtils.isEmpty(getJid())) {
            avatarInfo = new AvatarInfo.Builder()
                    .setIconResId(R.drawable.ic_phone)
                    .build();

        }
        else if(mAliases != null && mAliases.toLowerCase().contains(XBERT_ALIASES)){
            avatarInfo = new AvatarInfo.Builder()
                        .setIconResId(R.drawable.xbert_avatar)
                        .setPhotoData(getPhotoData())
                        .build();
        }
        else {
            AvatarInfo.Builder builder = new AvatarInfo.Builder();

            if (getAvatarName() != null) {
                builder.setDisplayName(getAvatarName());
            }

            if (getVCard() != null) {
                builder.setPhotoData(getVCard().getPhotoData());
            }

            if (getPresence() != null) {
                builder.setPresence(getPresence());

            } else if (!TextUtils.isEmpty(mJid) && getSubscriptionState() == Enums.Contacts.SubscriptionStates.PENDING) {
                builder.setPresence(PresenceUtil.getPendingPresence(mJid));
            }

            if (isConnect ||
                    getContactType() == Enums.Contacts.ContactTypes.CONNECT_USER ||
                    getContactType() == Enums.Contacts.ContactTypes.CONNECT_SHARED ||
                    getContactType() == Enums.Contacts.ContactTypes.CONNECT_PERSONAL ||
                    getContactType() == Enums.Contacts.ContactTypes.CONNECT_UNKNOWN) {
                builder.setFontAwesomeIconResId(R.string.fa_user);
            }
            builder.isConnect(isConnect);


            avatarInfo = builder.build();
        }

        return avatarInfo;
    }

    public void convertToConnect(int contactType) {
        mContactType = contactType;

        for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
            phoneNumber.setType(ConnectPhoneType.Companion.fromIntType(phoneNumber.getType()).getNumericType());
        }

        for (EmailAddress emailAddress : mEmailAddresses) {
            emailAddress.setType(ConnectEmailType.Companion.fromIntType(emailAddress.getType()).getNumericType());
        }

    }

    public ParticipantInfo getParticipantInfo(String number) {
        ParticipantInfo participantInfo = new ParticipantInfo();

        if (TextUtils.isEmpty(number)) {
            if (getAllPhoneNumbers() != null && !getAllPhoneNumbers().isEmpty()) {
                participantInfo.setNumberToCall(getAllPhoneNumbers().get(0).getStrippedNumber());
            }
        } else {
            participantInfo.setNumberToCall(number);
        }

        participantInfo.setDisplayName(getUiName());
        participantInfo.setContactId(getUserId());

        return participantInfo;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NextivaContact) {
            NextivaContact that = (NextivaContact) obj;
            return StringUtil.equalsWithNullsAndBlanks(mUserId, that.getUserId()) &&
                    getContactType().equals(that.getContactType()) &&
                    StringUtil.equalsWithNullsAndBlanks(mFirstName, that.getFirstName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mLastName, that.getLastName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mDisplayName, that.getDisplayName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mCompany, that.getCompany()) &&
                    isFavorite() == that.isFavorite() &&
                    StringUtil.equalsWithNullsAndBlanks(mGroupId, that.getGroupId()) &&
                    StringUtil.equalsWithNullsAndBlanks(mServerUserId, that.getServerUserId()) &&
                    doPresenceValuesMatch(that.getPresence()) &&
                    doPhoneNumberValuesMatch(that.getAllPhoneNumbers()) &&
                    doExtensionsMatch(that.getExtensions()) &&
                    doEmailAddressValuesMatch(that.getEmailAddresses()) &&
                    StringUtil.equalsWithNullsAndBlanks(mJid, that.getJid()) &&
                    getSubscriptionState().equals(that.getSubscriptionState()) &&
                    ((getVCard() == null && that.getVCard() == null) || (getVCard() != null && that.getVCard() != null && Arrays.equals(getVCard().getPhotoData(), that.getVCard().getPhotoData())));
        }

        return false;
    }

    public boolean equalsEnterpriseOrRoster(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj instanceof NextivaContact) {
            NextivaContact that = (NextivaContact) obj;

            return StringUtil.equalsWithNullsAndBlanks(mFirstName, that.getFirstName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mLastName, that.getLastName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mHiraganaFirstName, that.getHiraganaFirstName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mHiraganaLastName, that.getHiraganaLastName()) &&
                    StringUtil.equalsWithNullsAndBlanks(mCompany, that.getCompany()) &&
                    StringUtil.equalsWithNullsAndBlanks(mTitle, that.getTitle()) &&
                    StringUtil.equalsWithNullsAndBlanks(mServerUserId, that.getServerUserId()) &&
                    doPhoneNumberValuesMatch(that.getAllPhoneNumbers()) &&
                    doExtensionsMatch(that.getExtensions()) &&
                    doEmailAddressValuesMatch(that.getEmailAddresses()) &&

                    //doAddressValuesMatch(that.getAddress()) &&
                    StringUtil.equalsWithNullsAndBlanks(mJid, that.getJid());
        }

        return false;
    }

    private boolean doPresenceValuesMatch(DbPresence presence) {
        if (getPresence() != null && presence != null) {
            return getPresence().getType() == presence.getType() &&
                    getPresence().getAvailability().equals(presence.getAvailability()) &&
                    getPresence().getPriority() == presence.getPriority() &&
                    TextUtils.equals(getPresence().getStatus(), presence.getStatus()) &&
                    TextUtils.equals(getPresence().getJid(), presence.getJid());
        }

        return getPresence() == null && presence == null;
    }

    private boolean doAddressValuesMatch(List<Address> addresses) {
        boolean addressFoundAndMatches;

        if ((mAddresses != null && !mAddresses.isEmpty()) && (addresses != null && !addresses.isEmpty())) {
            for (Address thisAddress : mAddresses) {
                addressFoundAndMatches = false;

                for (Address thatAddress : addresses) {
                    if (thisAddress.getType() == thatAddress.getType() &&
                            TextUtils.equals(thisAddress.getAddressLineOne(), thatAddress.getAddressLineTwo()) &&
                            TextUtils.equals(thisAddress.getAddressLineTwo(), thatAddress.getAddressLineTwo()) &&
                            TextUtils.equals(thisAddress.getCity(), thatAddress.getCity()) &&
                            TextUtils.equals(thisAddress.getCountry(), thatAddress.getCountry()) &&
                            TextUtils.equals(thisAddress.getLocation(), thatAddress.getLocation()) &&
                            TextUtils.equals(thisAddress.getPostalCode(), thatAddress.getPostalCode()) &&
                            TextUtils.equals(thisAddress.getRegion(), thatAddress.getRegion())) {
                        addressFoundAndMatches = true;
                        break;
                    }
                }

                if (!addressFoundAndMatches) {
                    return false;
                }
            }
        }

        return (mAddresses == null && addresses == null) ||
                (((mAddresses != null && mAddresses.isEmpty()) && (addresses != null && addresses.isEmpty()))) ||
                (mAddresses != null && addresses != null);
    }

    private boolean doEmailAddressValuesMatch(List<EmailAddress> emailAddresses) {
        boolean emailAddressFoundAndMatches;

        if ((mEmailAddresses != null && !mEmailAddresses.isEmpty()) && (emailAddresses != null && !emailAddresses.isEmpty())) {
            for (EmailAddress thisEmailAddress : mEmailAddresses) {
                emailAddressFoundAndMatches = false;

                for (EmailAddress thatEmailAddress : emailAddresses) {
                    if (thisEmailAddress.getType() == thatEmailAddress.getType() &&
                            TextUtils.equals(thisEmailAddress.getAddress(), thatEmailAddress.getAddress())) {
                        emailAddressFoundAndMatches = true;
                        break;
                    }
                }

                if (!emailAddressFoundAndMatches) {
                    return false;
                }
            }

        }

        return (mEmailAddresses == null && emailAddresses == null) ||
                (((mEmailAddresses != null && mEmailAddresses.isEmpty()) && (emailAddresses != null && emailAddresses.isEmpty()))) ||
                (mEmailAddresses != null && emailAddresses != null);
    }

    private boolean doExtensionsMatch(List<PhoneNumber> phoneNumbers) {
        boolean phoneNumberFoundAndMatches;

        if ((getExtensions() != null && !getExtensions().isEmpty()) && (phoneNumbers != null && !phoneNumbers.isEmpty())) {
            for (PhoneNumber thisPhoneNumber : getExtensions()) {
                phoneNumberFoundAndMatches = false;

                for (PhoneNumber thatPhoneNumber : phoneNumbers) {
                    if (thisPhoneNumber.getType() == thatPhoneNumber.getType() &&
                            TextUtils.equals(thisPhoneNumber.getNumber(), thatPhoneNumber.getNumber()) &&
                            TextUtils.equals(thisPhoneNumber.getPinOne(), thatPhoneNumber.getPinOne()) &&
                            TextUtils.equals(thisPhoneNumber.getPinTwo(), thatPhoneNumber.getPinTwo())) {
                        phoneNumberFoundAndMatches = true;
                        break;
                    }
                }

                if (!phoneNumberFoundAndMatches) {
                    return false;
                }
            }

        }

        return (getExtensions() == null && phoneNumbers == null) ||
                (((getExtensions() != null && getExtensions().isEmpty()) && (phoneNumbers != null && phoneNumbers.isEmpty()))) ||
                ((getExtensions() != null && phoneNumbers != null) && getExtensions().size() == phoneNumbers.size());
    }

    private boolean doPhoneNumberValuesMatch(List<PhoneNumber> phoneNumbers) {
        boolean phoneNumberFoundAndMatches;

        if ((mAllPhoneNumbers != null && !mAllPhoneNumbers.isEmpty()) && (phoneNumbers != null && !phoneNumbers.isEmpty())) {
            for (PhoneNumber thisPhoneNumber : mAllPhoneNumbers) {
                phoneNumberFoundAndMatches = false;

                for (PhoneNumber thatPhoneNumber : phoneNumbers) {
                    if (thisPhoneNumber.getType() == thatPhoneNumber.getType() &&
                            TextUtils.equals(thisPhoneNumber.getNumber(), thatPhoneNumber.getNumber()) &&
                            TextUtils.equals(thisPhoneNumber.getPinOne(), thatPhoneNumber.getPinOne()) &&
                            TextUtils.equals(thisPhoneNumber.getPinTwo(), thatPhoneNumber.getPinTwo())) {
                        phoneNumberFoundAndMatches = true;
                        break;
                    }
                }

                if (!phoneNumberFoundAndMatches) {
                    return false;
                }
            }

        }

        return (mAllPhoneNumbers == null && phoneNumbers == null) ||
                (((mAllPhoneNumbers != null && mAllPhoneNumbers.isEmpty()) && (phoneNumbers != null && phoneNumbers.isEmpty()))) ||
                ((mAllPhoneNumbers != null && phoneNumbers != null) && mAllPhoneNumbers.size() == phoneNumbers.size());
    }

    public void updateContactWith(NextivaContact updatedContact) {
        this.setFirstName(updatedContact.getFirstName());
        this.setLastName(updatedContact.getLastName());
        this.setHiraganaFirstName(updatedContact.getHiraganaFirstName());
        this.setHiraganaLastName(updatedContact.getHiraganaLastName());
        this.setCompany(updatedContact.getCompany());
        this.setTitle(updatedContact.getTitle());
        this.setAddresses(updatedContact.getAddresses());
        this.setServerUserId(updatedContact.getServerUserId());
        this.setEmailAddresses(updatedContact.getEmailAddresses());
        this.setExtensions(updatedContact.getExtensions());

        PhoneNumber conferenceNumber = null;
        PhoneNumber personalNumber = null;

        if (this.getAllPhoneNumbers() != null) {
            for (PhoneNumber phoneNumber : this.getAllPhoneNumbers()) {
                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                    conferenceNumber = phoneNumber;
                }

                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.HOME_PHONE) {
                    personalNumber = phoneNumber;
                }
            }
        }

        this.setPhoneNumbers(updatedContact.getPhoneNumbers());

        if (conferenceNumber != null) {
            this.getAllPhoneNumbers().add(conferenceNumber);
        }

        if (personalNumber != null) {
            this.getAllPhoneNumbers().add(personalNumber);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "NextivaContact{" +
                "mUserId='" + mUserId + '\'' +
                ", mContactType=" + mContactType +
                ", mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mHiraganaFirstName='" + mHiraganaFirstName + '\'' +
                ", mHiraganaLastName='" + mHiraganaLastName + '\'' +
                ", mDisplayName='" + mDisplayName + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mCompany='" + mCompany + '\'' +
                ", mIsFavorite=" + mIsFavorite +
                ", mGroupId='" + mGroupId + '\'' +
                ", mAddresses=" + mAddresses +
                ", mServerUserId='" + mServerUserId + '\'' +
                ", mPresence=" + mPresences +
                ", mGroups=" + mGroups +
                ", mPhoneNumbers=" + mAllPhoneNumbers +
                ", mEmailAddresses=" + mEmailAddresses +
                ", mJid='" + mJid + '\'' +
                ", mVCard=" + Arrays.toString(mPhotoData) + '\'' +
                ", mAliases=" + mAliases +
                '}';
    }

    /**
     * Get a sorted list of all phone numbers in the display order specified by
     * the business logic.
     * <br>
     * <br>
     * <p>
     * This list can contain objects of {@link PhoneNumber}
     *
     * @return The sorted list of phone numbers
     */
    @NonNull
    public List<PhoneNumber> getAllPhoneNumbersSorted() {
        List<Pair<Integer, PhoneNumber>> numberPairsList = new ArrayList<>();

        if (mAllPhoneNumbers != null && !mAllPhoneNumbers.isEmpty()) {
            for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
                if (!TextUtils.isEmpty(phoneNumber.getNumber())) {
                    numberPairsList.add(new Pair<>(phoneNumber.getType(), phoneNumber));
                }
            }
        }

        Collections.sort(numberPairsList, new Comparator<Pair<Integer, PhoneNumber>>() {
            private final List<Integer> mListDefinedOrder = Arrays.asList(
                    Enums.Contacts.PhoneTypes.WORK_PHONE,
                    Enums.Contacts.PhoneTypes.WORK_EXTENSION,
                    Enums.Contacts.PhoneTypes.WORK_MOBILE_PHONE,
                    Enums.Contacts.PhoneTypes.WORK_PAGER,
                    Enums.Contacts.PhoneTypes.WORK_FAX,
                    Enums.Contacts.PhoneTypes.HOME_PHONE,
                    Enums.Contacts.PhoneTypes.MOBILE_PHONE,
                    Enums.Contacts.PhoneTypes.IPHONE,
                    Enums.Contacts.PhoneTypes.PAGER,
                    Enums.Contacts.PhoneTypes.HOME_FAX,
                    Enums.Contacts.PhoneTypes.MAIN_PHONE,
                    Enums.Contacts.PhoneTypes.CONFERENCE_PHONE,
                    Enums.Contacts.PhoneTypes.CUSTOM_PHONE,
                    Enums.Contacts.PhoneTypes.COMPANY_MAIN,
                    Enums.Contacts.PhoneTypes.ASSISTANT,
                    Enums.Contacts.PhoneTypes.CAR,
                    Enums.Contacts.PhoneTypes.RADIO,
                    Enums.Contacts.PhoneTypes.CALLBACK,
                    Enums.Contacts.PhoneTypes.ISDN,
                    Enums.Contacts.PhoneTypes.TELEX,
                    Enums.Contacts.PhoneTypes.TTY_TDD,
                    Enums.Contacts.PhoneTypes.MMS,
                    Enums.Contacts.PhoneTypes.OTHER_PHONE,
                    Enums.Contacts.PhoneTypes.OTHER_FAX,
                    Enums.Contacts.PhoneTypes.PHONE);

            @Override
            public int compare(Pair<Integer, PhoneNumber> o1, Pair<Integer, PhoneNumber> o2) {
                return Integer.compare(mListDefinedOrder.indexOf(o1.first),
                                       mListDefinedOrder.indexOf(o2.first));
            }
        });

        List<PhoneNumber> objectsList = new ArrayList<>();
        for (Pair<Integer, PhoneNumber> numberPair : numberPairsList) {
            if (!objectsList.contains(numberPair.second)) {
                objectsList.add(numberPair.second);
            }
        }

        return objectsList;
    }

    @NonNull
    public TreeMap<Integer, List<PhoneNumber>> getCategorizedNumbersSorted() {

        TreeMap<Integer, List<PhoneNumber>> categories = new TreeMap<>(new Comparator<Integer>() {
            private final List<Integer> mListDefinedOrder = Arrays.asList(
                    ConnectPhoneType.Work.getNumericType(),
                    ConnectPhoneType.Home.getNumericType(),
                    ConnectPhoneType.Mobile.getNumericType(),
                    ConnectPhoneType.Other.getNumericType());

            @Override
            public int compare(Integer o1, Integer o2) {
                    return Integer.compare(mListDefinedOrder.indexOf(o1),
                                           mListDefinedOrder.indexOf(o2));
            }
        });

        if (mAllPhoneNumbers != null && !mAllPhoneNumbers.isEmpty()) {
            for (PhoneNumber phoneNumber : mAllPhoneNumbers) {
                if (!TextUtils.isEmpty(phoneNumber.getNumber())) {
                    int type = ConnectPhoneType.Companion.fromIntType(phoneNumber.getType()).getNumericType();
                    List<PhoneNumber> list = categories.getOrDefault(type, new ArrayList<>());
                    list.add(phoneNumber);
                    categories.put(type, list);
                }
            }
        }

        //
        // Sort Phone numbers and group them into numbersWithExtension,
        // single numbers, and phone extensions,
        //

        for(Map.Entry<Integer, List<PhoneNumber>> category : categories.entrySet()) {
            List<PhoneNumber> list = categories.get(category.getKey());
            if (list != null) {
                list.sort((o1, o2) -> {

                    if (o1.getNumber() != null && o2.getNumber() != null) {

                        String num1 = "";
                        String num2 = "";
                        String ext1;
                        String ext2;
                        if (o1.getType() != Enums.Contacts.PhoneTypes.WORK_EXTENSION) {
                            String[] split = o1.getNumber().split("x");
                            num1 = split[0];
                            ext1 = split.length > 1 ? split[1] : "";
                        } else {
                            ext1 = o1.getNumber();
                        }
                        if (o2.getType() != Enums.Contacts.PhoneTypes.WORK_EXTENSION) {
                            String[] split = o2.getNumber().split("x");
                            num2 = o2.getNumber().split("x")[0];
                            ext2 = split.length > 1 ? split[1] : "";
                        } else {
                            ext2 = o2.getNumber();
                        }

                        boolean fullNumber1 = !num1.isEmpty() && !ext1.isEmpty();
                        boolean fullNumber2 = !num2.isEmpty() && !ext2.isEmpty();

                        boolean hasNumber1 = !num1.isEmpty();
                        boolean hasNumber2 = !num2.isEmpty();

                        if (fullNumber1 && fullNumber2) {
                            return o2.getNumber().length() - o1.getNumber().length();
                        } else if (!fullNumber1 && !fullNumber2) {
                            if (hasNumber1 && hasNumber2) {
                                return num2.length() - num1.length();
                            } else if (!hasNumber1 && !hasNumber2) {
                                return ext2.length() - ext1.length();
                            } else {
                                if (hasNumber1) {
                                    return -1;
                                } else {
                                    return 1;
                                }
                            }
                        } else {
                            if (fullNumber1) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    }
                    return 0;
                });
                categories.put(category.getKey(), list);
            }
        }

        return categories;
    }

    public String getHumanReadablePresenceText() {
        if (getPresence() != null && getSubscriptionState() != Enums.Contacts.SubscriptionStates.UNSUBSCRIBED) {
            return getPresence().getHumanReadablePresenceText();

        } else if (getSubscriptionState() == Enums.Contacts.SubscriptionStates.PENDING) {
            return Enums.Contacts.PresenceStateText.PENDING;
        }

        return null;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
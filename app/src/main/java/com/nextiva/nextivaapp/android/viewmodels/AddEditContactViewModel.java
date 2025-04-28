package com.nextiva.nextivaapp.android.viewmodels;

import android.app.Application;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.db.DbManager;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.ContactManagementRepository;
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.net.buses.RxBus;
import com.nextiva.nextivaapp.android.net.buses.RxEvents;
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.GuidUtil;
import com.nextiva.nextivaapp.android.util.StringUtil;
import com.nextiva.nextivaapp.android.xmpp.managers.interfaces.XMPPConnectionActionManager;

import java.util.ArrayList;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AddEditContactViewModel extends BaseViewModel {

    private final DbManager mDbManager;
    private final XMPPConnectionActionManager mXMPPConnectionActionManager;
    private final ContactManagementRepository mContactManagementRepository;
    private final ConfigManager mConfigManager;
    private final SchedulerProvider mSchedulerProvider;

    private final MutableLiveData<Resource<SaveNextivaContactEvent>> mSaveContactMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<SingleEvent<RxEvents.XmppErrorEvent>> mXmppErrorMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<NextivaContact> mEditingContactMutableLiveData = new MutableLiveData<>();

    private NextivaContact mSavedContact;
    private CallLogEntry mCallLogEntry = null;

    @Inject
    public AddEditContactViewModel(
            @NonNull Application application,
            DbManager dbManager,
            XMPPConnectionActionManager xmppConnectionActionManager,
            ContactManagementRepository contactManagementRepository,
            ConfigManager configManager,
            SchedulerProvider schedulerProvider) {

        super(application);

        mDbManager = dbManager;
        mXMPPConnectionActionManager = xmppConnectionActionManager;
        mContactManagementRepository = contactManagementRepository;
        mConfigManager = configManager;
        mSchedulerProvider = schedulerProvider;

        initRxEventListeners();
    }

    public void setEditingContact(NextivaContact editingContact) {
        mEditingContactMutableLiveData.setValue(editingContact);
    }

    public LiveData<NextivaContact> getEditingContactLiveData() {
        return mEditingContactMutableLiveData;
    }

    public LiveData<Resource<SaveNextivaContactEvent>> getSaveContactLiveData() {
        return mSaveContactMutableLiveData;
    }

    public LiveData<SingleEvent<RxEvents.XmppErrorEvent>> getXmppErrorLiveData() {
        return mXmppErrorMutableLiveData;
    }

    public String completeImAddressIfNeeded(@NonNull String imAddress) {
        StringBuilder stringBuilder = new StringBuilder(imAddress);

        if (stringBuilder.length() > 0 &&
                stringBuilder.indexOf("@") == -1 &&
                mConfigManager.getMobileConfig() != null &&
                mConfigManager.getMobileConfig().getXmpp() != null &&
                !TextUtils.isEmpty(mConfigManager.getMobileConfig().getXmpp().getDomain())) {

            stringBuilder.append("@");
            stringBuilder.append(mConfigManager.getMobileConfig().getXmpp().getDomain());
        }

        return stringBuilder.toString();
    }

    // --------------------------------------------------------------------------------------------
    // Enterprise Contact Flow
    // --------------------------------------------------------------------------------------------
    public void validateEnterpriseContact(@NonNull String displayName,
                                          @NonNull String imAddress,
                                          @NonNull String personalPhoneNumber,
                                          @NonNull String conferenceNumber,
                                          @NonNull String conferenceId,
                                          @NonNull String securityPin) {

        imAddress = completeImAddressIfNeeded(imAddress);

        if (TextUtils.isEmpty(personalPhoneNumber) &&
                TextUtils.isEmpty(conferenceNumber) &&
                TextUtils.isEmpty(imAddress)) {

            int content = mEditingContactMutableLiveData.getValue() == null ?
                    R.string.add_contact_incomplete_add_enterprise_contact_message :
                    R.string.add_contact_incomplete_edit_enterprise_contact_message;

            mSaveContactMutableLiveData.setValue(Resource.error(getApplication().getString(content), null));

        } else if (mEditingContactMutableLiveData.getValue() == null &&
                !TextUtils.isEmpty(imAddress) &&
                !StringUtil.isValidImAddress(imAddress)) {

            mSaveContactMutableLiveData.setValue(Resource.error(
                    getApplication().getString(R.string.add_contact_incorrect_im_address_format_message),
                    new SaveNextivaContactEvent(null, R.string.add_contact_incorrect_format)));

        } else {
            saveEnterpriseContact(displayName, imAddress, personalPhoneNumber, conferenceNumber, conferenceId, securityPin);
        }
    }

    private void saveEnterpriseContact(final String displayName,
                                       final String imAddress,
                                       final String personalPhone,
                                       final String conferenceNumber,
                                       final String conferenceId,
                                       final String securityPin) {

        mSaveContactMutableLiveData.setValue(Resource.loading(null));

        if (!TextUtils.isEmpty(imAddress) && mEditingContactMutableLiveData.getValue() == null) {
            mCompositeDisposable.add(
                    mContactManagementRepository.getEnterpriseContactByImpId(imAddress, null)
                            .subscribe(event -> {
                                if (event.isSuccessful()) {
                                    if (event.getNextivaContact() != null) {
                                        mSaveContactMutableLiveData.setValue(Resource.error(
                                                getApplication().getString(R.string.add_contact_duplicate_im_address_message, imAddress),
                                                null));

                                    } else {
                                        addEnterpriseContactToRoster(displayName, imAddress, personalPhone, conferenceNumber, conferenceId, securityPin);
                                    }

                                } else {
                                    mSaveContactMutableLiveData.setValue(Resource.error("", null));
                                }
                            }));

        } else {
            addEnterpriseContactToRoster(displayName, imAddress, personalPhone, conferenceNumber, conferenceId, securityPin);
        }
    }

    private void addEnterpriseContactToRoster(String displayName,
                                              String imAddress,
                                              String personalPhone,
                                              String conferenceNumber,
                                              String conferenceId,
                                              String securityPin) {

        addContactToRoster(Enums.Contacts.ContactTypes.PERSONAL, displayName, imAddress, personalPhone, conferenceNumber, conferenceId, securityPin);
    }
    // --------------------------------------------------------------------------------------------

    // --------------------------------------------------------------------------------------------
    // Conference Contact Flow
    // --------------------------------------------------------------------------------------------
    public void validateConferenceContact(@NonNull String displayName,
                                          @NonNull String conferenceNumber,
                                          @NonNull String conferenceId,
                                          @NonNull String securityPin) {

        if (TextUtils.isEmpty(conferenceNumber)) {
            int content = mEditingContactMutableLiveData.getValue() == null ?
                    R.string.add_contact_incomplete_add_conference_contact_message :
                    R.string.add_contact_incomplete_edit_conference_contact_message;

            mSaveContactMutableLiveData.setValue(Resource.error(getApplication().getString(content), null));

        } else {
            saveConferenceContact(displayName, conferenceNumber, conferenceId, securityPin);
        }
    }

    private void saveConferenceContact(String displayName,
                                       String conferenceNumber,
                                       String conferenceId,
                                       String securityPin) {

        addConferenceContactToRoster(displayName, conferenceNumber, conferenceId, securityPin);
    }

    private void addConferenceContactToRoster(String displayName,
                                              String conferenceNumber,
                                              String conferenceId,
                                              String securityPin) {

        addContactToRoster(Enums.Contacts.ContactTypes.CONFERENCE, displayName, null, null, conferenceNumber, conferenceId, securityPin);
    }
    // --------------------------------------------------------------------------------------------

    private void addContactToRoster(final @Enums.Contacts.ContactTypes.Type int contactType,
                                    final String displayName,
                                    final String imAddress,
                                    final String personalPhone,
                                    final String conferenceNumber,
                                    final String conferenceId,
                                    final String securityPin) {

        setSavedContact(null);

        if (mSaveContactMutableLiveData.getValue() == null || !TextUtils.equals(mSaveContactMutableLiveData.getValue().getStatus(), Enums.Net.StatusTypes.LOADING)) {
            mSaveContactMutableLiveData.setValue(Resource.loading(null));
        }

        mCompositeDisposable.add(
                mDbManager.getContacts(Enums.Contacts.CacheTypes.ALL_ROSTER)
                        .subscribe(rosterContacts -> {
                            if (mEditingContactMutableLiveData.getValue() == null || mEditingContactMutableLiveData.getValue().getContactType() == Enums.Contacts.ContactTypes.UNKNOWN) {
                                NextivaContact contact = new NextivaContact(String.valueOf(GuidUtil.getUniqueContactId(mDbManager)));
                                ArrayList<PhoneNumber> phones = new ArrayList<>();
                                contact.setContactType(contactType);

                                if (!TextUtils.isEmpty(displayName)) {
                                    contact.setDisplayName(displayName);
                                }

                                if (!TextUtils.isEmpty(imAddress)) {
                                    contact.setJid(imAddress);
                                }

                                if (contactType != Enums.Contacts.ContactTypes.CONFERENCE && !TextUtils.isEmpty(personalPhone)) {
                                    phones.add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, personalPhone, null));
                                }

                                if (!TextUtils.isEmpty(conferenceNumber)) {
                                    phones.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE,
                                                               conferenceNumber,
                                                               conferenceId,
                                                               securityPin));
                                }

                                contact.setAllPhoneNumbers(phones);

                                rosterContacts.add(contact);

                                setSavedContact(contact);

                                mXMPPConnectionActionManager.updateRoster(new ArrayList<NextivaContact>() {{
                                                                              add(contact);
                                                                          }},
                                                                          rosterContacts,
                                                                          Enums.Contacts.UpdateActions.ADD_EXISTING_CONTACT);

                            } else {
                                for (NextivaContact nextivaContact : rosterContacts) {
                                    if (TextUtils.equals(nextivaContact.getUserId(), mEditingContactMutableLiveData.getValue().getUserId())) {

                                        ArrayList<PhoneNumber> phoneNumbers = new ArrayList<>();

                                        if (!TextUtils.isEmpty(displayName)) {
                                            nextivaContact.setDisplayName(displayName);
                                        } else {
                                            nextivaContact.setDisplayName(null);
                                        }

                                        if (mEditingContactMutableLiveData.getValue().getContactType() != Enums.Contacts.ContactTypes.CONFERENCE && !TextUtils.isEmpty(personalPhone)) {
                                            if (nextivaContact.getPhoneNumbers() != null) {
                                                boolean personalNumberFound = false;

                                                for (PhoneNumber phoneNumber : nextivaContact.getPhoneNumbers()) {
                                                    if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.HOME_PHONE) {

                                                        phoneNumber.setNumber(personalPhone);
                                                        personalNumberFound = true;
                                                    }
                                                }

                                                if (!personalNumberFound) {
                                                    phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, personalPhone, null));
                                                }

                                            } else {
                                                phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.HOME_PHONE, personalPhone, null));
                                            }

                                        } else if (nextivaContact.getAllPhoneNumbers() != null) {
                                            ArrayList<PhoneNumber> removePhoneNumbersList = new ArrayList<>();

                                            for (PhoneNumber phoneNumber : nextivaContact.getAllPhoneNumbers()) {
                                                if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.HOME_PHONE) {
                                                    removePhoneNumbersList.add(phoneNumber);
                                                }
                                            }

                                            nextivaContact.getAllPhoneNumbers().removeAll(removePhoneNumbersList);
                                        }

                                        nextivaContact.setConferencePhoneNumbers(null);

                                        if (!TextUtils.isEmpty(conferenceNumber)) {
                                            phoneNumbers.add(new PhoneNumber(Enums.Contacts.PhoneTypes.CONFERENCE_PHONE,
                                                                             conferenceNumber,
                                                                             conferenceId,
                                                                             securityPin));

                                        }

                                        if (nextivaContact.getAllPhoneNumbers() != null) {
                                            phoneNumbers.addAll(nextivaContact.getAllPhoneNumbers());
                                        }

                                        nextivaContact.setAllPhoneNumbers(phoneNumbers);
                                        setSavedContact(nextivaContact);

                                        mXMPPConnectionActionManager.updateRoster(new ArrayList<NextivaContact>() {{
                                                                                      add(nextivaContact);
                                                                                  }},
                                                                                  rosterContacts,
                                                                                  Enums.Contacts.UpdateActions.EDIT);
                                        break;
                                    }
                                }
                            }
                        }));
    }

    public boolean changesMade(final String displayName,
                               final String imAddress,
                               final String personalPhone,
                               final String conferenceNumber,
                               final String conferenceId,
                               final String securityPin) {

        NextivaContact editingContact = mEditingContactMutableLiveData.getValue();

        if (editingContact != null) {
            if ((editingContact.getDisplayName() != null && !TextUtils.equals(displayName, editingContact.getDisplayName())) ||
                    editingContact.getDisplayName() == null && !TextUtils.isEmpty(displayName)) {

                return true;
            }

            if ((editingContact.getJid() != null && !TextUtils.equals(imAddress, editingContact.getJid())) ||
                    editingContact.getJid() == null && !TextUtils.isEmpty(imAddress)) {

                return true;
            }

            if (editingContact.getPhoneNumbers() != null) {
                boolean foundPersonalPhoneNumber = false;

                for (PhoneNumber phoneNumber : editingContact.getPhoneNumbers()) {
                    if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.HOME_PHONE &&
                            phoneNumber.getNumber() != null) {

                        foundPersonalPhoneNumber = true;

                        if (!TextUtils.equals(CallUtil.cleanForTextWatcher(phoneNumber.getNumber()),
                                CallUtil.cleanForTextWatcher(personalPhone))) {

                            return true;
                        }
                    }
                }

                if (!foundPersonalPhoneNumber && !TextUtils.isEmpty(personalPhone)) {
                    return true;
                }
            }

            if (editingContact.containsConferenceNumber() && editingContact.getConferencePhoneNumbers() != null) {
                if (!StringUtil.equalsWithNullsAndBlanks(
                        CallUtil.cleanForTextWatcher(editingContact.getConferencePhoneNumbers().get(0).getNumber()),
                        CallUtil.cleanForTextWatcher(conferenceNumber))) {

                    return true;
                }

                if (!StringUtil.equalsWithNullsAndBlanks(editingContact.getConferencePhoneNumbers().get(0).getPinOne(), conferenceId)) {
                    return true;
                }

                return !StringUtil.equalsWithNullsAndBlanks(editingContact.getConferencePhoneNumbers().get(0).getPinTwo(), securityPin);

            } else {
                return !TextUtils.isEmpty(conferenceNumber) || !TextUtils.isEmpty(conferenceId) || !TextUtils.isEmpty(securityPin);
            }
        } else {
            return !TextUtils.isEmpty(displayName) ||
                    !TextUtils.isEmpty(imAddress) ||
                    !TextUtils.isEmpty(personalPhone) ||
                    !TextUtils.isEmpty(conferenceNumber) ||
                    !TextUtils.isEmpty(conferenceId) ||
                    !TextUtils.isEmpty(securityPin);
        }
    }

    public boolean callLogEntryChangesMade(final String displayName,
                                           final String imAddress,
                                           final String personalPhone,
                                           final String conferenceNumber,
                                           final String conferenceId,
                                           final String securityPin) {
        if (mCallLogEntry != null) {
            return !TextUtils.equals(displayName, mCallLogEntry.getHumanReadableName()) ||
                    !TextUtils.isEmpty(imAddress) ||
                    !TextUtils.equals(PhoneNumberUtils.formatNumber(mCallLogEntry.getPhoneNumber(), Locale.getDefault().getCountry()),
                            PhoneNumberUtils.formatNumber(personalPhone, Locale.getDefault().getCountry())) ||
                    !TextUtils.isEmpty(conferenceNumber) ||
                    !TextUtils.isEmpty(conferenceId) ||
                    !TextUtils.isEmpty(securityPin);

        }

        return false;
    }

    public void setSavedContact(NextivaContact contact) {
        mSavedContact = contact;
    }

    public void setCallLogEntry(CallLogEntry entry) {
        mCallLogEntry = entry;
    }

    public CallLogEntry getCallLogEntry() {
        return mCallLogEntry;
    }

    // --------------------------------------------------------------------------------------------
    // XMPP Events
    // --------------------------------------------------------------------------------------------

    private void initRxEventListeners() {
        mCompositeDisposable.addAll(
                RxBus.INSTANCE.listen(RxEvents.ContactUpdatedResponseEvent.class)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(event -> {
                            if (event.isSuccessful()) {
                                mSaveContactMutableLiveData.setValue(Resource.success(new SaveNextivaContactEvent(mSavedContact, 0)));
                            } else {
                                mSaveContactMutableLiveData.setValue(Resource.error("", null));
                            }

                            setSavedContact(null);
                        }),

                RxBus.INSTANCE.listen(RxEvents.XmppErrorEvent.class)
                        .subscribeOn(mSchedulerProvider.io())
                        .observeOn(mSchedulerProvider.ui())
                        .subscribe(event -> {
                            mXmppErrorMutableLiveData.setValue(new SingleEvent<>(event));
                            mSaveContactMutableLiveData.setValue(Resource.error("", null));
                            setSavedContact(null);
                        }));
    }
    // --------------------------------------------------------------------------------------------

    public static class SaveNextivaContactEvent {
        private final NextivaContact mNextivaContact;
        @StringRes
        private final int mImAddressErrorResId;

        public SaveNextivaContactEvent(NextivaContact nextivaContact,
                                       @StringRes int imAddressErrorResId) {

            mNextivaContact = nextivaContact;
            mImAddressErrorResId = imAddressErrorResId;
        }

        public NextivaContact getNextivaContact() {
            return mNextivaContact;
        }

        @StringRes
        public int getImAddressErrorResId() {
            return mImAddressErrorResId;
        }
    }
}

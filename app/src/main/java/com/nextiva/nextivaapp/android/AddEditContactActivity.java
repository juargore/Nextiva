/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android;

import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.BACK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.SAVE_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.UNABLE_TO_SAVE_CONTACT_DIALOG_OK_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.UNABLE_TO_SAVE_CONTACT_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_CANCEL_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_DISCARD_BUTTON_PRESSED;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.UNSAVED_CHANGES_DIALOG_SHOWN;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ADD_CONFERENCE_CONTACT;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.ADD_ENTERPRISE_CONTACT;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.EDIT_CONFERENCE_CONTACT;
import static com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.EDIT_ENTERPRISE_CONTACT;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.annotation.StringDef;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.databinding.ActivityAddContactBinding;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.interfaces.EntryForm;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.models.CallLogEntry;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.models.Resource;
import com.nextiva.nextivaapp.android.models.SingleEvent;
import com.nextiva.nextivaapp.android.net.buses.RxEvents.XmppErrorEvent;
import com.nextiva.nextivaapp.android.util.GsonUtil;
import com.nextiva.nextivaapp.android.util.MenuUtil;
import com.nextiva.nextivaapp.android.util.ViewUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.textwatchers.ExtensionEnabledPhoneNumberFormattingTextWatcher;
import com.nextiva.nextivaapp.android.viewmodels.AddEditContactViewModel;
import com.nextiva.nextivaapp.android.xmpp.util.XmppDebuggingUtil;

import java.lang.annotation.Retention;
import java.util.Locale;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import kotlin.Unit;

/**
 * Created by adammacdonald on 2/8/18.
 */
@AndroidEntryPoint
public class AddEditContactActivity extends BaseActivity implements
        EntryForm {

    @Retention(SOURCE)
    @StringDef({
            ENTERPRISE_CONTACT,
            CONFERENCE_CONTACT
    })
    private @interface ScreenType {
    }

    private static final String ENTERPRISE_CONTACT = "com.nextiva.nextivaapp.android.ENTERPRISE_CONTACT";
    private static final String CONFERENCE_CONTACT = "com.nextiva.nextivaapp.android.CONFERENCE_CONTACT";

    private static final String PARAMS_SCREEN_TYPE = "PARAMS_SCREEN_TYPE";
    private static final String PARAMS_EDITING_CONTACT = "PARAMS_EDITING_CONTACT";
    private static final String PARAMS_CALL_LOG_ENTRY = "PARAMS_CALL_LOG_ENTRY";

    protected ViewGroup mMasterLayout;
    protected Toolbar mToolbar;
    protected TextInputLayout mDisplayNameTextInputLayout;
    protected EditText mDisplayNameEditText;
    protected LinearLayout mPersonalPhoneMasterLayout;
    protected TextInputLayout mPersonalPhoneTextInputLayout;
    protected EditText mPersonalPhoneEditText;
    protected LinearLayout mImAddressMasterLayout;
    protected TextInputLayout mImAddressTextInputLayout;
    protected EditText mImAddressEditText;
    protected RelativeLayout mConferenceNumberMasterLayout;
    protected TextInputLayout mConferenceNumberTextInputLayout;
    protected EditText mConferenceNumberEditText;
    protected TextInputLayout mConferenceIdTextInputLayout;
    protected EditText mConferenceIdEditText;
    protected TextInputLayout mSecurityPinTextInputLayout;
    protected EditText mSecurityPinEditText;

    private MaterialMenuDrawable mMaterialMenuDrawable;
    private MenuItem mSaveMenuItem;

    @Inject
    protected DialogManager mDialogManager;
    @Inject
    protected AnalyticsManager mAnalyticsManager;

    @ScreenType
    private String mScreenType;
    private AddEditContactViewModel mViewModel;

    private final Observer<NextivaContact> mEditingContactObserver = nextivaContact -> {
        if (nextivaContact != null) {
            setLayoutsHintAnimationEnabled(false);

            if (!TextUtils.isEmpty(nextivaContact.getJid())) {
                mImAddressEditText.setText(nextivaContact.getJid());
            }

            if (nextivaContact.getPhoneNumbers() != null && nextivaContact.getPhoneNumbers().size() > 0) {
                for (PhoneNumber phoneNumber : nextivaContact.getPhoneNumbers()) {
                    if (phoneNumber.getType() == Enums.Contacts.PhoneTypes.HOME_PHONE) {
                        mPersonalPhoneEditText.setText(phoneNumber.getNumber());
                        break;
                    }
                }
            }

            if (!TextUtils.isEmpty(nextivaContact.getDisplayName())) {
                mDisplayNameEditText.setText(nextivaContact.getDisplayName());
            }

            if (nextivaContact.getConferencePhoneNumbers() != null) {
                for (PhoneNumber conferenceNumber : nextivaContact.getConferencePhoneNumbers()) {
                    if (conferenceNumber.getType() == Enums.Contacts.PhoneTypes.CONFERENCE_PHONE) {
                        mConferenceIdEditText.setText(conferenceNumber.getPinOne());
                        mSecurityPinEditText.setText(conferenceNumber.getPinTwo());
                        //Setting Conference Number last because it will enable/disable the ID and PIN fields
                        mConferenceNumberEditText.setText(conferenceNumber.getNumber());
                        break;
                    }
                }
            }

            setLayoutsHintAnimationEnabled(true);
        }

        setSaveButtonEnabledState();
    };
    private final Observer<Resource<AddEditContactViewModel.SaveNextivaContactEvent>> mSaveContactObserver = resource -> {
        if (resource != null) {
            switch (resource.getStatus()) {
                case Enums.Net.StatusTypes.LOADING: {
                    mDialogManager.showProgressDialog(AddEditContactActivity.this, getAnalyticScreenName(), R.string.progress_processing);
                    if (mSaveMenuItem != null)
                        mSaveMenuItem.setEnabled(false);
                    break;
                }
                case Enums.Net.StatusTypes.SUCCESS: {
                    mDialogManager.dismissProgressDialog();

                    Intent resultIntent = new Intent();

                    if (resource.getData() != null && resource.getData().getNextivaContact() != null) {
                        resource.getData().getNextivaContact().setVCard(null);
                        resultIntent.putExtra(Constants.EXTRA_NEXTIVA_CONTACT_JSON, GsonUtil.getJSON(resource.getData().getNextivaContact()));
                    }

                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                    break;
                }
                case Enums.Net.StatusTypes.ERROR: {
                    mDialogManager.dismissProgressDialog();

                    if (TextUtils.isEmpty(resource.getMessage())) {
                        mDialogManager.showErrorDialog(AddEditContactActivity.this, getAnalyticScreenName());

                    } else {
                        mAnalyticsManager.logEvent(getAnalyticScreenName(), UNABLE_TO_SAVE_CONTACT_DIALOG_SHOWN);

                        mDialogManager.showDialog(
                                AddEditContactActivity.this,
                                getString(R.string.add_contact_incomplete_title),
                                resource.getMessage(),
                                getString(R.string.general_ok),
                                (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), UNABLE_TO_SAVE_CONTACT_DIALOG_OK_BUTTON_PRESSED));
                    }

                    if (resource.getData() != null) {
                        if (resource.getData().getImAddressErrorResId() != 0) {
                            mImAddressTextInputLayout.setError(getString(resource.getData().getImAddressErrorResId()));
                        }
                    }

                    setSaveButtonEnabledState();
                    break;
                }
            }
        }
    };
    private final Observer<SingleEvent<XmppErrorEvent>> mXmppErrorObserver = singleEvent -> {
        if (singleEvent != null && singleEvent.getContentIfNotHandled() != null) {
            mDialogManager.dismissProgressDialog();
            setSaveButtonEnabledState();
            XmppDebuggingUtil.displayDebugLogMessage(singleEvent.peekContent().getErrorException(), Thread.currentThread().getStackTrace()[2]);
        }
    };

    public static Intent newAddEnterpriseContactIntent(Context context) {
        return newIntent(context, ENTERPRISE_CONTACT, null, null);
    }

    public static Intent newEditEnterpriseContactIntent(Context context, NextivaContact editingContact) {
        return newIntent(context, ENTERPRISE_CONTACT, editingContact, null);
    }

    public static Intent newAddConferenceContactIntent(Context context) {
        return newIntent(context, CONFERENCE_CONTACT, null, null);
    }

    public static Intent newEditConferenceContactIntent(Context context, NextivaContact editingContact) {
        return newIntent(context, CONFERENCE_CONTACT, editingContact, null);
    }

    public static Intent newAddCallLogContactIntent(Context context, CallLogEntry callLogEntry) {
        return newIntent(context, ENTERPRISE_CONTACT, null, callLogEntry);
    }

    public static Intent newAddUnkownContactIntent(Context context, NextivaContact nextivaContact) {
        return newIntent(context, ENTERPRISE_CONTACT, nextivaContact, null);
    }

    private static Intent newIntent(Context context, @ScreenType String screenType, NextivaContact editingContact, CallLogEntry callLogEntry) {
        Intent intent = new Intent(context, AddEditContactActivity.class);
        intent.putExtra(PARAMS_SCREEN_TYPE, screenType);
        intent.putExtra(PARAMS_EDITING_CONTACT, editingContact);
        intent.putExtra(PARAMS_CALL_LOG_ENTRY, callLogEntry);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(bindViews());
        mViewModel = new ViewModelProvider(this).get(AddEditContactViewModel.class);
        mViewModel.getEditingContactLiveData().observe(this, mEditingContactObserver);
        mViewModel.getSaveContactLiveData().observe(this, mSaveContactObserver);
        mViewModel.getXmppErrorLiveData().observe(this, mXmppErrorObserver);

        NextivaContact editingContact = null;

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();

            if (extras.containsKey(PARAMS_SCREEN_TYPE)) {
                mScreenType = extras.getString(PARAMS_SCREEN_TYPE);
                editingContact = (NextivaContact) extras.getSerializable(PARAMS_EDITING_CONTACT);

                if (editingContact != null && editingContact.getContactType() == Enums.Contacts.ContactTypes.UNKNOWN) {
                    loadUnknownContactData(editingContact);
                }
            }

            if (extras.containsKey(PARAMS_CALL_LOG_ENTRY) && extras.getSerializable(PARAMS_CALL_LOG_ENTRY) != null) {
                CallLogEntry callLogEntry = (CallLogEntry) extras.getSerializable(PARAMS_CALL_LOG_ENTRY);
                mViewModel.setCallLogEntry(callLogEntry);
                loadCallLogEntryData(callLogEntry);
            }
        }

        if (TextUtils.isEmpty(mScreenType)) {
            finish();
        }

        mViewModel.setEditingContact(editingContact);

        setSupportActionBar(mToolbar);

        mMaterialMenuDrawable = new MaterialMenuDrawable(AddEditContactActivity.this,
                ContextCompat.getColor(AddEditContactActivity.this, R.color.white),
                MaterialMenuDrawable.Stroke.REGULAR);
        mMaterialMenuDrawable.setIconState(MaterialMenuDrawable.IconState.ARROW);
        mToolbar.setNavigationIcon(mMaterialMenuDrawable);
        mToolbar.setNavigationContentDescription(R.string.back_button_accessibility_id);

        mToolbar.setNavigationOnClickListener(v -> {
            if (mMaterialMenuDrawable.getIconState() == MaterialMenuDrawable.IconState.ARROW) {
                mAnalyticsManager.logEvent(getAnalyticScreenName(), BACK_BUTTON_PRESSED);
                onBackPressed();
            }
        });

        if (TextUtils.equals(ENTERPRISE_CONTACT, mScreenType)) {
            mSecurityPinEditText.setImeOptions(EditorInfo.IME_ACTION_NEXT);

            if (editingContact == null || editingContact.getContactType() == Enums.Contacts.ContactTypes.UNKNOWN) {
                setTitle(R.string.add_contact_enterprise_contact_title);
            } else {
                setTitle(getString(R.string.edit_contact_enterprise_contact_title));
            }

        } else if (TextUtils.equals(CONFERENCE_CONTACT, mScreenType)) {
            mSecurityPinEditText.setImeOptions(EditorInfo.IME_ACTION_DONE);

            if (editingContact == null) {
                setTitle(R.string.add_contact_conference_contact_title);
            } else {
                setTitle(getString(R.string.edit_contact_conference_edit_title));
            }

            mImAddressMasterLayout.setVisibility(View.GONE);
            mPersonalPhoneMasterLayout.setVisibility(View.GONE);
        }

        if (editingContact != null && editingContact.getContactType() != Enums.Contacts.ContactTypes.UNKNOWN) {
            mImAddressMasterLayout.setVisibility(View.GONE);
        }

        mConferenceNumberEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());
        mPersonalPhoneEditText.addTextChangedListener(new ExtensionEnabledPhoneNumberFormattingTextWatcher());

        EditTextExtensionsKt.makeClearableEditText(mDisplayNameEditText);
        EditTextExtensionsKt.makeClearableEditText(mConferenceIdEditText);
        EditTextExtensionsKt.makeClearableEditText(mConferenceNumberEditText);
        EditTextExtensionsKt.makeClearableEditText(mPersonalPhoneEditText);
        EditTextExtensionsKt.makeClearableEditText(mImAddressEditText, () -> {
            if (!mImAddressEditText.hasFocus()) {
                mImAddressEditText.setText(mViewModel.completeImAddressIfNeeded(mImAddressEditText.getText().toString()));
            }
            return Unit.INSTANCE;
        });
        EditTextExtensionsKt.makeClearableEditText(mSecurityPinEditText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAnalyticsManager.logScreenView(getAnalyticScreenName());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_contact, menu);

        MenuUtil.tintAllIcons(menu, ContextCompat.getColor(this, R.color.white));
        MenuUtil.setMenuContentDescriptions(menu);

        mSaveMenuItem = menu.findItem(R.id.add_contact_save);
        if (mSaveMenuItem != null)
            mSaveMenuItem.setEnabled(enableSaveButton());

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ViewUtil.hideKeyboard(mToolbar);

        if (item.getItemId() == R.id.add_contact_save) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), SAVE_BUTTON_PRESSED);
            saveContact();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View bindViews() {
        ActivityAddContactBinding binding = ActivityAddContactBinding.inflate(getLayoutInflater());

        mMasterLayout = binding.addContactActivityLayout;
        mToolbar = binding.addContactToolbar;
        mDisplayNameTextInputLayout = binding.addContactDisplayNameTextInputLayout;
        mDisplayNameEditText = binding.addContactDisplayNameEditText;
        mPersonalPhoneMasterLayout = binding.addContactPersonalPhoneMasterLayout;
        mPersonalPhoneTextInputLayout = binding.addContactPersonalPhoneTextInputLayout;
        mPersonalPhoneEditText = binding.addContactPersonalPhoneEditText;
        mImAddressMasterLayout = binding.addContactImAddressMasterLayout;
        mImAddressTextInputLayout = binding.addContactImAddressTextInputLayout;
        mImAddressEditText = binding.addContactImAddressEditText;
        mConferenceNumberMasterLayout = binding.addContactConferenceNumberMasterLayout;
        mConferenceNumberTextInputLayout = binding.addContactConferenceNumberTextInputLayout;
        mConferenceNumberEditText = binding.addContactConferenceNumberEditText;
        mConferenceIdTextInputLayout = binding.addContactConferenceIdTextInputLayout;
        mConferenceIdEditText = binding.addContactConferenceIdEditText;
        mSecurityPinTextInputLayout = binding.addContactSecurityPinLayout;
        mSecurityPinEditText = binding.addContactSecurityPinEditText;

        TextWatcher afterGeneralTextChangedWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterGeneralTextChanged();
            }
        };

        mDisplayNameEditText.addTextChangedListener(afterGeneralTextChangedWatcher);
        mConferenceIdEditText.addTextChangedListener(afterGeneralTextChangedWatcher);
        mSecurityPinEditText.addTextChangedListener(afterGeneralTextChangedWatcher);
        mPersonalPhoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterPersonalPhoneTextChanged();
            }
        });
        mImAddressEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterImAddressTextChanged();
            }
        });
        mConferenceNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                afterConferenceNumberTextChanged(editable);
            }
        });
        mConferenceNumberEditText.setOnEditorActionListener((textView, i, keyEvent) -> onPhoneNumberEditorAction(i));

        overrideEdgeToEdge(binding.getRoot());

        return binding.getRoot();
    }

    @Override
    public void onBackPressed() {
        cancelScreen();
    }

    protected void afterGeneralTextChanged() {
        setSaveButtonEnabledState();
    }

    protected void afterPersonalPhoneTextChanged() {
        mPersonalPhoneTextInputLayout.setError(null);
        setSaveButtonEnabledState();
    }

    protected void afterImAddressTextChanged() {
        mImAddressTextInputLayout.setError(null);
        setSaveButtonEnabledState();
    }

    protected void afterConferenceNumberTextChanged(Editable editable) {
        mConferenceIdEditText.setEnabled(editable.length() > 0);
        mSecurityPinEditText.setEnabled(editable.length() > 0);

        if (editable.length() == 0) {
            mConferenceIdEditText.setText("");
            mSecurityPinEditText.setText("");
        }

        mConferenceNumberTextInputLayout.setError(null);
        setSaveButtonEnabledState();
    }

    protected boolean onPhoneNumberEditorAction(int actionId) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            if (!mConferenceIdEditText.isEnabled()) {
                if (mImAddressMasterLayout.getVisibility() == View.VISIBLE) {
                    mImAddressMasterLayout.requestFocus();
                } else {
                    ViewUtil.hideKeyboard(mConferenceNumberEditText);
                    mMasterLayout.requestFocus();
                }

                return true;
            }
        }

        return false;
    }

    private String getAnalyticScreenName() {
        if (TextUtils.equals(ENTERPRISE_CONTACT, mScreenType)) {
            if (mViewModel.getEditingContactLiveData().getValue() != null) {
                return EDIT_ENTERPRISE_CONTACT;
            } else {
                return ADD_ENTERPRISE_CONTACT;
            }

        } else {
            if (mViewModel.getEditingContactLiveData().getValue() != null) {
                return EDIT_CONFERENCE_CONTACT;
            } else {
                return ADD_CONFERENCE_CONTACT;
            }
        }
    }

    private void loadCallLogEntryData(CallLogEntry callLogEntry) {
        if (!TextUtils.isEmpty(callLogEntry.getPhoneNumber())) {
            mPersonalPhoneEditText.setText(PhoneNumberUtils.formatNumber(callLogEntry.getPhoneNumber(),
                    Locale.getDefault().getCountry()));
        }

        if (!TextUtils.isEmpty(callLogEntry.getHumanReadableName())) {
            mDisplayNameEditText.setText(callLogEntry.getHumanReadableName());
        }

        setLayoutsHintAnimationEnabled(true);
    }

    private void loadUnknownContactData(NextivaContact contact) {
        if (contact.getPhoneNumbers() != null) {
            for (PhoneNumber number : contact.getPhoneNumbers()) {
                if (number.getType() == Enums.Contacts.PhoneTypes.PHONE) {
                    mPersonalPhoneEditText.setText(PhoneNumberUtils.formatNumber(number.getStrippedNumber(),
                            Locale.getDefault().getCountry()));
                }
            }
        }

        if (!TextUtils.isEmpty(contact.getJid())) {
            mImAddressEditText.setText(contact.getJid());
        }
    }

    private void saveContact() {
        if (!mConnectionStateManager.isInternetConnected()) {

            mDialogManager.showDialog(
                    AddEditContactActivity.this,
                    getString(R.string.add_contact_incomplete_title),
                    getString(R.string.error_no_internet_title),
                    getString(R.string.general_ok),
                    (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), UNABLE_TO_SAVE_CONTACT_DIALOG_OK_BUTTON_PRESSED));
            return;
        }

        mPersonalPhoneTextInputLayout.setError(null);
        mImAddressTextInputLayout.setError(null);
        mConferenceIdTextInputLayout.setError(null);

        if (TextUtils.equals(mScreenType, ENTERPRISE_CONTACT)) {
            mViewModel.validateEnterpriseContact(mDisplayNameEditText.getText().toString(),
                    mImAddressEditText.getText().toString(),
                    mPersonalPhoneEditText.getText().toString(),
                    mConferenceNumberEditText.getText().toString(),
                    mConferenceIdEditText.getText().toString(),
                    mSecurityPinEditText.getText().toString());

        } else if (TextUtils.equals(mScreenType, CONFERENCE_CONTACT)) {
            mViewModel.validateConferenceContact(mDisplayNameEditText.getText().toString(),
                    mConferenceNumberEditText.getText().toString(),
                    mConferenceIdEditText.getText().toString(),
                    mSecurityPinEditText.getText().toString());
        }
    }

    private void setLayoutsHintAnimationEnabled(boolean isEnabled) {
        mImAddressTextInputLayout.setHintAnimationEnabled(isEnabled);
        mPersonalPhoneTextInputLayout.setHintAnimationEnabled(isEnabled);
        mDisplayNameTextInputLayout.setHintAnimationEnabled(isEnabled);
        mConferenceNumberTextInputLayout.setHintAnimationEnabled(isEnabled);
        mConferenceIdTextInputLayout.setHintAnimationEnabled(isEnabled);
        mSecurityPinTextInputLayout.setHintAnimationEnabled(isEnabled);
    }

    private void cancelScreen() {
        ViewUtil.hideKeyboard(mToolbar);

        if (mViewModel.getCallLogEntry() != null && !callLogEntryChangesMade()) {
            super.onBackPressed();
            return;
        }

        if (changesMade()) {
            mAnalyticsManager.logEvent(getAnalyticScreenName(), UNSAVED_CHANGES_DIALOG_SHOWN);

            mDialogManager.showDialog(
                    AddEditContactActivity.this,
                    0,
                    R.string.error_unsaved_changes_title,
                    R.string.general_discard,
                    (dialog, which) -> {
                        AddEditContactActivity.super.onBackPressed();
                        mAnalyticsManager.logEvent(getAnalyticScreenName(), UNSAVED_CHANGES_DIALOG_DISCARD_BUTTON_PRESSED);
                    },
                    R.string.general_cancel,
                    (dialog, which) -> mAnalyticsManager.logEvent(getAnalyticScreenName(), UNSAVED_CHANGES_DIALOG_CANCEL_BUTTON_PRESSED));

        } else {
            super.onBackPressed();
        }
    }

    public boolean callLogEntryChangesMade() {
        return mViewModel.callLogEntryChangesMade(mDisplayNameEditText.getText().toString(),
                mImAddressEditText.getText().toString(),
                mPersonalPhoneEditText.getText().toString(),
                mConferenceNumberEditText.getText().toString(),
                mConferenceIdEditText.getText().toString(),
                mSecurityPinEditText.getText().toString());
    }

    private void setSaveButtonEnabledState() {
        if (mSaveMenuItem != null) {
            mSaveMenuItem.setEnabled(enableSaveButton());
        }
    }

    // --------------------------------------------------------------------------------------------
    // region EntryForm Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public boolean changesMade() {
        return mViewModel.changesMade(mDisplayNameEditText.getText().toString(),
                mImAddressEditText.getText().toString(),
                mPersonalPhoneEditText.getText().toString(),
                mConferenceNumberEditText.getText().toString(),
                mConferenceIdEditText.getText().toString(),
                mSecurityPinEditText.getText().toString());
    }

    @Override
    public boolean enableSaveButton() {
        return changesMade();
    }

    @Override
    public boolean enableDeleteButton() {
        return false;
    }
    // --------------------------------------------------------------------------------------------
    // endregion EntryForm Methods
    // --------------------------------------------------------------------------------------------
}

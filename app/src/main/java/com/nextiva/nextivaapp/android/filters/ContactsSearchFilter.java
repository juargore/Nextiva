package com.nextiva.nextivaapp.android.filters;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.db.model.EmailAddress;
import com.nextiva.nextivaapp.android.db.model.PhoneNumber;
import com.nextiva.nextivaapp.android.models.NextivaContact;
import com.nextiva.nextivaapp.android.util.CallUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;

/**
 * Created by Thaddeus Dannar on 3/9/18.
 */

public class ContactsSearchFilter extends Filter {

    private List<NextivaContact> mNextivaContactsList;
    private final ContactSearchFilterCallback mFilterCallback;

    private final Queue<Boolean> mIsFullListQueue = new LinkedList<>();

    public ContactsSearchFilter(ContactSearchFilterCallback filterCallback) {
        mFilterCallback = filterCallback;
    }

    private static boolean filterContactsByString(@Nullable NextivaContact contact, @Nullable String input) {
        if (contact == null) {
            return false;

        } else if (TextUtils.isEmpty(input)) {
            return true;
        }

        if ((contact.getFirstName() + " " + contact.getLastName()).toLowerCase().contains(input) ||
                contact.getFirstName() != null && contact.getFirstName().toLowerCase().contains(input) ||
                contact.getLastName() != null && contact.getLastName().toLowerCase().contains(input) ||
                contact.getCompany() != null && contact.getCompany().toLowerCase().contains(input) ||
                contact.getDisplayName() != null && contact.getDisplayName().toLowerCase().contains(input) ||
                contact.getUiName() != null && contact.getUiName().toLowerCase().contains(input) ||
                contact.getJid() != null && contact.getJid().toLowerCase().contains(input)) {
            return true;
        }

        if (contact.getPhoneNumbers() != null) {
            for (PhoneNumber number : contact.getPhoneNumbers()) {
                if (number.getNumber() != null && number.getNumber().contains(input) ||
                        (PhoneNumberUtils.formatNumber(number.getNumber(), Locale.getDefault().getCountry()) != null &&
                                PhoneNumberUtils.formatNumber(number.getNumber(), Locale.getDefault().getCountry()).contains(input)) ||
                        CallUtil.getStrippedPhoneNumber(number.getNumber()).contains(input)) {
                    return true;
                }
            }
        }

        if (contact.getEmailAddresses() != null) {
            for (EmailAddress email : contact.getEmailAddresses()) {
                if (email.getAddress() != null && email.getAddress().toLowerCase().contains(input)) {
                    return true;
                }
            }
        }

        if (contact.getExtensions() != null) {
            for (PhoneNumber extension : contact.getExtensions()) {
                if (extension.getNumber() != null && extension.getNumber().toLowerCase().contains(input) ||
                        (PhoneNumberUtils.formatNumber(extension.getNumber(), Locale.getDefault().getCountry()) != null &&
                                PhoneNumberUtils.formatNumber(extension.getNumber(), Locale.getDefault().getCountry()).contains(input))) {
                    return true;
                }
            }
        }

        if (contact.getConferencePhoneNumbers() != null) {
            for (PhoneNumber conferenceNumber : contact.getConferencePhoneNumbers()) {
                if (!TextUtils.isEmpty(conferenceNumber.getAssembledPhoneNumber()) && conferenceNumber.getAssembledPhoneNumber().contains(input) ||
                        PhoneNumberUtils.formatNumber(conferenceNumber.getNumber(), Locale.getDefault().getCountry()) != null &&
                                PhoneNumberUtils.formatNumber(conferenceNumber.getNumber(), Locale.getDefault().getCountry()).contains(input)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void filter(List<NextivaContact> nextivaContactsList, CharSequence constraint) {
        mNextivaContactsList = nextivaContactsList;
        super.filter(constraint);
    }

    public void filter(CharSequence constraint, boolean isFullList) {
        super.filter(constraint);
        mIsFullListQueue.add(isFullList);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();

        if (!TextUtils.isEmpty(constraint)) {
            filterResults.values = filterContacts(constraint.toString());
        } else {
            filterResults.values = mNextivaContactsList;
        }

        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        Boolean isFullList = mIsFullListQueue.poll();

        if (mFilterCallback != null) {
            mFilterCallback.onContactSearchFilterResults((List<NextivaContact>) results.values, isFullList == null ? true : isFullList);
        }
    }

    private List<NextivaContact> filterContacts(String input) {
        if (TextUtils.isEmpty(input)) {
            return mNextivaContactsList;

        } else {
            List<NextivaContact> contactsListFiltered = new ArrayList<>();
            for (NextivaContact nextivaContact : mNextivaContactsList) {
                if (filterContactsByString(nextivaContact, input.toLowerCase())) {
                    contactsListFiltered.add(nextivaContact);
                }
            }

            return contactsListFiltered;
        }
    }

    public List<NextivaContact> filterContacts(@NonNull List<NextivaContact> source, String input) {
        if (TextUtils.isEmpty(input)) {
            return source;

        } else {
            List<NextivaContact> contactsListFiltered = new ArrayList<>();
            for (NextivaContact nextivaContact : source) {
                if (filterContactsByString(nextivaContact, input.toLowerCase())) {
                    contactsListFiltered.add(nextivaContact);
                }
            }

            return contactsListFiltered;
        }
    }

    public interface ContactSearchFilterCallback {
        void onContactSearchFilterResults(List<NextivaContact> nextivaContactsList, boolean isFullList);
    }
}

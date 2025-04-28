package com.nextiva.nextivaapp.android.filters;

import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nextiva.nextivaapp.android.models.CallLogEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CallHistorySearchFilter extends Filter {

    private List<CallLogEntry> mCallLogEntryList;
    private final CallLogEntrySearchFilterCallback mFilterCallback;

    public CallHistorySearchFilter(CallLogEntrySearchFilterCallback callLogEntrySearchFilterCallback) {
        mFilterCallback = callLogEntrySearchFilterCallback;
    }

    public static boolean filterCallLogEntriesByString(@Nullable CallLogEntry callLogEntry, @Nullable String input) {
        if (callLogEntry == null) {
            return false;
        } else if (TextUtils.isEmpty(input)) {
            return true;
        }

        return isCallLogEntriesContainsName(callLogEntry, input) ||
                isCallLogEntriesContainsNumber(callLogEntry, input);

    }

    private static boolean isCallLogEntriesContainsName(@NonNull CallLogEntry callLogEntry, @NonNull String input) {
        if (callLogEntry.getUiName() != null && !callLogEntry.getUiName().equals(callLogEntry.getDisplayName())) {
            return callLogEntry.getUiName().toLowerCase().contains(input);
        }

        return callLogEntry.getDisplayName() != null && callLogEntry.getDisplayName().toLowerCase().contains(input);
    }

    private static boolean isCallLogEntriesContainsNumber(@NonNull CallLogEntry callLogEntry, @NonNull String input) {
        return callLogEntry.getPhoneNumber() != null && callLogEntry.getPhoneNumber().toLowerCase().contains(input) ||
                PhoneNumberUtils.formatNumber(callLogEntry.getPhoneNumber(), Locale.getDefault().getCountry()) != null &&
                        PhoneNumberUtils.formatNumber(callLogEntry.getPhoneNumber(), Locale.getDefault().getCountry()).contains(input);
    }

    @Override
    public CharSequence convertResultToString(Object resultValue) {
        return super.convertResultToString(resultValue);
    }

    public void filter(List<CallLogEntry> callLogEntries, CharSequence constraint) {
        mCallLogEntryList = callLogEntries;
        super.filter(constraint);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults filterResults = new FilterResults();
        filterResults.values = filterCallLogEntries(constraint.toString());
        return filterResults;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (mFilterCallback != null) {
            mFilterCallback.onCallLogEntrySearchFilterResults((List<CallLogEntry>) results.values);
        }
    }

    private List<CallLogEntry> filterCallLogEntries(String input) {
        if (TextUtils.isEmpty(input)) {
            return mCallLogEntryList;
        } else {
            List<CallLogEntry> callLogEntriesListFiltered = new ArrayList<>();
            for (CallLogEntry callLogEntry : mCallLogEntryList) {
                if (filterCallLogEntriesByString(callLogEntry, input.toLowerCase())) {
                    callLogEntriesListFiltered.add(callLogEntry);
                }
            }

            return callLogEntriesListFiltered;
        }
    }

    public List<CallLogEntry> filterCallLogEntries(@NonNull List<CallLogEntry> source, String input) {
        if (TextUtils.isEmpty(input)) {
            return source;
        } else {
            List<CallLogEntry> callLogEntriesListFiltered = new ArrayList<>();
            for (CallLogEntry callLogEntry : source) {
                if (filterCallLogEntriesByString(callLogEntry, input.toLowerCase())) {
                    callLogEntriesListFiltered.add(callLogEntry);
                }
            }

            return callLogEntriesListFiltered;
        }
    }

    public interface CallLogEntrySearchFilterCallback {
        void onCallLogEntrySearchFilterResults(List<CallLogEntry> callLogEntries);
    }
}

/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view.textwatchers;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;

import androidx.annotation.NonNull;

import com.nextiva.nextivaapp.android.util.CallUtil;

public class ExtensionEnabledPhoneNumberFormattingTextWatcher extends PhoneNumberFormattingTextWatcher {

    /**
     * Used to track if the text watcher is changing itself.  This is to prevent
     * causing a StackOverflowException via recursive calls back to this TextWatcher
     */
    private boolean mSelfChange = false;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        if (mSelfChange ||
                (s.length() > 0 && s.length() == count && start == 0) ||
                after == 0) {

            return;
        }

        super.beforeTextChanged(s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSelfChange) {
            return;
        }

        super.onTextChanged(s, start, before, count);
    }

    @Override
    public synchronized void afterTextChanged(Editable editable) {
        if (mSelfChange) {
            return;
        }

        // Allow only one plus at the start of the number
        limitPluses(editable);

        // Clean of all non-functional characters
        String sEditable = editable.toString();
        String cleaned = CallUtil.cleanForTextWatcher(sEditable);

        if (cleaned.length() > 4) {
            String trim = sEditable.trim();
            if(!trim.equals(sEditable)) {
                mSelfChange = true;
                editable.replace(0, editable.length(), trim, 0, trim.length());
                mSelfChange = false;
            } else {
                super.afterTextChanged(editable);
            }
        } else {
            mSelfChange = true;
            editable.replace(0, editable.length(), cleaned, 0, cleaned.length());
            mSelfChange = false;
        }
    }

    /**
     * Allow only one plus <code>[+]</code> character at the beginning of the number.  All other pluses will be removed
     * <br>
     * This method will affect {@link ExtensionEnabledPhoneNumberFormattingTextWatcher#mSelfChange} eventually setting to <code>false</code> at the end of this method
     */
    private void limitPluses(@NonNull Editable editable) {
        mSelfChange = true;

        int length = editable.length();
        if (length > 1) {
            for (int i = 1; i < length; i++) {
                if ('+' == editable.charAt(i)) {
                    editable.delete(i, i + 1);
                    length--;
                }
            }
        }

        mSelfChange = false;
    }
}

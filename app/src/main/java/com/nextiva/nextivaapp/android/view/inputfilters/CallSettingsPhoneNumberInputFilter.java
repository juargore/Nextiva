/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.view.inputfilters;

import android.text.InputFilter;
import android.text.Spanned;

import com.nextiva.nextivaapp.android.constants.Constants;

public class CallSettingsPhoneNumberInputFilter implements InputFilter {

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        if (source != null) {
            if (Constants.CALL_SETTINGS_INVALID_PHONE_SPECIAL_CHARACTERS.contains(("" + source))) {
                return "";

            } else if (source.length() > 1 && source.toString().matches(".*[" + Constants.CALL_SETTINGS_INVALID_PHONE_SPECIAL_CHARACTERS + "].*")) {
                return source.toString().replaceAll("[" + Constants.CALL_SETTINGS_INVALID_PHONE_SPECIAL_CHARACTERS + "]", "");
            }
        }

        return null;
    }
}

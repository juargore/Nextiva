package com.nextiva.nextivaapp.android.view.textwatchers;

import android.text.Editable;
import android.text.TextUtils;

import com.afollestad.materialdialogs.internal.MDButton;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.util.CallUtil;

/**
 * Created by Thaddeus Dannar on 2019-08-07.
 */
public class TextListenerDialogPhoneNumber extends ExtensionEnabledPhoneNumberFormattingTextWatcher {
    private final MDButton mPositiveButton;

    public TextListenerDialogPhoneNumber(MDButton positiveButton) {
        mPositiveButton = positiveButton;
    }

    @Override
    public synchronized void afterTextChanged(final Editable editable) {
        super.afterTextChanged(editable);
        if (mPositiveButton != null) {
            mPositiveButton.setEnabled(!TextUtils.isEmpty(editable.toString()) && CallUtil.getStrippedPhoneNumber(editable.toString()).length() >= Constants.CharacterLimits.MINIMUM_PHONE_NUMBER_LENGTH);
        }

    }
}

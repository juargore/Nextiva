/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.BuildConfig;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Constants;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;
import com.nextiva.nextivaapp.android.util.ApplicationUtil;
import com.nextiva.nextivaapp.android.util.CallUtil;
import com.nextiva.nextivaapp.android.util.extensions.EditTextExtensionsKt;
import com.nextiva.nextivaapp.android.view.textwatchers.TextListenerDialogPhoneNumber;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by adammacdonald on 3/21/18.
 */

@Singleton
public class NextivaDialogManager implements DialogManager {

    private MaterialDialog mCurrentMaterialDialog;
    private MaterialDialog mCurrentProgressDialog;
    private AlertDialog mCurrentAlertDialog;

    private final AnalyticsManager mAnalyticsManager;
    private final SettingsManager mSettingsManager;

    @Inject
    public NextivaDialogManager(AnalyticsManager analyticsManager,
                                SettingsManager settingsManager) {
        mAnalyticsManager = analyticsManager;
        mSettingsManager = settingsManager;
    }

    private void dismissDialogWithCheck(@Nullable Dialog dialog) {
        if (dialog != null) {
            if (dialog.isShowing()) {
                //get the Context that was used to create the dialog
                Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();

                // if the Context used here was an activity AND it hasn't been finished
                // or destroyed then dismiss the dialog
                if (context instanceof Activity) {
                    if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                        dismissDialogWithTryCatch(dialog);
                    }

                } else {
                    // if the Context used wasn't an Activity, then dismiss the dialog
                    dismissDialogWithTryCatch(dialog);
                }
            }
        }
    }

    private void dismissDialogWithTryCatch(@NonNull Dialog dialog) {
        try {
            dialog.dismiss();
        } catch (final IllegalArgumentException e) {
            // Do nothing.
            // MOBILE-2583 Was happening because an IllegalArgumentException was thrown
            // when a dialog was attempted to be dismissed when its parent activity
            // had already been finished.  This catch is to prevent the crash in this
            // known scenario, even though it doesn't happen too often.
        }
    }

    // --------------------------------------------------------------------------------------------
    // DialogManager Methods
    // --------------------------------------------------------------------------------------------
    @Override
    public MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName) {

        return showErrorDialog(context, analyticsScreenName, (dialog, which) -> mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.GENERIC_ERROR_DIALOG_OK_BUTTON_PRESSED));
    }

    @Override
    public MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback) {

        return showErrorDialog(context,
                               analyticsScreenName,
                               null,
                               context.getString(R.string.error_general_error_title),
                               context.getString(R.string.error_general_error_message),
                               neutralCallback);
    }

    @Override
    public MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable @Enums.Analytics.EventName.Event String eventName,
            @Nullable String title,
            @Nullable String content,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback) {

        mAnalyticsManager.logEvent(analyticsScreenName, eventName != null ? eventName : Enums.Analytics.EventName.GENERIC_ERROR_DIALOG_SHOWN);

        return showDialog(
                context,
                title,
                content,
                context.getString(R.string.general_ok),
                neutralCallback);
    }



    @Override
    public MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable @Enums.Analytics.EventName.Event String eventName,
            @Nullable String title,
            @Nullable String content,
            @NonNull String suggestionButtonTitle,
            @NonNull MaterialDialog.SingleButtonCallback suggestionCallback,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback)
    {

        mAnalyticsManager.logEvent(analyticsScreenName, eventName != null ? eventName : Enums.Analytics.EventName.GENERIC_ERROR_DIALOG_SHOWN);

        return showDialog(
                context,
                title,
                content,
                suggestionButtonTitle,
                suggestionCallback,
                context.getString(R.string.general_ok),
                neutralCallback);
    }


    @Override
    public MaterialDialog showProgressDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            int contentId) {

        return showProgressDialog(context, analyticsScreenName, 0, contentId);
    }

    @Override
    public MaterialDialog showProgressDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @StringRes int titleId,
            @StringRes int contentId) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissAllDialogs();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .progress(true, 0)
                .cancelable(false);

        if (titleId != 0) {
            builder.title(titleId);
        }

        if (contentId != 0) {
            builder.content(contentId);
        }

        mCurrentProgressDialog = builder.show();

        if (mCurrentProgressDialog.getContentView() != null) {
            mCurrentProgressDialog.getContentView().setContentDescription(context.getString(contentId).replace("…", ""));
        }

        mAnalyticsManager.logEvent(analyticsScreenName, Enums.Analytics.EventName.PROGRESS_DIALOG_SHOWN);

        return mCurrentProgressDialog;
    }

    @Override
    public MaterialDialog showDialog(
            @NonNull Context context,
            @StringRes int titleId,
            @StringRes int contentId,
            @StringRes int positiveButtonId,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback) {

        return showDialog(
                context,
                (titleId != 0) ? context.getString(titleId) : "",
                (contentId != 0) ? context.getString(contentId) : "",
                (positiveButtonId != 0) ? context.getString(positiveButtonId) : "",
                positiveCallback);
    }

    @Override
    public MaterialDialog showDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @Nullable String neutralButton,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false);

        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }

        if (!TextUtils.isEmpty(neutralButton)) {
            builder.positiveText(neutralButton);
        }

        builder.onPositive(neutralCallback);
        builder.positiveColor(ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? ContextCompat.getColor(context, R.color.white) : ContextCompat.getColor(context, R.color.black));

        mCurrentMaterialDialog = builder.show();
        mCurrentMaterialDialog.getActionButton(DialogAction.NEUTRAL)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, neutralButton));

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialDialog showDialog(
            @NonNull Context context,
            @StringRes int titleId,
            @StringRes int contentId,
            @StringRes int positiveButtonId,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @StringRes int negativeButtonId,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback) {

        return showDialog(
                context,
                (titleId != 0) ? context.getString(titleId) : "",
                (contentId != 0) ? context.getString(contentId) : "",
                (positiveButtonId != 0) ? context.getString(positiveButtonId) : "",
                positiveCallback,
                (negativeButtonId != 0) ? context.getString(negativeButtonId) : "",
                negativeCallback);
    }

    @Override
    public MaterialDialog showDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @Nullable String positiveButton,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @Nullable String negativeButton,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false);

        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }

        if (!TextUtils.isEmpty(positiveButton)) {
            builder.positiveText(positiveButton);
        }

        builder.onPositive(positiveCallback);

        if (!TextUtils.isEmpty(negativeButton)) {
            builder.negativeText(negativeButton);
        }

        builder.onNegative(negativeCallback);

        builder.positiveColor(ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? ContextCompat.getColor(context, R.color.white) : ContextCompat.getColor(context, R.color.black));
        builder.negativeColor(ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? ContextCompat.getColor(context, R.color.white) : ContextCompat.getColor(context, R.color.black));

        mCurrentMaterialDialog = builder.show();
        mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, positiveButton));
        mCurrentMaterialDialog.getActionButton(DialogAction.NEGATIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, negativeButton));

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback) {

        return showSimpleListDialog(context, title, null, items, listener, negativeCallback);
    }

    @Override
    public MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback) {
        return showSimpleListDialog(context, title, content, items, listener, negativeCallback, ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? R.color.white : R.color.black);
    }

    @Override
    public MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            int itemColor) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false)
                .items(items)
                .itemsColor(ContextCompat.getColor(context, itemColor))
                .itemsCallback((dialog, itemView, position, text) -> listener.onSelectionMade(position))
                .negativeText(R.string.general_cancel)
                .negativeColor(ContextCompat.getColor(context, itemColor))
                .onNegative(negativeCallback);

        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }

        mCurrentMaterialDialog = builder.show();

        if (mCurrentMaterialDialog.getRecyclerView() != null) {
            for (int i = 0; i < mCurrentMaterialDialog.getRecyclerView().getChildCount(); i++) {
                mCurrentMaterialDialog.getRecyclerView().getChildAt(i).setContentDescription(context.getString(R.string.dialog_item_list_content_description, i));
            }
        }

        mCurrentMaterialDialog.getActionButton(DialogAction.NEGATIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_cancel)));

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialDialog showTwoLineListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            int itemColor,
            @NonNull RecyclerView.Adapter listAdapter) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false)
                .itemsColor(ContextCompat.getColor(context, itemColor))
                .itemsCallback((dialog, itemView, position, text) -> listener.onSelectionMade(position))
                .adapter(listAdapter, null)
                .positiveText(R.string.general_ok)
                .positiveColor(ContextCompat.getColor(context, itemColor));

        if (!TextUtils.isEmpty(title)) {
            builder.title(title);
        }

        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }

        mCurrentMaterialDialog = builder.show();

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialAlertDialogBuilder showOptionsAlertDialog(@NonNull Context context, @Nullable String title, @NonNull CharSequence[] items, DialogInterface.OnClickListener listener) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context)
                .setCancelable(true)
                .setItems(items, listener)
                .setTitle(title);

        mCurrentAlertDialog = builder.show();

        return null;
    }


    @Override
    public MaterialDialog showEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
            final int inputType,
            final int inputLength,
            @Nullable String defaultText,
            @Nullable String hintText,
            @NonNull final MaterialDialog.SingleButtonCallback singleButtonCallback) {
        return showEditTextDialog(context, titleId, inputType, inputLength, defaultText, null, hintText, null, singleButtonCallback, null, null, ApplicationUtil.isNightModeEnabled(context, mSettingsManager) ? R.color.white : R.color.black);
    }

    @Override
    public MaterialDialog showEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
            final int inputType,
            final int inputLength,
            @Nullable String defaultText,
            @Nullable String prepopulateText,
            @Nullable String hintText,
            @Nullable String positiveButton,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @Nullable String negativeButton,
            @Nullable MaterialDialog.SingleButtonCallback negativeCallback,
            @ColorRes int itemColor) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false)
                .inputType(inputType);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            FrameLayout textInputFrameLayout = (FrameLayout) FrameLayout.inflate(context, R.layout.view_text_input_layout, null);
            AppCompatEditText editText = textInputFrameLayout.findViewById(R.id.text_input_dialog_edit_text);
            EditTextExtensionsKt.makeClearableEditText(editText);
            builder.customView(textInputFrameLayout, true);
        }

        if (!TextUtils.isEmpty(positiveButton)) {
            builder.positiveText(positiveButton);
        } else {
            builder.positiveText(R.string.general_ok);
        }

        builder.onPositive(positiveCallback);

        if (itemColor != 0) {
            builder.positiveColor(ContextCompat.getColor(context, itemColor));
        }

        if (!TextUtils.isEmpty(negativeButton)) {
            builder.negativeText(negativeButton);
        } else {
            builder.negativeText(R.string.general_cancel);
        }

        if (negativeCallback != null) {
            builder.onNegative(negativeCallback);
        }

        if (itemColor != 0) {
            builder.negativeColor(ContextCompat.getColor(context, itemColor));
        }


        if (titleId != 0) {
            builder.title(titleId);
        }

        mCurrentMaterialDialog = builder.show();

        mCurrentMaterialDialog.getActionButton(DialogAction.NEGATIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_cancel)));
        mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_ok)));

        if (mCurrentMaterialDialog.getCustomView() != null) {
            EditText dialogEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.text_input_dialog_edit_text);
            TextInputLayout dialogTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.text_input_dialog_input_layout);

            dialogEditText.setInputType(inputType);
            dialogEditText.setContentDescription(context.getString(R.string.dialog_edit_text_content_description));

            if (!TextUtils.isEmpty(defaultText)) {
                dialogTextInputLayout.setHint(defaultText);
                dialogEditText.setText(defaultText, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(true);
            }

            if (!TextUtils.isEmpty(prepopulateText)) {
                dialogEditText.setText(prepopulateText, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(hintText)) {
                dialogTextInputLayout.setHint(hintText);
            }

            if (inputType == InputType.TYPE_CLASS_PHONE) {

                MDButton positiveMDButton = mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE);

                positiveMDButton.setEnabled(!TextUtils.isEmpty(prepopulateText) && CallUtil.getStrippedPhoneNumber(prepopulateText).length() >= Constants.CharacterLimits.MINIMUM_PHONE_NUMBER_LENGTH);

                dialogEditText.addTextChangedListener(new TextListenerDialogPhoneNumber(positiveMDButton));
            }


            dialogEditText.setFilters(new InputFilter[] {
                    new InputFilter.LengthFilter(inputLength)
            });
            dialogTextInputLayout.setCounterMaxLength(inputLength);

        }

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialDialog showEndpointEditTextDialog(@NonNull Context context,
                                                     int titleId,
                                                     @Nullable String defaultHostname,
                                                     @Nullable String prepopulateHostname,
                                                     @Nullable String defaultBroadsoftApiUrl,
                                                     @Nullable String prepopulateBroadsoftApiUrl,
                                                     @Nullable String defaultPlatformApiUrl,
                                                     @Nullable String prepopulatePlatformApiUrl,
                                                     @Nullable String defaultMessageApiUrl,
                                                     @Nullable String prepopulateMessageApiUrl,
                                                     @Nullable String defaultAuthProxyPublicHost,
                                                     @Nullable String prepopulateAuthProxyPublicHost,
                                                     @Nullable String defaultAuthProxyRestApi,
                                                     @Nullable String prepopulateAuthProxyRestApi,
                                                     @Nullable String defaultAuthTenantRestApi,
                                                     @Nullable String prepopulateAuthTenantRestApi,
                                                     @Nullable String defaultGeneralRestApi,
                                                     @Nullable String prepopulateGeneralRestApi,
                                                     @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
                                                     @NonNull MaterialDialog.SingleButtonCallback neutralCallback,
                                                     @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
                                                     int itemColor) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false)
                .inputType(InputType.TYPE_CLASS_TEXT);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            View root = inflater.inflate(R.layout.view_endpoint_text_edit_layout, null);
            LinearLayout textInputFrameLayout = root.findViewById(R.id.text_input_view_layout);
            AppCompatEditText hostnameEditText = textInputFrameLayout.findViewById(R.id.hostname_text_input_dialog_edit_text);
            AppCompatEditText broadsoftApiUrlEditText = textInputFrameLayout.findViewById(R.id.broadsoft_api_url_text_input_dialog_edit_text);
            AppCompatEditText platformApiUrlEditText = textInputFrameLayout.findViewById(R.id.platform_api_url_text_input_dialog_edit_text);
            AppCompatEditText messageApiUrlEditText = textInputFrameLayout.findViewById(R.id.message_api_url_text_input_dialog_edit_text);

            AppCompatEditText authProxyPublicHostEditText = textInputFrameLayout.findViewById(R.id.auth_proxy_public_host_text_input_dialog_edit_text);
            AppCompatEditText authProxyRestApiEditText = textInputFrameLayout.findViewById(R.id.auth_proxy_rest_api_text_input_dialog_edit_text);
            AppCompatEditText authProxyTenantRestApiEditText = textInputFrameLayout.findViewById(R.id.auth_tenant_rest_api_text_input_dialog_edit_text);
            AppCompatEditText generalRestApiEditText = textInputFrameLayout.findViewById(R.id.general_rest_api_text_input_dialog_edit_text);

            EditTextExtensionsKt.makeClearableEditText(hostnameEditText);
            EditTextExtensionsKt.makeClearableEditText(broadsoftApiUrlEditText);
            EditTextExtensionsKt.makeClearableEditText(platformApiUrlEditText);
            EditTextExtensionsKt.makeClearableEditText(messageApiUrlEditText);

            EditTextExtensionsKt.makeClearableEditText(authProxyPublicHostEditText);
            EditTextExtensionsKt.makeClearableEditText(authProxyRestApiEditText);
            EditTextExtensionsKt.makeClearableEditText(authProxyTenantRestApiEditText);
            EditTextExtensionsKt.makeClearableEditText(generalRestApiEditText);

            builder.customView(textInputFrameLayout, true);
        }

        builder.positiveText(R.string.general_ok);
        builder.negativeText(R.string.general_cancel);
        builder.neutralText(R.string.endpoints_neutral_text);

        builder.onPositive(positiveCallback);

        if (itemColor != 0) {
            builder.positiveColor(ContextCompat.getColor(context, itemColor));
        }

        builder.onNeutral(neutralCallback);

        if (itemColor != 0) {
            builder.neutralColor(ContextCompat.getColor(context, itemColor));
        }

        builder.onNegative(negativeCallback);

        if (itemColor != 0) {
            builder.negativeColor(ContextCompat.getColor(context, itemColor));
        }


        if (titleId != 0) {
            builder.title(titleId);
        }

        mCurrentMaterialDialog = builder.show();

        mCurrentMaterialDialog.getActionButton(DialogAction.NEGATIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_cancel)));
        mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_ok)));
        mCurrentMaterialDialog.getActionButton(DialogAction.NEUTRAL)
                .setContentDescription(context.getString(R.string.dialog_reset_to_defaults_content_description));

        if (mCurrentMaterialDialog.getCustomView() != null) {
            EditText hostnameEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.hostname_text_input_dialog_edit_text);
            TextInputLayout hostnameTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.hostname_text_input_dialog_input_layout);
            EditText broadsoftApiUrlEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.broadsoft_api_url_text_input_dialog_edit_text);
            TextInputLayout broadsoftApiUrlTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.broadsoft_api_url_text_input_dialog_input_layout);
            EditText platformApiUrlEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.platform_api_url_text_input_dialog_edit_text);
            TextInputLayout platformApiUrlTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.platform_api_url_text_input_dialog_input_layout);
            EditText messageApiUrlEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.message_api_url_text_input_dialog_edit_text);
            TextInputLayout messageApiUrlTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.message_api_url_text_input_dialog_input_layout);
            TextView loadDevSettingsTextView = mCurrentMaterialDialog.getCustomView().findViewById(R.id.load_dev_settings_text_view);
            TextView loadRcSettingsTextView = mCurrentMaterialDialog.getCustomView().findViewById(R.id.load_rc_settings_text_view);

            EditText authProxyPublicHostEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_proxy_public_host_text_input_dialog_edit_text);
            TextInputLayout authProxyPublicHostTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_proxy_public_host_text_input_dialog_input_layout);
            EditText authProxyRestApiEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_proxy_rest_api_text_input_dialog_edit_text);
            TextInputLayout authProxyRestApiTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_proxy_rest_api_text_input_dialog_input_layout);
            EditText authTenantRestApiEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_tenant_rest_api_text_input_dialog_edit_text);
            TextInputLayout authTenantRestApiTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.auth_tenant_rest_api_text_input_dialog_input_layout);
            EditText generalRestApiEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.general_rest_api_text_input_dialog_edit_text);
            TextInputLayout generalRestApiTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.general_rest_api_text_input_dialog_input_layout);

            hostnameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            hostnameEditText.setContentDescription(context.getString(R.string.dialog_hostname_edit_text_content_description));
            broadsoftApiUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            broadsoftApiUrlEditText.setContentDescription(context.getString(R.string.dialog_broadsoft_api_url_edit_text_content_description));
            authProxyPublicHostEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            authProxyPublicHostEditText.setContentDescription(context.getString(R.string.dialog_auth_proxy_public_host_edit_text_content_description));
            platformApiUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            platformApiUrlEditText.setContentDescription(context.getString(R.string.dialog_platform_api_url_edit_text_content_description));
            messageApiUrlEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            messageApiUrlEditText.setContentDescription(context.getString(R.string.dialog_message_api_url_edit_text_content_description));

            authProxyPublicHostEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            authProxyPublicHostEditText.setContentDescription(context.getString(R.string.dialog_auth_proxy_public_host_edit_text_content_description));
            authProxyRestApiEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            authProxyRestApiEditText.setContentDescription(context.getString(R.string.dialog_auth_proxy_rest_api_edit_text_content_description));
            authTenantRestApiEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            authTenantRestApiEditText.setContentDescription(context.getString(R.string.dialog_auth_tenant_rest_api_edit_text_content_description));
            generalRestApiEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            generalRestApiEditText.setContentDescription(context.getString(R.string.dialog_general_rest_api_edit_text_content_description));

            loadDevSettingsTextView.setContentDescription(context.getString(R.string.endpoints_load_dev_settings));
            loadRcSettingsTextView.setContentDescription(context.getString(R.string.endpoints_load_rc_settings));

            if (!TextUtils.isEmpty(defaultHostname)) {
                hostnameTextInputLayout.setHint(defaultHostname);
                hostnameEditText.setText(defaultHostname, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultBroadsoftApiUrl)) {
                broadsoftApiUrlTextInputLayout.setHint(defaultBroadsoftApiUrl);
                broadsoftApiUrlEditText.setText(defaultBroadsoftApiUrl, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultPlatformApiUrl)) {
                platformApiUrlTextInputLayout.setHint(defaultPlatformApiUrl);
                platformApiUrlEditText.setText(defaultPlatformApiUrl, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultMessageApiUrl)) {
                messageApiUrlTextInputLayout.setHint(defaultMessageApiUrl);
                messageApiUrlEditText.setText(defaultMessageApiUrl, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultAuthProxyPublicHost)) {
                authProxyPublicHostTextInputLayout.setHint(defaultAuthProxyPublicHost);
                authProxyPublicHostEditText.setText(defaultAuthProxyPublicHost, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultAuthProxyRestApi)) {
                authProxyRestApiTextInputLayout.setHint(defaultAuthProxyRestApi);
                authProxyRestApiEditText.setText(defaultAuthProxyRestApi, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultAuthTenantRestApi)) {
                authTenantRestApiTextInputLayout.setHint(defaultAuthTenantRestApi);
                authTenantRestApiEditText.setText(defaultAuthTenantRestApi, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(defaultGeneralRestApi)) {
                generalRestApiTextInputLayout.setHint(defaultGeneralRestApi);
                generalRestApiEditText.setText(defaultGeneralRestApi, TextView.BufferType.EDITABLE);
            } else {
                mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE).setEnabled(false);
            }

            if (!TextUtils.isEmpty(prepopulateHostname)) {
                hostnameEditText.setText(prepopulateHostname, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateBroadsoftApiUrl)) {
                broadsoftApiUrlEditText.setText(prepopulateBroadsoftApiUrl, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulatePlatformApiUrl)) {
                platformApiUrlEditText.setText(prepopulatePlatformApiUrl, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateMessageApiUrl)) {
                messageApiUrlEditText.setText(prepopulateMessageApiUrl, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateAuthProxyPublicHost)) {
                authProxyPublicHostEditText.setText(prepopulateAuthProxyPublicHost, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateAuthProxyRestApi)) {
                authProxyRestApiEditText.setText(prepopulateAuthProxyRestApi, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateAuthTenantRestApi)) {
                authTenantRestApiEditText.setText(prepopulateAuthTenantRestApi, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateGeneralRestApi)) {
                generalRestApiEditText.setText(prepopulateGeneralRestApi, TextView.BufferType.EDITABLE);
            }

            loadDevSettingsTextView.setOnClickListener(view -> {
                hostnameEditText.setText(BuildConfig.DEV_HOSTNAME);
                broadsoftApiUrlEditText.setText(BuildConfig.DEV_API_URL);
                platformApiUrlEditText.setText(BuildConfig.DEV_PLATFORM_ACCESS_API_URL);
                messageApiUrlEditText.setText(BuildConfig.DEV_PLATFORM_API_URL);
                authProxyPublicHostEditText.setText(BuildConfig.DEV_AUTH_PROXY_PUBLIC_HOST);
                authProxyRestApiEditText.setText(BuildConfig.DEV_AUTH_PROXY_REST_API);
                authTenantRestApiEditText.setText(BuildConfig.DEV_AUTH_TENANT_REST_API);
                generalRestApiEditText.setText(BuildConfig.DEV_GENERAL_REST_API);
            });

            loadRcSettingsTextView.setOnClickListener(view -> {
                hostnameEditText.setText(BuildConfig.RC_HOSTNAME);
                broadsoftApiUrlEditText.setText(BuildConfig.RC_API_URL);
                platformApiUrlEditText.setText(BuildConfig.RC_PLATFORM_ACCESS_API_URL);
                messageApiUrlEditText.setText(BuildConfig.RC_PLATFORM_API_URL);
                authProxyPublicHostEditText.setText(BuildConfig.RC_AUTH_PROXY_PUBLIC_HOST);
                authProxyRestApiEditText.setText(BuildConfig.RC_AUTH_PROXY_REST_API);
                authTenantRestApiEditText.setText(BuildConfig.RC_AUTH_TENANT_REST_API);
                generalRestApiEditText.setText(BuildConfig.RC_GENERAL_REST_API);
            });
        }

        return mCurrentMaterialDialog;
    }

    @Override
    public MaterialDialog showCredentialsEditTextDialog(@NonNull Context context, int titleId, @Nullable String defaultUsername, @Nullable String prepopulateUsername, @Nullable String defaultPassword, @Nullable String prepopulatePassword, @NonNull MaterialDialog.SingleButtonCallback positiveCallback, @NonNull MaterialDialog.SingleButtonCallback neutralCallback, @NonNull MaterialDialog.SingleButtonCallback negativeCallback, int itemColor) {

        if (!(context instanceof Activity) || ((Activity) context).isFinishing()) {
            return null;
        }

        dismissCurrentDialog();

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .cancelable(false)
                .inputType(InputType.TYPE_CLASS_TEXT);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (inflater != null) {
            LinearLayout textInputFrameLayout = (LinearLayout) LinearLayout.inflate(context, R.layout.view_credentials_text_edit_layout, null);
            AppCompatEditText usernameEditText = textInputFrameLayout.findViewById(R.id.username_text_input_dialog_edit_text);
            AppCompatEditText passwordEditText = textInputFrameLayout.findViewById(R.id.password_text_input_dialog_edit_text);
            EditTextExtensionsKt.makeClearableEditText(usernameEditText);
            EditTextExtensionsKt.makeClearableEditText(passwordEditText);
            builder.customView(textInputFrameLayout, true);
        }

        builder.positiveText(R.string.general_ok);
        builder.negativeText(R.string.general_cancel);
        builder.neutralText(R.string.endpoints_neutral_text);

        builder.onPositive(positiveCallback);

        if (itemColor != 0) {
            builder.positiveColor(ContextCompat.getColor(context, itemColor));
        }

        builder.onNeutral(neutralCallback);

        if (itemColor != 0) {
            builder.neutralColor(ContextCompat.getColor(context, itemColor));
        }

        builder.onNegative(negativeCallback);

        if (itemColor != 0) {
            builder.negativeColor(ContextCompat.getColor(context, itemColor));
        }


        if (titleId != 0) {
            builder.title(titleId);
        }

        mCurrentMaterialDialog = builder.show();

        mCurrentMaterialDialog.getActionButton(DialogAction.NEGATIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_cancel)));
        mCurrentMaterialDialog.getActionButton(DialogAction.POSITIVE)
                .setContentDescription(context.getString(R.string.dialog_button_content_description, context.getString(R.string.general_ok)));
        mCurrentMaterialDialog.getActionButton(DialogAction.NEUTRAL)
                .setContentDescription(context.getString(R.string.dialog_reset_to_defaults_content_description));

        if (mCurrentMaterialDialog.getCustomView() != null) {
            EditText usernameEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.username_text_input_dialog_edit_text);
            TextInputLayout usernameTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.username_text_input_dialog_input_layout);
            EditText passwordEditText = mCurrentMaterialDialog.getCustomView().findViewById(R.id.password_text_input_dialog_edit_text);
            TextInputLayout passwordTextInputLayout = mCurrentMaterialDialog.getCustomView().findViewById(R.id.password_text_input_dialog_input_layout);

            usernameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            usernameEditText.setContentDescription(context.getString(R.string.dialog_username_edit_text_content_description));
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            passwordEditText.setContentDescription(context.getString(R.string.dialog_password_edit_text_content_description));

            if (!TextUtils.isEmpty(defaultUsername)) {
                usernameTextInputLayout.setHint(defaultUsername);
                usernameEditText.setText(defaultUsername, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(defaultPassword)) {
                passwordTextInputLayout.setHint(defaultPassword);
                passwordEditText.setText(defaultPassword, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulateUsername)) {
                usernameEditText.setText(prepopulateUsername, TextView.BufferType.EDITABLE);
            }

            if (!TextUtils.isEmpty(prepopulatePassword)) {
                passwordEditText.setText(prepopulatePassword, TextView.BufferType.EDITABLE);
            }
        }

        return mCurrentMaterialDialog;
    }

    @Override
    public void dismissAllDialogs() {
        dismissProgressDialog();
        dismissCurrentDialog();
    }

    @Override
    public void dismissProgressDialog() {
        if (mCurrentProgressDialog != null && mCurrentProgressDialog.isShowing()) {
            dismissDialogWithCheck(mCurrentProgressDialog);
            mCurrentProgressDialog = null;
        }
    }

    @Override
    public void dismissCurrentDialog() {
        if (mCurrentMaterialDialog != null && mCurrentMaterialDialog.isShowing()) {
            dismissDialogWithCheck(mCurrentMaterialDialog);
            mCurrentMaterialDialog = null;
        }
    }
    // --------------------------------------------------------------------------------------------
}
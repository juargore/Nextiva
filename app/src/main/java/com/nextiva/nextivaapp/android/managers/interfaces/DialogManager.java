/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers.interfaces;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener;

import java.util.List;

/**
 * Created by adammacdonald on 3/21/18.
 */

public interface DialogManager {

    MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName);

    MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback);

    MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable @Enums.Analytics.EventName.Event String eventName,
            @Nullable String title,
            @Nullable String content,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback);

    MaterialDialog showErrorDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @Nullable @Enums.Analytics.EventName.Event String eventName,
            @Nullable String title,
            @Nullable String content,
            @NonNull String suggestionButtonTitle,
            @NonNull MaterialDialog.SingleButtonCallback suggestionCallback,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback);

    MaterialDialog showProgressDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @StringRes int contentId);

    MaterialDialog showProgressDialog(
            @NonNull Context context,
            @NonNull @Enums.Analytics.ScreenName.Screen String analyticsScreenName,
            @StringRes int titleId,
            @StringRes int contentId);

    MaterialDialog showDialog(
            @NonNull Context context,
            @StringRes int titleId,
            @StringRes int contentId,
            @StringRes int neutralButtonId,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback);

    MaterialDialog showDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @Nullable String neutralButton,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback);

    MaterialDialog showDialog(
            @NonNull Context context,
            @StringRes int titleId,
            @StringRes int contentId,
            @StringRes int positiveButtonId,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @StringRes int negativeButtonId,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback);

    MaterialDialog showDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @Nullable String positiveButton,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @Nullable String negativeButton,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback);

    MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback);

    MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback);

    MaterialDialog showSimpleListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull List<String> items,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            @ColorRes int itemColor);

    MaterialDialog showTwoLineListDialog(
            @NonNull Context context,
            @Nullable String title,
            @Nullable String content,
            @NonNull final SimpleListDialogListener listener,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            int itemColor,
            @NonNull RecyclerView.Adapter listAdapter);

    MaterialAlertDialogBuilder showOptionsAlertDialog(
            @NonNull Context context,
            @Nullable String title,
            @NonNull  CharSequence[] items,
            final DialogInterface.OnClickListener listener);


    MaterialDialog showEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
            int inputType,
            int inputLength,
            @Nullable String defaultText,
            @Nullable String hintText,
            @NonNull final MaterialDialog.SingleButtonCallback singleButtonCallback);

    MaterialDialog showEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
            int inputType,
            int inputLength,
            @Nullable String defaultText,
            @Nullable String prepopulateText,
            @Nullable String hintText,
            @Nullable String positiveButton,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @Nullable String negativeButton,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            @ColorRes int itemColor);

    MaterialDialog showEndpointEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
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
            @ColorRes int itemColor);

    MaterialDialog showCredentialsEditTextDialog(
            @NonNull Context context,
            @StringRes int titleId,
            @Nullable String defaultUsername,
            @Nullable String prepopulateUsername,
            @Nullable String defaultPassword,
            @Nullable String prepopulatePassword,
            @NonNull MaterialDialog.SingleButtonCallback positiveCallback,
            @NonNull MaterialDialog.SingleButtonCallback neutralCallback,
            @NonNull MaterialDialog.SingleButtonCallback negativeCallback,
            @ColorRes int itemColor);

    void dismissAllDialogs();

    void dismissProgressDialog();

    void dismissCurrentDialog();
}

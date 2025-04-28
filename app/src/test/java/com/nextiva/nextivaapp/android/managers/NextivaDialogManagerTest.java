/*
 * Copyright (c) 2017 Nextiva, Inc. to Present.
 * All rights reserved.
 */

package com.nextiva.nextivaapp.android.managers;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.nextiva.nextivaapp.android.BaseRobolectricTest;
import com.nextiva.nextivaapp.android.R;
import com.nextiva.nextivaapp.android.constants.Enums;
import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener;
import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager;
import com.nextiva.nextivaapp.android.managers.interfaces.SettingsManager;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.shadows.ShadowAlertDialog;

import java.io.IOException;
import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

@HiltAndroidTest
public class NextivaDialogManagerTest extends BaseRobolectricTest {

    @Inject
    protected AnalyticsManager mAnalyticsManager;

    @Inject
    protected SettingsManager mSettingsManager;

    private ActivityController<Activity> mController;
    private Activity mActivity;

    private NextivaDialogManager mDialogManager;

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    @Before
    public void setup() throws IOException {
        hiltRule.inject();

        mController = Robolectric.buildActivity(Activity.class);
        mActivity = mController.create().start().resume().visible().get();

        mDialogManager = new NextivaDialogManager(mAnalyticsManager, mSettingsManager);
    }

    @Test
    public void showErrorDialog_callsToAnalyticsManager() {
        mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        verify(mAnalyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.GENERIC_ERROR_DIALOG_SHOWN);
    }

    @Test
    public void showErrorDialog_showsCorrectDialog() {
        mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Error", alertDialog.getTitleView().getText().toString());
        assertEquals("We're sorry, an error occurred. Please try again.", alertDialog.getContentView().getText().toString());
        assertEquals("OK", alertDialog.getActionButton(DialogAction.POSITIVE).getText());
    }

    @Test
    public void showErrorDialog_selectOk_callsToAnalyticsManager() {
        mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).callOnClick();

        verify(mAnalyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.GENERIC_ERROR_DIALOG_OK_BUTTON_PRESSED);
    }

    @Test
    public void showErrorDialog_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showProgressDialog_callsToAnalyticsManager() {
        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_back);

        verify(mAnalyticsManager).logEvent(Enums.Analytics.ScreenName.CALL_DETAILS, Enums.Analytics.EventName.PROGRESS_DIALOG_SHOWN);
    }

    @Test
    public void showProgressDialog_supplyContentIdParam_showsCorrectDialog() {
        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_back);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Performing call-back…", alertDialog.getContentView().getText().toString());
        assertEquals(View.VISIBLE, alertDialog.getProgressBar().getVisibility());
    }

    @Test
    public void showProgressDialog_supplyContentIdParam_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_back);

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showProgressDialog_supplyContentIdParam_dismissesExistingProgressDialog() {
        MaterialDialog oldDialog = mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_through);

        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_back);

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showProgressDialog_supplyTitleIdAndContentIdParams_showsCorrectDialog() {
        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.call_back_initiated, R.string.progress_performing_call_back);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("You will receive a call shortly.", alertDialog.getTitleView().getText().toString());
        assertEquals("Performing call-back…", alertDialog.getContentView().getText().toString());
        assertEquals(View.VISIBLE, alertDialog.getProgressBar().getVisibility());
    }

    @Test
    public void showProgressDialog_supplyTitleIdAndContentIdParams_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.call_back_initiated, R.string.progress_performing_call_back);

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showProgressDialog_supplyTitleIdAndContentIdParams_dismissesExistingProgressDialog() {
        MaterialDialog oldDialog = mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_through);

        mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.call_back_initiated, R.string.progress_performing_call_back);

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void dismissProgressDialog_dismissesCorrectDialog() {
        MaterialDialog oldDialog = mDialogManager.showProgressDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS, R.string.progress_performing_call_through);

        mDialogManager.dismissProgressDialog();

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showDialog_titleIdStringContentIdPositiveButtonCallbackParams_showsCorrectDialog() {
        mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Incoming Video Call", alertDialog.getTitleView().getText().toString());
        assertEquals("Unavailable", alertDialog.getContentView().getText().toString());
        assertEquals("Yes", alertDialog.getActionButton(DialogAction.POSITIVE).getText());
    }

    @Test
    public void showDialog_titleIdStringContentIdPositiveButtonCallbackParams_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog, which) -> {

                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showDialog_titleIdStringContentIdPositiveButtonCallbackParams_okButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showDialog_titleIdStringContentIdPositiveButtonCallbackParams_okButtonCallsToCallback() {
        MaterialDialog.SingleButtonCallback callback = Mockito.mock(MaterialDialog.SingleButtonCallback.class);

        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                callback);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        verify(callback).onClick(dialog, DialogAction.POSITIVE);
    }

    @Test
    public void showDialog_titleStringContentStringPositiveButtonCallbackParams_showsCorrectDialog() {
        mDialogManager.showDialog(
                mActivity,
                "Title",
                "Content",
                "Action",
                (dialog, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Title", alertDialog.getTitleView().getText().toString());
        assertEquals("Content", alertDialog.getContentView().getText().toString());
        assertEquals("Action", alertDialog.getActionButton(DialogAction.POSITIVE).getText());
    }

    @Test
    public void showDialog_titleStringContentStringPositiveButtonCallbackParams_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showDialog(
                mActivity,
                "Title",
                "Content",
                "Action",
                (dialog, which) -> {

                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showDialog_titleStringContentStringPositiveButtonCallbackParams_okButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                "Title",
                "Content",
                "Action",
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showDialog_titleStringContentStringPositiveButtonCallbackParams_okButtonCallsToCallback() {
        MaterialDialog.SingleButtonCallback callback = Mockito.mock(MaterialDialog.SingleButtonCallback.class);

        MaterialDialog dialog = mDialogManager.showDialog(mActivity, "Title", "Content", "Action", callback);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        verify(callback).onClick(dialog, DialogAction.POSITIVE);
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_showsCorrectDialog() {
        mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog, which) -> {

                },
                R.string.general_no,
                (dialog, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Incoming Video Call", alertDialog.getTitleView().getText().toString());
        assertEquals("Unavailable", alertDialog.getContentView().getText().toString());
        assertEquals("Yes", alertDialog.getActionButton(DialogAction.POSITIVE).getText());
        assertEquals("No", alertDialog.getActionButton(DialogAction.NEGATIVE).getText());
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog, which) -> {

                },
                R.string.general_no,
                (dialog, which) -> {

                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_positiveButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog12, which) -> {

                },
                R.string.general_no,
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_positiveButtonCallsToCallback() {
        MaterialDialog.SingleButtonCallback callback = Mockito.mock(MaterialDialog.SingleButtonCallback.class);

        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                callback,
                R.string.general_no,
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        verify(callback).onClick(dialog, DialogAction.POSITIVE);
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_negativeButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog12, which) -> {

                },
                R.string.general_no,
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.NEGATIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showDialog_titleIdContentIdBothButtonCallbacksParams_negativeButtonCallsToCallback() {
        MaterialDialog.SingleButtonCallback callback = Mockito.mock(MaterialDialog.SingleButtonCallback.class);

        MaterialDialog dialog = mDialogManager.showDialog(
                mActivity,
                R.string.call_dialog_title,
                R.string.call_dialog_unavailable_display_name,
                R.string.general_yes,
                (dialog1, which) -> {

                },
                R.string.general_no,
                callback);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.NEGATIVE).performClick();

        verify(callback).onClick(dialog, DialogAction.NEGATIVE);
    }


    @Test
    public void showSimpleListDialog_showsCorrectDialog() {
        ArrayList<String> itemsList = new ArrayList<String>() {{
            add("One");
            add("Two");
            add("Three");
            add("Four");
        }};

        mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                itemsList,
                position -> {
                },
                (callbackDialog, which) -> {
                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Title", alertDialog.getTitleView().getText().toString());
        assertEquals(itemsList, alertDialog.getItems());
        assertEquals("Cancel", alertDialog.getActionButton(DialogAction.NEGATIVE).getText());
    }

    @Test
    public void showSimpleListDialog_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                new ArrayList<>(),
                position -> {

                },
                (callbackDialog, which) -> {
                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showSimpleListDialog_cancelButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                new ArrayList<>(),
                position -> {

                },
                (callbackDialog, which) -> {
                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.NEGATIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showSimpleListDialog_selectItem_callsToCallback() {
        SimpleListDialogListener callback = mock(SimpleListDialogListener.class);

        ArrayList<String> itemsList = new ArrayList<String>() {{
            add("One");
            add("Two");
            add("Three");
            add("Four");
        }};

        MaterialDialog dialog = mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                itemsList,
                callback,
                (callbackDialog, which) -> {
                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.onItemSelected(dialog, dialog.getView(), 2, null, false);

        verify(callback).onSelectionMade(2);
    }

    @Test
    public void showSimpleListDialogWithContent_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                "Content",
                new ArrayList<>(),
                position -> {

                },
                (callbackDialog, which) -> {
                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showSimpleListDialogWithContent_cancelButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                "Content",
                new ArrayList<>(),
                position -> {

                },
                (callbackDialog, which) -> {
                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.NEGATIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showSimpleListDialogWithContent_selectItem_callsToCallback() {
        SimpleListDialogListener callback = mock(SimpleListDialogListener.class);

        ArrayList<String> itemsList = new ArrayList<String>() {{
            add("One");
            add("Two");
            add("Three");
            add("Four");
        }};

        MaterialDialog dialog = mDialogManager.showSimpleListDialog(
                mActivity,
                "Title",
                "Content",
                itemsList,
                callback,
                (callbackDialog, which) -> {
                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.onItemSelected(dialog, dialog.getView(), 2, null, false);

        verify(callback).onSelectionMade(2);
    }

    @Test
    public void showEditTextDialogSimple_showsCorrectDialog() {
        mDialogManager.showEditTextDialog(
                mActivity,
                R.string.call_dialog_title,
                InputType.TYPE_CLASS_NUMBER,
                100,
                "Default",
                null,
                (dialog, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Incoming Video Call", alertDialog.getTitleView().getText().toString());
        assertEquals("OK", alertDialog.getActionButton(DialogAction.POSITIVE).getText());
        assertEquals("Default", ((EditText) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_edit_text)).getText().toString());
        assertEquals(InputType.TYPE_CLASS_NUMBER, ((EditText) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_edit_text)).getInputType());
        assertEquals(100, ((TextInputLayout) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_input_layout)).getCounterMaxLength());
    }


    @Test
    public void showEditTextDialogAdvanced_showsCorrectDialog() {
        mDialogManager.showEditTextDialog(
                mActivity,
                R.string.call_dialog_title,
                InputType.TYPE_CLASS_NUMBER,
                100,
                "Default",
                "PrePop",
                null,
                "Pos",
                (dialog, which) -> {

                },
                "Neg",
                (dialog, which) -> {

                }, R.color.black);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        assertEquals("Incoming Video Call", alertDialog.getTitleView().getText().toString());
        assertEquals("Default", ((TextInputLayout) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_input_layout)).getHint());
        assertEquals("PrePop", ((EditText) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_edit_text)).getText().toString());
        assertEquals("Pos", (alertDialog.getActionButton(DialogAction.POSITIVE).getText()));
        assertEquals(InputType.TYPE_CLASS_NUMBER, ((EditText) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_edit_text)).getInputType());
        assertEquals(100, ((TextInputLayout) alertDialog.getCustomView().findViewById(R.id.text_input_dialog_input_layout)).getCounterMaxLength());
        assertEquals("Neg", (alertDialog.getActionButton(DialogAction.NEGATIVE).getText()));
    }

    @Test
    public void showEditTextDialog_dismissesExistingDialog() {
        MaterialDialog oldDialog = mDialogManager.showErrorDialog(mActivity, Enums.Analytics.ScreenName.CALL_DETAILS);

        mDialogManager.showEditTextDialog(
                mActivity,
                R.string.call_dialog_title,
                InputType.TYPE_CLASS_NUMBER,
                100,
                "Default",
                null,
                (dialog, which) -> {

                });

        assertFalse(oldDialog.isShowing());
    }

    @Test
    public void showEditTextDialog_okButtonDismissesDialog() {
        MaterialDialog dialog = mDialogManager.showEditTextDialog(
                mActivity,
                R.string.call_dialog_title,
                InputType.TYPE_CLASS_NUMBER,
                100,
                "Default",
                null,
                (dialog1, which) -> {

                });

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        assertFalse(dialog.isShowing());
    }

    @Test
    public void showEditTextDialog_okButtonCallsToCallback() {
        MaterialDialog.SingleButtonCallback callback = Mockito.mock(MaterialDialog.SingleButtonCallback.class);

        MaterialDialog dialog = mDialogManager.showEditTextDialog(mActivity, R.string.call_dialog_title, InputType.TYPE_CLASS_NUMBER, 100, "Default", null, callback);

        MaterialDialog alertDialog = (MaterialDialog) ShadowAlertDialog.getLatestDialog();
        alertDialog.getActionButton(DialogAction.POSITIVE).performClick();

        verify(callback).onClick(dialog, DialogAction.POSITIVE);
    }
}

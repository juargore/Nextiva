package com.nextiva.nextivaapp.android.fragments
//
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.constants.Enums.Analytics.EventName.*
//import com.nextiva.nextivaapp.android.constants.Enums.Analytics.ScreenName.APP_PREFERENCES
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.managers.interfaces.*
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.viewmodels.AppPreferenceViewModel
//import com.nhaarman.mockito_kotlin.*
//import org.junit.Assert.assertFalse
//import org.junit.Assert.assertTrue
//import org.junit.Test
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil
//import javax.inject.Inject
//
//class AppPreferencesFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var permissionsManager: PermissionManager
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var intentManager: IntentManager
//
//    @Inject
//    lateinit var settingsManager: SettingsManager
//
//    private lateinit var fragment: AppPreferencesFragment
//
//    private val mockViewModel: AppPreferenceViewModel = mock()
//
//    override fun setup() {
//        super.setup()
//
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(mockViewModel.isLoggingEnabled()).thenReturn(false)
//        whenever(mockViewModel.isFileLoggingEnabled()).thenReturn(false)
//        whenever(mockViewModel.isXmppLoggingEnabled()).thenReturn(false)
//        whenever(mockViewModel.isSipLoggingEnabled()).thenReturn(false)
//        whenever(viewModelFactory.create(AppPreferenceViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(settingsManager.nightModeState).thenReturn(Enums.Session.NightModeState.NIGHT_MODE_STATE_SYSTEM_DEFAULT)
//
//        fragment = AppPreferencesFragment.newInstance()
//        SupportFragmentTestUtil.startFragment(fragment)
//    }
//
//    override fun after() {
//        super.after()
//        fragment.onPause()
//        fragment.onStop()
//        fragment.onDestroy()
//    }
//
//    @Test
//    fun onCreatePreferences_disabledValues() {
//        assertFalse(fragment.enableLoggingCheckbox.isChecked)
//        assertFalse(fragment.fileLoggingCheckbox.isChecked)
//        assertFalse(fragment.fileLoggingCheckbox.isEnabled)
//        assertFalse(fragment.sipLoggingCheckbox.isChecked)
//        assertFalse(fragment.sipLoggingCheckbox.isEnabled)
//        assertFalse(fragment.xmppLoggingCheckbox.isChecked)
//        assertFalse(fragment.xmppLoggingCheckbox.isEnabled)
//    }
//
//    @Test
//    fun onCreatePreferences_enabledValues() {
//        reset(mockViewModel)
//        whenever(mockViewModel.isLoggingEnabled()).thenReturn(true)
//        whenever(mockViewModel.isFileLoggingEnabled()).thenReturn(true)
//        whenever(mockViewModel.isXmppLoggingEnabled()).thenReturn(true)
//        whenever(mockViewModel.isSipLoggingEnabled()).thenReturn(true)
//        fragment.fileLoggingCheckbox.isEnabled = true
//        fragment.xmppLoggingCheckbox.isEnabled = true
//        fragment.sipLoggingCheckbox.isEnabled = true
//
//        fragment.onCreatePreferences(null, null)
//
//        assertTrue(fragment.enableLoggingCheckbox.isChecked)
//        assertTrue(fragment.fileLoggingCheckbox.isChecked)
//        assertTrue(fragment.fileLoggingCheckbox.isEnabled)
//        assertTrue(fragment.sipLoggingCheckbox.isChecked)
//        assertTrue(fragment.sipLoggingCheckbox.isEnabled)
//        assertTrue(fragment.xmppLoggingCheckbox.isChecked)
//        assertTrue(fragment.xmppLoggingCheckbox.isEnabled)
//    }
//
//    @Test
//    fun enableLoggingPreferenceChange_setEnabledToDisabled() {
//        reset(mockViewModel)
//        reset(analyticsManager)
//
//        assertFalse(fragment.enableLoggingCheckbox.isChecked)
//        assertFalse(fragment.fileLoggingCheckbox.isEnabled)
//        assertFalse(fragment.sipLoggingCheckbox.isEnabled)
//        assertFalse(fragment.xmppLoggingCheckbox.isEnabled)
//
//        fragment.enableLoggingCheckbox.performClick()
//
//        assertTrue(fragment.enableLoggingCheckbox.isChecked)
//        assertTrue(fragment.xmppLoggingCheckbox.isEnabled)
//        assertTrue(fragment.sipLoggingCheckbox.isEnabled)
//        assertTrue(fragment.fileLoggingCheckbox.isEnabled)
//
//        verify(analyticsManager).logEvent(APP_PREFERENCES, ENABLE_LOGGING_SWITCH_CHECKED)
//    }
//
//    @Test
//    fun enableLoggingPreferenceChange_setDisabledFromEnabled() {
//        reset(mockViewModel)
//
//        fragment.enableLoggingCheckbox.performClick()
//
//        reset(analyticsManager)
//
//        assertTrue(fragment.enableLoggingCheckbox.isChecked)
//        assertTrue(fragment.xmppLoggingCheckbox.isEnabled)
//        assertTrue(fragment.xmppLoggingCheckbox.isChecked)
//        assertTrue(fragment.sipLoggingCheckbox.isEnabled)
//        assertTrue(fragment.sipLoggingCheckbox.isChecked)
//        assertTrue(fragment.fileLoggingCheckbox.isEnabled)
//        assertTrue(fragment.fileLoggingCheckbox.isChecked)
//
//        fragment.enableLoggingCheckbox.performClick()
//
//        assertFalse(fragment.enableLoggingCheckbox.isChecked)
//        assertFalse(fragment.xmppLoggingCheckbox.isEnabled)
//        assertFalse(fragment.xmppLoggingCheckbox.isChecked)
//        assertFalse(fragment.sipLoggingCheckbox.isEnabled)
//        assertFalse(fragment.sipLoggingCheckbox.isChecked)
//        assertFalse(fragment.fileLoggingCheckbox.isEnabled)
//        assertFalse(fragment.fileLoggingCheckbox.isChecked)
//
//        verify(analyticsManager).logEvent(APP_PREFERENCES, ENABLE_LOGGING_SWITCH_UNCHECKED)
//    }
////
////    @Test
////    fun fileLoggingPreferenceChange_setEnabledFromDisabled_permissionsGranted() {
////        reset(mockViewModel)
////
////        fragment.enableLoggingCheckbox.performClick()
////        fragment.fileLoggingCheckbox.performClick() //This will unselect the autoselect
////
////        assertTrue(fragment.enableLoggingCheckbox.isChecked)
////        assertTrue(fragment.xmppLoggingCheckbox.isEnabled)
////        assertTrue(fragment.sipLoggingCheckbox.isEnabled)
////
////        assertTrue(fragment.fileLoggingCheckbox.isEnabled)
////        assertFalse(fragment.fileLoggingCheckbox.isChecked)
////
////        fragment.fileLoggingCheckbox.performClick()
////        fragment.fileLoggingCheckbox.performClick()
////
////        val grantedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
////
////        verify(permissionsManager).requestFileLoggingPermission(any(), eq(APP_PREFERENCES), grantedArgumentCaptor.capture(), any())
////
////        grantedArgumentCaptor.firstValue.onPermissionGranted()
////
////        verify(mockViewModel).enableFileLogging(true)
////        verify(mockViewModel).setupLogger()
////        verify(analyticsManager).logEvent(APP_PREFERENCES, FILE_LOGGING_SWITCH_CHECKED)
////    }
////
////    @Test
////    fun fileLoggingPreferenceChange_setEnabledFromDisabled_permissionsDenied() {
////        reset(mockViewModel)
////
////        fragment.enableLoggingCheckbox.performClick()
////        fragment.fileLoggingCheckbox.performClick() //This will unselect the autoselect
////
////        assertTrue(fragment.enableLoggingCheckbox.isChecked)
////        assertTrue(fragment.xmppLoggingCheckbox.isEnabled)
////        assertTrue(fragment.sipLoggingCheckbox.isEnabled)
////
////        assertTrue(fragment.fileLoggingCheckbox.isEnabled)
////        assertFalse(fragment.fileLoggingCheckbox.isChecked)
////
////        fragment.fileLoggingCheckbox.performClick()
////
////        val deniedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionDeniedCallback> = argumentCaptor()
////
////        verify(permissionsManager).requestFileLoggingPermission(any(), eq(APP_PREFERENCES), any(), deniedArgumentCaptor.capture())
////
////        deniedArgumentCaptor.firstValue.onPermissionDenied()
////
////        assertFalse(fragment.fileLoggingCheckbox.isChecked)
////        verify(mockViewModel).enableFileLogging(true)
////        verify(analyticsManager).logEvent(APP_PREFERENCES, FILE_LOGGING_SWITCH_CHECKED)
////    }
//
////    @Test
////    fun expireContactCache_showsDialog_positiveOptionClicked() {
////        reset(mockViewModel)
////
////        fragment.expireContactCache?.performClick()
////
////        val positiveCallbackArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
////
////        verify(dialogManager).showDialog(any(),
////                eq(0),
////                eq(R.string.app_preference_expire_contact_cache_message),
////                eq(R.string.app_preference_expire_contact_cache_positive_action),
////                positiveCallbackArgumentCaptor.capture(),
////                eq(R.string.general_cancel),
////                any())
////
////        positiveCallbackArgumentCaptor.firstValue.onClick(mock(), mock())
////
////        verify(mockViewModel).expireContactsCache()
////    }
////
////    @Test
////    fun expireContactCache_showsDialog_negativeOptionClicked() {
////        reset(mockViewModel)
////
////        fragment.expireContactCache?.performClick()
////
////        val negativeCallbackArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
////
////        verify(dialogManager).showDialog(any(),
////                eq(0),
////                eq(R.string.app_preference_expire_contact_cache_message),
////                eq(R.string.app_preference_expire_contact_cache_positive_action),
////                any(),
////                eq(R.string.general_cancel),
////                negativeCallbackArgumentCaptor.capture())
////
////        negativeCallbackArgumentCaptor.firstValue.onClick(mock(), mock())
////
////        verify(mockViewModel, never()).expireContactsCache()
////    }
//
////    @Test
////    fun clearLogs_showsDialog_positiveOptionClicked() {
////        reset(mockViewModel)
////
////        whenever(mockViewModel.isFileLoggingEnabled()).thenReturn(true)
////
////        fragment.clearLogs.performClick()
////
////        val grantedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
////
////        verify(permissionsManager).requestFileLoggingPermission(any(), eq(APP_PREFERENCES), grantedArgumentCaptor.capture(), eq(null))
////
////        grantedArgumentCaptor.firstValue.onPermissionGranted()
////
////        val positiveCallbackArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
////
////        verify(analyticsManager).logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_SHOWN)
////
////        verify(dialogManager).showDialog(any(),
////                eq(0),
////                eq(R.string.app_preference_clear_logs_dialog_message),
////                eq(R.string.app_preference_clear_logs_dialog_positive_action),
////                positiveCallbackArgumentCaptor.capture(),
////                eq(R.string.general_cancel),
////                any())
////
////        positiveCallbackArgumentCaptor.firstValue.onClick(mock(), mock())
////
////        verify(mockViewModel).deleteDirectory(any())
////        verify(mockViewModel).isFileLoggingEnabled()
////        verify(analyticsManager).logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_CLEAR_LOGS_BUTTON_PRESSED)
////    }
//
//    @Test
//    fun clearLogs_showsDialog_negativeOptionClicked() {
//        reset(mockViewModel)
//
//        whenever(mockViewModel.isFileLoggingEnabled()).thenReturn(true)
//
//        fragment.clearLogs.performClick()
//
//        val grantedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
//
//        verify(permissionsManager).requestFileLoggingPermission(any(), eq(APP_PREFERENCES), grantedArgumentCaptor.capture(), eq(null))
//
//        grantedArgumentCaptor.firstValue.onPermissionGranted()
//
//        val negativeCallbackArgumentCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//
//        verify(analyticsManager).logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_SHOWN)
//
//        verify(dialogManager).showDialog(any(),
//                eq(0),
//                eq(R.string.app_preference_clear_logs_dialog_message),
//                eq(R.string.app_preference_clear_logs_dialog_positive_action),
//                any(),
//                eq(R.string.general_cancel),
//                negativeCallbackArgumentCaptor.capture())
//
//        negativeCallbackArgumentCaptor.firstValue.onClick(mock(), mock())
//
//        verify(mockViewModel, never()).deleteDirectory(any())
//        verify(mockViewModel, never()).isFileLoggingEnabled()
//        verify(analyticsManager).logEvent(APP_PREFERENCES, CLEAR_ALL_LOGS_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun emailLogs_permissionGranted_callsToIntentManager() {
//        reset(mockViewModel)
//
//        fragment.emailLogs.performClick()
//
//        verify(analyticsManager).logEvent(APP_PREFERENCES, EMAIL_LOGS_TO_SUPPORT_BUTTON_PRESSED)
//
//        val grantedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
//
//        verify(permissionsManager).requestFileLoggingPermission(any(), eq(APP_PREFERENCES), grantedArgumentCaptor.capture(), eq(null))
//
//        grantedArgumentCaptor.firstValue.onPermissionGranted()
//
//        verify(mockViewModel).zipLogs()
//    }
//}
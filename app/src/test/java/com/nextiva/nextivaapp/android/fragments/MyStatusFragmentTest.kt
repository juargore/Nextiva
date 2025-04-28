package com.nextiva.nextivaapp.android.fragments
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.graphics.drawable.BitmapDrawable
//import android.text.TextUtils
//import android.view.View
//import android.view.inputmethod.EditorInfo
//import android.widget.ImageView
//import android.widget.RadioButton
//import android.widget.TextView
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.Observer
//import androidx.lifecycle.ViewModelProvider
//import androidx.test.core.app.ApplicationProvider
//import com.afollestad.materialdialogs.DialogAction
//import com.afollestad.materialdialogs.MaterialDialog
//import com.nextiva.nextivaapp.android.BaseRobolectricTest
//import com.nextiva.nextivaapp.android.R
//import com.nextiva.nextivaapp.android.constants.Enums
//import com.nextiva.nextivaapp.android.db.DbManager
//import com.nextiva.nextivaapp.android.db.model.DbPresence
//import com.nextiva.nextivaapp.android.db.model.DbSession
//import com.nextiva.nextivaapp.android.di.TestNextivaComponent
//import com.nextiva.nextivaapp.android.dialogs.SimpleListDialogListener
//import com.nextiva.nextivaapp.android.managers.interfaces.AnalyticsManager
//import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
//import com.nextiva.nextivaapp.android.managers.interfaces.DialogManager
//import com.nextiva.nextivaapp.android.managers.interfaces.PermissionManager
//import com.nextiva.nextivaapp.android.mocks.TestNextivaApplication
//import com.nextiva.nextivaapp.android.mocks.activities.FakeNavigationListenerActivity
//import com.nextiva.nextivaapp.android.models.AvatarInfo
//import com.nextiva.nextivaapp.android.models.CallInfo
//import com.nextiva.nextivaapp.android.models.SingleEvent
//import com.nextiva.nextivaapp.android.models.UserDetails
//import com.nextiva.nextivaapp.android.net.buses.RxEvents
//import com.nextiva.nextivaapp.android.util.StringUtil
//import com.nextiva.nextivaapp.android.view.PresenceView
//import com.nextiva.nextivaapp.android.viewmodels.MyStatusViewModel
//import com.nhaarman.mockito_kotlin.*
//import com.theartofdev.edmodo.cropper.CropImage
//import com.theartofdev.edmodo.cropper.CropImageView
//import io.mockk.every
//import io.mockk.mockkClass
//import io.mockk.mockkStatic
//import io.reactivex.Maybe
//import org.junit.Assert.*
//import org.junit.Test
//import org.mockito.ArgumentCaptor
//import org.robolectric.Shadows
//import org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment
//import javax.inject.Inject
//
//class MyStatusFragmentTest : BaseRobolectricTest() {
//
//    @Inject
//    lateinit var viewModelFactory: ViewModelProvider.Factory
//
//    @Inject
//    lateinit var analyticsManager: AnalyticsManager
//
//    @Inject
//    lateinit var dialogManager: DialogManager
//
//    @Inject
//    lateinit var dbManager: DbManager
//
//    @Inject
//    lateinit var avatarManager: AvatarManager
//
//    @Inject
//    lateinit var permissionManager: PermissionManager
//
//    private lateinit var fragment: MyStatusFragment
//
//    private val mockViewModel: MyStatusViewModel = mock()
//    private val mockContactHeaderViewPresenceLiveData: MutableLiveData<DbPresence> = mock()
//    private val mockXmppErrorEventLiveData: MutableLiveData<RxEvents.XmppErrorEvent> = mock()
//    private val mockConnectionErrorLiveData: MutableLiveData<SingleEvent<Boolean>> = mock()
//    private val mockOwnVCardLiveData: MutableLiveData<DbSession> = mock()
//    private val mockOwnPresenceLiveData: MutableLiveData<DbSession> = mock()
//    private val mockMyConferenceLiveData: MutableLiveData<CallInfo> = mock()
//
//    override fun setup() {
//        super.setup()
//        ((ApplicationProvider.getApplicationContext() as TestNextivaApplication).nextivaComponent as TestNextivaComponent).inject(this)
//
//        whenever(viewModelFactory.create(MyStatusViewModel::class.java)).thenReturn(mockViewModel)
//        whenever(mockViewModel.contactHeaderViewPresenceStateLiveData).thenReturn(mockContactHeaderViewPresenceLiveData)
//        whenever(mockViewModel.xmppErrorEventLiveData).thenReturn(mockXmppErrorEventLiveData)
//        whenever(mockViewModel.connectionErrorLiveData).thenReturn(mockConnectionErrorLiveData)
//        whenever(dbManager.ownPresenceLiveData).thenReturn(mockOwnPresenceLiveData)
//        whenever(dbManager.ownVCardLiveData).thenReturn(mockOwnVCardLiveData)
//        whenever(mockViewModel.myConferenceLiveData).thenReturn(mockMyConferenceLiveData)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
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
//    fun onCreateView_callsToViewModelToGetOwnVCard() {
//        verify(mockViewModel).getOwnVCard()
//    }
//
//    @Test
//    fun onCreateView_hasUserDetails_setsContactHeaderViewName() {
//        val mockUserDetails: UserDetails = mock()
//        whenever(mockUserDetails.fullName).thenReturn("Full Name")
//        whenever(mockViewModel.userDetails).thenReturn(mockUserDetails)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val nameTextView: TextView = fragment.mContactHeaderView.findViewById(R.id.contact_header_name_text_view)
//
//        assertEquals("Full Name", nameTextView.text.toString())
//    }
//
//    @Test
//    fun onCreateView_hasUserDetails_setsContactHeaderViewAvatarBitmap() {
//        val mockUserDetails: UserDetails = mock()
//        whenever(mockUserDetails.fullName).thenReturn("Full Name")
//        whenever(mockViewModel.userDetails).thenReturn(mockUserDetails)
//
//        val mockBitmap: Bitmap = mock()
//        whenever(avatarManager.getBitmap(any())).thenReturn(mockBitmap)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val avatarImageView: ImageView = (fragment.mContactHeaderView.findViewById(R.id.contact_header_avatar_view) as View).findViewById(R.id.avatar_image_view)
//
//        val argumentCaptor: KArgumentCaptor<AvatarInfo> = argumentCaptor()
//        verify(avatarManager).getBitmap(argumentCaptor.capture())
//        assertEquals("Full Name", argumentCaptor.firstValue.displayName)
//        assertEquals(mockBitmap, (avatarImageView.drawable as BitmapDrawable).bitmap)
//    }
//
//    @Test
//    fun onCreateView_hasXmppUsername_callsToDbManagerToSetAvatar() {
//        whenever(dbManager.getAvatarBitmap("xmppUserName")).thenReturn(Maybe.just(mock()))
//        whenever(mockViewModel.xmppUsername).thenReturn("xmppUserName")
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        verify(dbManager).getAvatarBitmap("xmppUserName")
//        verify(mockViewModel).setIsAvatarLoaded(true)
//    }
//
//    @Test
//    fun onCreateView_hasAvailablePresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.AVAILABLE
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_mobile_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Available", presenceLeftTextView.text.toString())
//        assertEquals("Available", presenceRightTextView.text.toString())
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.MOBILE_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onCreateView_hasAwayPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.AWAY
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_away_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_away_square, shadowDrawable.createdFromResId)
//        assertEquals("Away", presenceLeftTextView.text.toString())
//        assertEquals("Away", presenceRightTextView.text.toString())
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.AWAY_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onCreateView_hasBusyPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.BUSY
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_busy_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_busy_square, shadowDrawable.createdFromResId)
//        assertEquals("Busy", presenceLeftTextView.text.toString())
//        assertEquals("Busy", presenceRightTextView.text.toString())
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.BUSY_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onCreateView_hasMobilePresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, "abc", Enums.Contacts.PresenceTypes.AVAILABLE)
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_mobile_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Mobile", presenceLeftTextView.text.toString())
//        assertEquals("Mobile", presenceRightTextView.text.toString())
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.MOBILE_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onCreateView_hasNonePresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.NONE
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        fragment = MyStatusFragment.newInstance()
//        startFragment(fragment, FakeNavigationListenerActivity::class.java)
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(-1, fragment.mRadioGroup.checkedRadioButtonId)
//        assertEquals(View.INVISIBLE, presenceView.visibility)
//
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.MOBILE_RADIO_BUTTON_CHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.AWAY_RADIO_BUTTON_CHECKED)
//        verify(analyticsManager, times(0)).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.BUSY_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onCreateView_observesViewModelLiveDatas() {
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), any())
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), any())
//        verify(mockConnectionErrorLiveData).observe(eq(fragment), any())
//    }
//
//    @Test
//    fun onResume_callsToAnalyticsManagerToTrackScreenView() {
//        verify(analyticsManager).logScreenView(Enums.Analytics.ScreenName.MY_STATUS)
//    }
//
//    @Test
//    fun onActivityResult_resultOk_callsToViewModelToSaveAvatar() {
//        val mockIntent: Intent = mock()
//
//        fragment.onActivityResult(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, Activity.RESULT_OK, mockIntent)
//
//        verify(mockViewModel).saveAvatar(mockIntent)
//    }
//
//    @Test
//    fun onActivityResult_resultError_callsToDialogManagerToShowError() {
//        val mockIntent: Intent = mock()
//
//        fragment.onActivityResult(CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE, CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE, mockIntent)
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.MY_STATUS)
//    }
//
//    @Test
//    fun savePresence_callsToViewModel() {
//        fragment.mMessageEditText.setText("Text")
//
//        fragment.savePresence()
//
//        verify(mockViewModel).savePresenceStatus("Text")
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun onImeKeyEvent_sessionPresenceIsNullChangesMade_clearsFocus() {
//        mockkStatic(StringUtil::class)
//        every { StringUtil.changesMade(any(), any()) } returns true
//
//        whenever(mockViewModel.sessionPresence).thenReturn(null)
//
//        fragment.onImeKeyEvent(fragment.mMessageEditText, 0)
//
//        verify(mockViewModel, never()).savePresenceStatus(any())
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun onImeKeyEvent_sessionPresenceNotNullNotChangesMade_clearsFocus() {
//        mockkStatic(StringUtil::class)
//        every { StringUtil.changesMade(any(), any()) } returns false
//
//        fragment.onImeKeyEvent(fragment.mMessageEditText, 0)
//
//        verify(mockViewModel, never()).savePresenceStatus(any())
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun onImeKeyEvent_sessionPresenceNotNullChangesMade_savesPresenceAndClearsFocus() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(StringUtil::class)
//        every { StringUtil.changesMade(any(), any()) } returns true
//
//        fragment.onImeKeyEvent(fragment.mMessageEditText, 0)
//
//        verify(mockViewModel).savePresenceStatus(any())
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun doOnBackPressed_callsToAnalyticsManager() {
//        fragment.doOnBackPressed()
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.BACK_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun doOnBackPressed_sessionPresenceNotNullStatusEqual_returnsFalse() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), any()) } returns true
//
//        assertFalse(fragment.doOnBackPressed())
//    }
//
//    @Test
//    fun doOnBackPressed_sessionPresenceNotNullStatusNotEqual_statusAreEmpty_returnsFalse() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), any()) } returns false
//        every { TextUtils.isEmpty(any()) } returns true
//
//        assertFalse(fragment.doOnBackPressed())
//    }
//
//    @Test
//    fun doOnBackPressed_sessionPresenceNotNullStatusNotEqual_statusAreNotEmpty_returnsTrue() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), any()) } returns false
//        every { TextUtils.isEmpty(any()) } returns false
//
//        assertTrue(fragment.doOnBackPressed())
//        assertTrue(fragment.backPressed)
//        verify(mockViewModel).savePresenceStatus(any())
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksAvailable_savesAvailablePresence() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_mobile_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(mockViewModel).savePresence("abcd", Enums.Contacts.PresenceStates.AVAILABLE)
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksAvailable_callsToAnalyticsManager() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_mobile_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.MOBILE_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksAway_savesAwayPresence() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_away_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(mockViewModel).savePresence("abcd", Enums.Contacts.PresenceStates.AWAY)
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksAway_callsToAnalyticsManager() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_away_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.AWAY_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksBusy_savesBusyPresence() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_busy_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(mockViewModel).savePresence("abcd", Enums.Contacts.PresenceStates.BUSY)
//    }
//
//    @Test
//    fun onRadioGroupChecked_checksBusy_callsToAnalyticsManager() {
//        val radioButton: RadioButton = fragment.view!!.findViewById(R.id.my_status_busy_radio_button)
//
//        fragment.mMessageEditText.setText("abcd")
//        radioButton.isChecked = true
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.BUSY_RADIO_BUTTON_CHECKED)
//    }
//
//    @Test
//    fun onEditorActionListener_actionNotDone_returnsFalse() {
//        assertFalse(fragment.mOnEditorActionListener.onEditorAction(null, EditorInfo.IME_ACTION_GO, null))
//    }
//
//    @Test
//    fun onEditorActionListener_actionDoneNoChangesMade_returnsTrue() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(StringUtil::class)
//        every { StringUtil.changesMade(any(), any()) } returns false
//
//        assertTrue(fragment.mOnEditorActionListener.onEditorAction(null, EditorInfo.IME_ACTION_DONE, null))
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun onEditorActionListener_actionDoneChangesMade_returnsTrue() {
//        whenever(mockViewModel.sessionPresence).thenReturn(DbPresence())
//        mockkStatic(StringUtil::class)
//        every { StringUtil.changesMade(any(), any()) } returns true
//
//        assertTrue(fragment.mOnEditorActionListener.onEditorAction(null, EditorInfo.IME_ACTION_DONE, null))
//        verify(mockViewModel).savePresenceStatus(any())
//        assertFalse(fragment.mMessageEditText.hasFocus())
//        assertTrue(fragment.mMasterLayout.hasFocus())
//    }
//
//    @Test
//    fun onAvatarClickListener_callsToAnalyticsManager() {
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.AVATAR_IMAGE_PRESSED)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarNotLoaded_callsToPermissionManagerToVerifyPermission() {
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(false)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        verify(permissionManager).requestAvatarCameraPermission(
//                any(),
//                eq(Enums.Analytics.ScreenName.MY_STATUS),
//                any(),
//                isNull())
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarNotLoaded_getsNewProfilePic() {
//        val mockCropImageActivityBuilder = mockkClass(CropImage.ActivityBuilder::class)
//        var isCropImageActivityStarted = false
//
//        mockkStatic(CropImage::class)
//        every { CropImage.activity() } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setGuidelines(CropImageView.Guidelines.ON) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setCropShape(CropImageView.CropShape.OVAL) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setFixAspectRatio(true) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setAspectRatio(1, 1) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setMinCropResultSize(100, 100) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setBorderLineColor(any()) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.start(any(), fragment) } answers { isCropImageActivityStarted = true }
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(false)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val argumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
//        verify(permissionManager).requestAvatarCameraPermission(
//                any(),
//                eq(Enums.Analytics.ScreenName.MY_STATUS),
//                argumentCaptor.capture(),
//                isNull())
//
//        argumentCaptor.firstValue.onPermissionGranted()
//
//        io.mockk.verify { CropImage.activity() }
//        io.mockk.verify { mockCropImageActivityBuilder.setGuidelines(CropImageView.Guidelines.ON) }
//        io.mockk.verify { mockCropImageActivityBuilder.setCropShape(CropImageView.CropShape.OVAL) }
//        io.mockk.verify { mockCropImageActivityBuilder.setFixAspectRatio(true) }
//        io.mockk.verify { mockCropImageActivityBuilder.setAspectRatio(1, 1) }
//        io.mockk.verify { mockCropImageActivityBuilder.setMinCropResultSize(100, 100) }
//        io.mockk.verify { mockCropImageActivityBuilder.setBorderLineColor(any()) }
//        assertTrue(isCropImageActivityStarted)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_callsToAnalyticsManager() {
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns true
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns false
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.CHANGE_PROFILE_PHOTO_DIALOG_SHOWN)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_callsToDialogManager() {
//        val mockCropImageActivityBuilder = mockkClass(CropImage.ActivityBuilder::class)
//        var isCropImageActivityStarted = false
//
//        mockkStatic(CropImage::class)
//        every { CropImage.activity() } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setGuidelines(CropImageView.Guidelines.ON) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setCropShape(CropImageView.CropShape.OVAL) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setFixAspectRatio(true) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setAspectRatio(1, 1) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setMinCropResultSize(100, 100) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setBorderLineColor(any()) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.start(any(), fragment) } answers { isCropImageActivityStarted = true }
//
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns true
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns false
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), any(), any())
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_selectsNewProfile_callsToAnalyticsManager() {
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns true
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns false
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val argumentCaptor = ArgumentCaptor.forClass(SimpleListDialogListener::class.java)
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), argumentCaptor.capture(), any())
//        val listListener = argumentCaptor.value
//
//        listListener.onSelectionMade(0)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.CHANGE_PROFILE_PHOTO_DIALOG_NEW_PHOTO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_selectsNewProfile_startsCropImageActivity() {
//        val mockCropImageActivityBuilder = mockkClass(CropImage.ActivityBuilder::class)
//        var isCropImageActivityStarted = false
//
//        mockkStatic(CropImage::class)
//        every { CropImage.activity() } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setGuidelines(CropImageView.Guidelines.ON) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setCropShape(CropImageView.CropShape.OVAL) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setFixAspectRatio(true) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setAspectRatio(1, 1) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setMinCropResultSize(100, 100) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.setBorderLineColor(any()) } returns mockCropImageActivityBuilder
//        every { mockCropImageActivityBuilder.start(any(), fragment) } answers { isCropImageActivityStarted = true }
//
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns true
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns false
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val argumentCaptor = ArgumentCaptor.forClass(SimpleListDialogListener::class.java)
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), argumentCaptor.capture(), any())
//        val listListener = argumentCaptor.value
//
//        listListener.onSelectionMade(0)
//
//        val permissionGrantedArgumentCaptor: KArgumentCaptor<PermissionManager.PermissionGrantedCallback> = argumentCaptor()
//        verify(permissionManager).requestAvatarCameraPermission(
//                any(),
//                eq(Enums.Analytics.ScreenName.MY_STATUS),
//                permissionGrantedArgumentCaptor.capture(),
//                isNull())
//
//        permissionGrantedArgumentCaptor.firstValue.onPermissionGranted()
//
//        io.mockk.verify { CropImage.activity() }
//        io.mockk.verify { mockCropImageActivityBuilder.setGuidelines(CropImageView.Guidelines.ON) }
//        io.mockk.verify { mockCropImageActivityBuilder.setCropShape(CropImageView.CropShape.OVAL) }
//        io.mockk.verify { mockCropImageActivityBuilder.setFixAspectRatio(true) }
//        io.mockk.verify { mockCropImageActivityBuilder.setAspectRatio(1, 1) }
//        io.mockk.verify { mockCropImageActivityBuilder.setMinCropResultSize(100, 100) }
//        io.mockk.verify { mockCropImageActivityBuilder.setBorderLineColor(any()) }
//        assertTrue(isCropImageActivityStarted)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_selectsClear_callsToAnalyticsManager() {
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns false
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns true
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val argumentCaptor = ArgumentCaptor.forClass(SimpleListDialogListener::class.java)
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), argumentCaptor.capture(), any())
//        val listListener = argumentCaptor.value
//
//        listListener.onSelectionMade(1)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.CHANGE_PROFILE_PHOTO_DIALOG_CLEAR_PHOTO_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_selectsClear_callsToViewModel() {
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns false
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns true
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val argumentCaptor = ArgumentCaptor.forClass(SimpleListDialogListener::class.java)
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), argumentCaptor.capture(), any())
//        val listListener = argumentCaptor.value
//
//        listListener.onSelectionMade(1)
//
//        verify(mockViewModel).deleteAvatar()
//    }
//
//    @Test
//    fun onAvatarClickListener_avatarLoaded_selectsCancel_callsToAnalyticsManager() {
//        mockkStatic(TextUtils::class)
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_new_profile_photo)) } returns true
//        every { TextUtils.equals(any(), (ApplicationProvider.getApplicationContext() as TestNextivaApplication).getString(R.string.my_status_clear_profile_photo)) } returns false
//
//        whenever(mockViewModel.isAvatarLoaded).thenReturn(true)
//
//        fragment.mAvatarOnClickListener.onClick(null)
//
//        val buttonCaptor: KArgumentCaptor<MaterialDialog.SingleButtonCallback> = argumentCaptor()
//        verify(dialogManager).showSimpleListDialog(eq(fragment.activity!!), eq("Change Profile Photo"), any(), any(), buttonCaptor.capture())
//
//        val dialog = MaterialDialog.Builder(fragment.activity!!).build()
//        buttonCaptor.firstValue.onClick(dialog, DialogAction.POSITIVE)
//
//        verify(analyticsManager).logEvent(Enums.Analytics.ScreenName.MY_STATUS, Enums.Analytics.EventName.CHANGE_PROFILE_PHOTO_DIALOG_CANCEL_BUTTON_PRESSED)
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasAvailablePresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -5, null, Enums.Contacts.PresenceTypes.AVAILABLE))
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Available", presenceLeftTextView.text.toString())
//        assertEquals("Available", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasAwayPresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(DbPresence(null, Enums.Contacts.PresenceStates.AWAY, -30, null, Enums.Contacts.PresenceTypes.AVAILABLE))
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_away_square, shadowDrawable.createdFromResId)
//        assertEquals("Away", presenceLeftTextView.text.toString())
//        assertEquals("Away", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasBusyPresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(DbPresence(null, Enums.Contacts.PresenceStates.BUSY, 100, null, Enums.Contacts.PresenceTypes.AVAILABLE))
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_busy_square, shadowDrawable.createdFromResId)
//        assertEquals("Busy", presenceLeftTextView.text.toString())
//        assertEquals("Busy", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasMobilePresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, null, Enums.Contacts.PresenceTypes.AVAILABLE))
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Mobile", presenceLeftTextView.text.toString())
//        assertEquals("Mobile", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasNonePresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(DbPresence(null, Enums.Contacts.PresenceStates.NONE, -128, null, Enums.Contacts.PresenceTypes.UNAVAILABLE))
//
//        assertEquals(View.INVISIBLE, presenceView.visibility)
//    }
//
//    @Test
//    fun contactHeaderViewPresenceObserverValueUpdated_hasNullPresence_setsViewsInfo() {
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//
//        val argumentCaptor: KArgumentCaptor<Observer<DbPresence>> = argumentCaptor()
//        verify(mockContactHeaderViewPresenceLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(null)
//
//        assertEquals(View.INVISIBLE, presenceView.visibility)
//    }
//
//    @Test
//    fun vCardResponseEventObserverValueUpdated_successfulHasVCard_callsToAvatarManagerToGetAvatar() {
//        val avatarImageView: ImageView = (fragment.mContactHeaderView.findViewById(R.id.contact_header_avatar_view) as View).findViewById(R.id.avatar_image_view)
//
//        val avatarBytes = byteArrayOf(2)
//        val argumentCaptor: KArgumentCaptor<Observer<DbSession>> = argumentCaptor()
//        verify(mockOwnVCardLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val mockUserDetails: UserDetails = mock()
//        val mockBitmap: Bitmap = mock()
//
//        whenever(mockViewModel.userDetails).thenReturn(mockUserDetails)
//        whenever(mockUserDetails.fullName).thenReturn("Full Name")
//        whenever(avatarManager.getBitmap(any())).thenReturn(mockBitmap)
//        whenever(avatarManager.byteArrayToString(avatarBytes)).thenReturn("Mock")
//        whenever(avatarManager.stringToByteArray("Mock")).thenReturn(avatarBytes)
//
//        argumentCaptor.firstValue.onChanged(DbSession(null, "USER_AVATAR", avatarManager.byteArrayToString(avatarBytes)))
//
//        val avatarInfoCaptor: KArgumentCaptor<AvatarInfo> = argumentCaptor()
//        verify(avatarManager).getBitmap(avatarInfoCaptor.capture())
//        assertEquals(avatarBytes, avatarInfoCaptor.firstValue.photoData)
//        assertEquals("Full Name", avatarInfoCaptor.firstValue.displayName)
//        assertEquals(mockBitmap, (avatarImageView.drawable as BitmapDrawable).bitmap)
//    }
//
//    @Test
//    fun vCardResponseEventObserverValueUpdated_successfulHasVCard_callsToViewModelToSetIsAvatarLoaded() {
//        val argumentCaptor: KArgumentCaptor<Observer<DbSession>> = argumentCaptor()
//        verify(mockOwnVCardLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        val avatarBytes = byteArrayOf(2)
//
//        whenever(avatarManager.byteArrayToString(avatarBytes)).thenReturn("Mock")
//        whenever(avatarManager.stringToByteArray("Mock")).thenReturn(avatarBytes)
//
//        whenever(avatarManager.isByteArrayNotEmpty(any())).thenReturn(false)
//        argumentCaptor.firstValue.onChanged(DbSession(null, "USER_AVATAR", null))
//        verify(mockViewModel).setIsAvatarLoaded(false)
//
//        whenever(avatarManager.isByteArrayNotEmpty(any())).thenReturn(true)
//        argumentCaptor.firstValue.onChanged(DbSession(null, "USER_AVATAR", avatarManager.byteArrayToString(avatarBytes)))
//        verify(mockViewModel).setIsAvatarLoaded(true)
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_noSessionPresence_callsToDialogManagerToShowErrorDialog() {
//        whenever(mockViewModel.sessionPresence).thenReturn(null)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.MY_STATUS)
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasSessionPresence_callsToDialogManagerToShowErrorDialog() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.NONE
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.MY_STATUS)
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasAvailableSessionPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.AVAILABLE
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_mobile_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Available", presenceLeftTextView.text.toString())
//        assertEquals("Available", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasAwaySessionPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.AWAY
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_away_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_away_square, shadowDrawable.createdFromResId)
//        assertEquals("Away", presenceLeftTextView.text.toString())
//        assertEquals("Away", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasBusySessionPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.BUSY
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_busy_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_busy_square, shadowDrawable.createdFromResId)
//        assertEquals("Busy", presenceLeftTextView.text.toString())
//        assertEquals("Busy", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasMobileSessionPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence(null, Enums.Contacts.PresenceStates.AVAILABLE, -10, "abc", Enums.Contacts.PresenceTypes.AVAILABLE)
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//        val presenceImageView: ImageView = presenceView.findViewById(R.id.presence_image_view)
//        val presenceLeftTextView: TextView = presenceView.findViewById(R.id.presence_left_text_view)
//        val presenceRightTextView: TextView = presenceView.findViewById(R.id.presence_right_text_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(R.id.my_status_mobile_radio_button, fragment.mRadioGroup.checkedRadioButtonId)
//
//        assertEquals(View.VISIBLE, presenceView.visibility)
//        val shadowDrawable = Shadows.shadowOf(presenceImageView.drawable)
//        assertEquals(R.drawable.status_mobile_square, shadowDrawable.createdFromResId)
//        assertEquals("Mobile", presenceLeftTextView.text.toString())
//        assertEquals("Mobile", presenceRightTextView.text.toString())
//    }
//
//    @Test
//    fun xmppErrorEventObserverValueUpdated_hasNoneSessionPresence_setsViewsInfo() {
//        val nextivaPresence = DbPresence()
//        nextivaPresence.status = "abc"
//        nextivaPresence.state = Enums.Contacts.PresenceStates.NONE
//        whenever(mockViewModel.sessionPresence).thenReturn(nextivaPresence)
//
//        val argumentCaptor: KArgumentCaptor<Observer<RxEvents.XmppErrorEvent>> = argumentCaptor()
//        verify(mockXmppErrorEventLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(RxEvents.XmppErrorEvent(Exception()))
//
//        val presenceView: PresenceView = fragment.mContactHeaderView.findViewById(R.id.contact_header_presence_view)
//
//        assertEquals("abc", fragment.mMessageEditText.text.toString())
//        assertEquals(-1, fragment.mRadioGroup.checkedRadioButtonId)
//        assertEquals(View.INVISIBLE, presenceView.visibility)
//    }
//
//    @Test
//    fun connectionErrorObserverValueUpdated_callsToDialogManagerToShowErrorDialog() {
//        val argumentCaptor: KArgumentCaptor<Observer<SingleEvent<Boolean>>> = argumentCaptor()
//        verify(mockConnectionErrorLiveData).observe(eq(fragment), argumentCaptor.capture())
//
//        argumentCaptor.firstValue.onChanged(SingleEvent(true))
//
//        verify(dialogManager).showErrorDialog(fragment.activity!!, Enums.Analytics.ScreenName.MY_STATUS)
//    }
//}
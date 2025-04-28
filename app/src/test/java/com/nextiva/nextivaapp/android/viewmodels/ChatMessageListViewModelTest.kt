package com.nextiva.nextivaapp.android.viewmodels

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.nextiva.nextivaapp.android.BaseRobolectricTest
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.rooms.RoomsEnums
import com.nextiva.nextivaapp.android.features.rooms.api.ChatMessageAttachment
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomAdmin
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMediaCallMetadata
import com.nextiva.nextivaapp.android.features.rooms.api.ConnectRoomMember
import com.nextiva.nextivaapp.android.features.rooms.db.RoomsDbManager
import com.nextiva.nextivaapp.android.features.rooms.model.DbRoom
import com.nextiva.nextivaapp.android.features.rooms.viewmodel.ChatMessageListViewModel
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PlatformRoomsRepository
import com.nextiva.nextivaapp.android.managers.apimanagers.repositories.PresenceRepository
import com.nextiva.nextivaapp.android.managers.interfaces.CalendarManager
import com.nextiva.nextivaapp.android.managers.interfaces.CallManager
import com.nextiva.nextivaapp.android.managers.interfaces.ConfigManager
import com.nextiva.nextivaapp.android.managers.interfaces.NotificationManager
import com.nextiva.nextivaapp.android.managers.interfaces.SessionManager
import com.nextiva.nextivaapp.android.mocks.managers.FakeMediaPlayerManager
import com.nextiva.nextivaapp.android.models.UserDetails
import com.nextiva.nextivaapp.android.rx.interfaces.SchedulerProvider
import com.nextiva.nextivaapp.android.util.getOrAwaitValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.whenever
import javax.inject.Inject

@HiltAndroidTest
class ChatMessageListViewModelTest : BaseRobolectricTest() {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var configManager: ConfigManager

    @Inject
    lateinit var dbManager: DbManager

    @Inject
    lateinit var roomsDbManager: RoomsDbManager

    @Inject
    lateinit var platformRoomsRepository: PlatformRoomsRepository

    @Inject
    lateinit var schedulerProvider: SchedulerProvider

    @Inject
    lateinit var calendarManager: CalendarManager

    @Inject
    lateinit var presenceRepository: PresenceRepository

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var nextivaMediaPlayer: FakeMediaPlayerManager

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var callManager: CallManager

    private lateinit var viewModel: ChatMessageListViewModel
    private lateinit var dbRoom: DbRoom

    override fun setup() {
        super.setup()
        hiltRule.inject()

        val userDetails = UserDetails()
        userDetails.firstName = "First"
        userDetails.lastName = "Last"
        whenever(sessionManager.userDetails).thenReturn(userDetails)

        var admins: List<ConnectRoomAdmin> = ArrayList<ConnectRoomAdmin>()
        var members: List<ConnectRoomMember> = ArrayList<ConnectRoomMember>()

        viewModel = ChatMessageListViewModel(
            ApplicationProvider.getApplicationContext(),
            ApplicationProvider.getApplicationContext(),
            dbManager,
            roomsDbManager,
            platformRoomsRepository,
            schedulerProvider,
            calendarManager,
            sessionManager,
            nextivaMediaPlayer,
            notificationManager,
            callManager
        )

        dbRoom = DbRoom(roomId = "roomId",
            name = "123456",
            description = "Description",
            archived = "archived",
            admins = admins,
            members = members,
            recentActivityType = "type",
            recentActivityTimestamp = "recentActivityTimestamp",
            createdBy = "createdBy",
            createdTime = "createdTime",
            lastModifiedTime = "lastModifiedTime",
            lastModifiedBy = "lastModifiedBy",
            type = "type",
            requestorID = "requestorID",
            requestorMuteNotifications = false,
            requestorFavorite = false,
            requestorHideRoom = false,
            archivedBy = "archivedBy",
            archivedTime = "archivedTime",
            unarchivedBy = "unarchivedBy",
            unarchivedTime = "unarchivedTime",
            corpId = "corpId",
            favoriteInteractionError = "favoriteInteractionError",
            locked = false,
            mediaCallMetaData = ConnectRoomMediaCallMetadata(null, null, null, null, null, null),
            unreadMessageCount = 0,
            ownerId = ""
        )
    }

    @Test
    fun testIsImageError() {
        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.gif"), 200000000), true)
        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.gif"), 2000), false)

        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.png"), 200000000), false)
        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.png"), 2000), false)

        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.exe"), 200000000), true)
        assertEquals(viewModel.isImageError(Uri.parse("file://nextiva.com/test.exe"), 2000), true)
    }

    @Test
    fun testDisplayName() {
        assertEquals(viewModel.displayName(null), "")
        assertEquals(viewModel.displayName(dbRoom), "123456")

        dbRoom.type = RoomsEnums.ConnectRoomsTypes.CURRENT_USER_MY_ROOM.value
        assertEquals(viewModel.displayName(dbRoom), "My Room (First Last)")

        assertEquals(viewModel.displayName(dbRoom, true), "First Last's Room")
        assertEquals(viewModel.displayName(dbRoom, false), "My Room (First Last)")
    }

    @Test
    fun testConvertAttachments() {
        assertEquals(viewModel.convertAttachments(null).size, 0)
    }

    @Test
    fun testIsAllowedImageType() {
        val allowedExtensions = listOf("jpeg", "jpg", "png", "bmp", "gif")

        // Test cases with allowed extensions
        allowedExtensions.forEach { extension ->
            val filename = "sample.$extension"
            val result = viewModel.isAllowedImageType(filename)
            assertEquals(true, result)
        }

        // Test case with a disallowed extension
        val disallowedFilename = "sample.txt"
        val disallowedResult = viewModel.isAllowedImageType(disallowedFilename)
        assertEquals(false, disallowedResult)
    }

    @Test
    fun testIsAllowedFileType() {
        assertEquals(viewModel.isAllowedFileType("example.pdf"), true)
        assertEquals(viewModel.isAllowedFileType("image.jpg"), true)
        assertEquals(viewModel.isAllowedFileType("video.mp4"), true)
        assertEquals(viewModel.isAllowedFileType("doc.docx"), true)
    }

    @Test
    fun testAudioFilePlayer() {
        //
        // test speaker phone setting
        assertEquals(false, nextivaMediaPlayer.isSpeakerPhoneEnabled())
        assertEquals(false, viewModel.attachmentAudioFilePlayer.speakerEnabledLiveData("1").getOrAwaitValue())

        viewModel.toggleSpeaker("1")
        assertEquals(true, nextivaMediaPlayer.isSpeakerPhoneEnabled())
        assertEquals(true, viewModel.attachmentAudioFilePlayer.speakerEnabledLiveData("1").getOrAwaitValue())

        viewModel.toggleSpeaker("1")
        assertEquals(false, nextivaMediaPlayer.isSpeakerPhoneEnabled())
        assertEquals(false, viewModel.attachmentAudioFilePlayer.speakerEnabledLiveData("1").getOrAwaitValue())

        //
        // test progress setting
        // - viewModel converts from 0%-100% to audio file milliseconds
        assertEquals(nextivaMediaPlayer.getCurrentPlayingProgress(), 0)
        viewModel.attachmentAudioFilePlayer.duration = 1600 // 1.6 seconds
        viewModel.audioProgressDragged(50)
        assertEquals(nextivaMediaPlayer.getCurrentPlayingProgress(), 800) // half of 1.6 seconds == 800 milliseconds

        //
        // test setting up and playing the audio file resource
        val filename = "sample-3s.mp3"
        val url = javaClass.classLoader!!.getResource(filename)
        val uri = Uri.parse(url.toString())
        val attachment = ChatMessageAttachment(filename, uri.toString(), "audio/mp3")
        attachment.id = url.toString()  // note: FakeMediaPlayerManager uses the id as the file url

        assertEquals(viewModel.attachmentAudioFilePlayer.isPlaying.value, false)
        viewModel.playAudioAttachment(attachment.id, attachment)
        assertEquals( viewModel.attachmentAudioFilePlayer.isPlaying.value, true)
        assertEquals(nextivaMediaPlayer.getCurrentActiveAudioFileMessageId(), attachment.id)
    }

    @Test
    fun testIsFileAnImageOrVideo() {
        // Test cases for valid image or video files
        assertEquals(viewModel.isTheFileAnImageOrVideo("video1.mov"), true)
        assertEquals(viewModel.isTheFileAnImageOrVideo("image.gif"), true)
        assertEquals(viewModel.isTheFileAnImageOrVideo("image.jpg"), true)
        assertEquals(viewModel.isTheFileAnImageOrVideo("video.mp4"), true)

        // Test cases for invalid image or video files
        assertEquals(viewModel.isTheFileAnImageOrVideo("doc.docx"), false)
        assertEquals(viewModel.isTheFileAnImageOrVideo("myFile.apk"), false)
        assertEquals(viewModel.isTheFileAnImageOrVideo("myFile.iso"), false)
        assertEquals(viewModel.isTheFileAnImageOrVideo("example.pdf"), false)
    }

    @Test
    fun testOneToOneContact() {
        assertEquals(null, viewModel.singleContact.value)
    }
}

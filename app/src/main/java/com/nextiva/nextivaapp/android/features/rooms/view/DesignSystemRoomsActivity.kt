package com.nextiva.nextivaapp.android.features.rooms.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentAudioFilePlayer
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentInfo
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentMenuItems
import com.nextiva.nextivaapp.android.core.common.ui.AttachmentReceivedView
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextField
import com.nextiva.nextivaapp.android.core.common.ui.MessageTextFieldInterface
import com.nextiva.nextivaapp.android.core.common.ui.SendingViaBanner
import com.nextiva.nextivaapp.android.features.messaging.viewmodel.ConversationViewModel
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageBubbleType
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageReceivedView
import com.nextiva.nextivaapp.android.features.rooms.view.components.MessageSentView
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.SmsConversationDetails

class DesignSystemRoomsActivity : BaseActivity(), MessageTextFieldInterface {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, DesignSystemRoomsActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(ComposeView(this).apply {
            setContent {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .navigationBarsPadding()
                        .imePadding()
                ) {

                    HeaderContent(modifier = Modifier)

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState(), reverseScrolling = true)
                            .weight(weight = 1.0f, fill = false)
                    ) {

                        MessageReceivedView(
                            "Lorem ipsum A.",
                            AvatarInfo.Builder().setDisplayName("Peter Seymour").isConnect(true)
                                .build(),
                            "1:40 am",
                            MessageBubbleType.NONE
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
                            AvatarInfo.Builder().setDisplayName("Group Chat").isConnect(true)
                                .build(),
                            "1:40 am",
                            MessageBubbleType.TOP
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor.",
                            null,
                            "1:40 am",
                            MessageBubbleType.MIDDLE
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor sit amet, consectetr elit adipiscing.",
                            null,
                            "1:40 am",
                            MessageBubbleType.BOTTOM
                        )




                        MessageSentView(
                            "Lorem ipsum.",
                            "1:00 am",
                            MessageBubbleType.NONE
                        )
                        MessageSentView(
                            "Lorem ipsum dolor sit amet, consectetr elit adipiscing.",
                            "1:10 am",
                            MessageBubbleType.TOP
                        )
                        MessageSentView(
                            "Lorem ipsum dolor.",
                            null,
                            MessageBubbleType.MIDDLE
                        )
                        MessageSentView(
                            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
                            null,
                            MessageBubbleType.BOTTOM
                        )



                        MessageReceivedView(
                            "Lorem ipsum A.",
                            AvatarInfo.Builder().setDisplayName("Peter Seymour").isConnect(true)
                                .build(),
                            "1:20 am",
                            MessageBubbleType.NONE
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
                            AvatarInfo.Builder().setDisplayName("Group Chat").isConnect(true)
                                .build(),
                            "1:30 am",
                            MessageBubbleType.TOP
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor.",
                            null,
                            "1:40 am",
                            MessageBubbleType.MIDDLE
                        )
                        MessageReceivedView(
                            "Lorem ipsum dolor sit amet, consectetr adipiscing elit.",
                            null,
                            "1:50 am",
                            MessageBubbleType.BOTTOM
                        )

                        mSessionManager.sessionId?.let {
                            AttachmentReceivedView(
                                id = "",
                                thumbnail = "https://nextiva.nextos.com/public/attachment/v3/attachments/thumbnail/63697a25c6856b05fd05d273",
                                filename = "test mp3 file.mp3",
                                contentType = "audio/mp3",
                                sessionId = it,
                                corpAccountNumber = mSessionManager.userInfo?.comNextivaCorpAccountNumber.toString(),
                                avatarInfo = AvatarInfo.Builder().setDisplayName("Group Chat").build(),
                                displayName = "Peter Seymour",
                                displayTime = "1:40 am",
                                bubbleType = MessageBubbleType.NONE,
                                audioPlayer = AttachmentAudioFilePlayer(),
                            )
                        }

                        Spacer(Modifier.height(dimensionResource(R.dimen.general_padding_medium)))

                    }

                    MessageTextField(
                        activity = this@DesignSystemRoomsActivity,
                        messageTextFieldInterface = this@DesignSystemRoomsActivity
                    )
                }
            }
        })
    }

    @Composable
    fun HeaderContent(modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(R.color.connectGrey01)),
                verticalAlignment = Alignment.CenterVertically,
            ) {

                TextButton(
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(R.dimen.general_padding_large),
                            end = dimensionResource(R.dimen.general_padding_medium)
                        )
                        .width(dimensionResource(R.dimen.general_view_xlarge))
                        .height(dimensionResource(R.dimen.general_view_xlarge)),
                    shape = CircleShape,
                    onClick = { this@DesignSystemRoomsActivity.finish() }) {

                    Text(
                        text = stringResource(R.string.fa_arrow_left),
                        style = TextStyle(
                            color = colorResource(R.color.connectGrey09),
                            fontSize = dimensionResource(R.dimen.material_text_title).value.sp,
                            fontFamily = FontAwesome,
                            fontWeight = FontWeight.W400,
                        ),
                    )
                }

                Text(
                    text = "Ashlynn Dias, Adeline Johnson-Markson",
                    color = colorResource(R.color.connectSecondaryDarkBlue),
                    style = TypographyBody2,
                    modifier = Modifier.padding(
                        start = dimensionResource(R.dimen.general_padding_small),
                        top = dimensionResource(R.dimen.general_padding_xmedium),
                        bottom = dimensionResource(R.dimen.general_padding_xmedium),
                    )
                )

            }
        }
    }

    // --------------------------------------------------------------------------------------------
    // MessageTextFieldInterface
    // --------------------------------------------------------------------------------------------
    override val isSending: MutableLiveData<Boolean> = MutableLiveData(false)
    override val editMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val setDraftMessage: MutableLiveData<String?> = MutableLiveData(null)
    override val errorMessages: MutableLiveData<List<String>?> = MutableLiveData(null)
    override val selectedAttachments: MutableLiveData<List<AttachmentInfo>?> = MutableLiveData(null)
    override val sendingPhoneNumbers: MutableLiveData<ArrayList<ConversationViewModel.SendingPhoneNumber>?> = MutableLiveData(null)
    override val conversationDetails: MutableLiveData<SmsConversationDetails?> = MutableLiveData(null)
    override val sendingViaBanner: MutableLiveData<SendingViaBanner?> = MutableLiveData(null)

    override fun onEditMessageCancel() { }
    override fun onSend(text: String?) { }
    override fun onSendingViaBannerClosed() { }
    override fun onValueChanged(text: String) { }
    override fun addAttachments(attachments: List<Uri>) { }
    override fun removeAttachment(info: AttachmentInfo) { }
    override fun isExcludedType(uri: Uri): Boolean { return false }
    override fun onAlertError() { }
    override fun isImageError(uri: Uri, byteSize: Long): Boolean { return false }
    override fun hasThumbnail(uri: Uri): Boolean { return false }
    override fun menuItems() : List<AttachmentMenuItems> { return listOf() }
}

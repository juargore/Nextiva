package com.nextiva.nextivaapp.android.features.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.IconButton
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.asFlow
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.adapters.masterlistadapter.listitems.VoicemailListItem
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.DbManager
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.managers.interfaces.AvatarManager
import com.nextiva.nextivaapp.android.managers.interfaces.NextivaMediaPlayer
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.util.extensions.isNull
import com.nextiva.nextivaapp.android.view.compose.ConnectAvatarView
import com.nextiva.nextivaapp.android.view.fontawesome.FontTextView
import io.reactivex.Single
import io.reactivex.observers.DisposableSingleObserver

@SuppressLint("UseCompatLoadingForDrawables")
@Composable
fun ConnectVoicemailListItem(
    mListItem: VoicemailListItem?,
    avatarInfo: AvatarInfo,
    avatarManager: AvatarManager,
    time: String?,
    startTime: String,
    mContext: Context,
    duration: String,
    isSmsEnabled: Boolean,
    isValidSmsNumber: Boolean,
    hasRating: Boolean = false,
    onClick: () -> Unit,
    readButton: () -> Unit,
    deleteButton: () -> Unit,
    contactButton: () -> Unit,
    smsButton: () -> Unit,
    phoneButton: () -> Unit,
    thumbsUpButton: () -> Unit,
    thumbsDownButton: () -> Unit,
    nextivaMediaPlayer: NextivaMediaPlayer,
    dbManager: DbManager
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val interactionSource = remember { MutableInteractionSource() }
    var showCheckBox by remember { mutableStateOf(false) }
    var checkBoxIsChecked by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var isSpeakerEnabled by remember { mutableStateOf(nextivaMediaPlayer.isSpeakerPhoneEnabled()) }
    var nextivaContact by remember { mutableStateOf(mListItem?.nextivaContact) }
    var hasTranscription by remember { mutableStateOf(!mListItem?.voicemail?.transcription.isNullOrEmpty()) }
    var isPlaying by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableIntStateOf(0) }
    val isPlayingPaused by nextivaMediaPlayer.getCurrentPlayingVoicemailPausedLiveData().asFlow().collectAsState(initial = false)
    val messageIdChanged by nextivaMediaPlayer.getActiveVoicemailMessageIdChangedLiveData().observeAsState()
    val title = mListItem?.data?.uiName ?: mListItem?.data?.name ?: mListItem?.data?.callerId ?: mContext.getString(R.string.general_unavailable)

    val resetPlayState = {
        isPlaying = false
        isSpeakerEnabled = nextivaMediaPlayer.isSpeakerPhoneEnabled()
        sliderPosition = 0
    }

    val collapseListItem = {
        if(expanded){
            expanded = false
            isPlaying = false
            sliderPosition = 0
            hasTranscription = false
        }
    }

    val expandListItem = {
        if(!expanded){
            expanded = true
            isPlaying = nextivaMediaPlayer.isSpeakerPhoneEnabled()
            hasTranscription = !mListItem?.voicemail?.transcription.isNullOrEmpty()
        }
    }

    if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == mListItem?.voicemail?.actualVoiceMailId) {
        expandListItem()
        if (nextivaMediaPlayer.isPlaying()) {
            isPlaying = true
        } else {
            nextivaMediaPlayer.getCurrentPlayingProgress().let { progress ->
                sliderPosition = progress.div(1000)
            }
        }
    } else {
        collapseListItem()
    }

    nextivaMediaPlayer.getCurrentPlayingProgressChangedLiveData().observe(lifecycleOwner) { progress ->
        if (nextivaMediaPlayer.getCurrentActiveAudioFileMessageId() == mListItem?.voicemail?.actualVoiceMailId) {
            if (progress >= 0) {
                sliderPosition = progress / 1000
            } else if (!nextivaMediaPlayer.isPlaying()) {
                resetPlayState()
            }

            isPlaying = nextivaMediaPlayer.isPlaying()
        }
    }

    LaunchedEffect(isPlayingPaused){
        isPlaying = false
    }

    LaunchedEffect(messageIdChanged){
        mListItem?.voicemail?.let { voicemail ->
            if (voicemail.actualVoiceMailId != nextivaMediaPlayer.getCurrentActiveAudioFileMessageId()) {
                resetPlayState()
                collapseListItem()
            } else {
                expandListItem()
            }
        }
    }

    LaunchedEffect(mListItem?.strippedNumber) {
        if (!mListItem?.strippedNumber.isNullOrEmpty()) {
            dbManager.getConnectContactFromPhoneNumber(mListItem?.strippedNumber)
                .flatMap { dbResponse ->
                    if (dbResponse.value != null) {
                        Single.just(dbResponse.value)
                    } else {
                        Single.error(Throwable("Contact not found"))
                    }
                }
                .onErrorResumeNext { throwable: Throwable ->
                    if (mListItem?.data?.userId.isNullOrEmpty()) {
                        Single.error(throwable)
                    } else {
                        dbManager.getNextivaContactByUserId(mListItem?.data?.userId!!)
                    }
                }
                .subscribe(object : DisposableSingleObserver<NextivaContact>() {
                    override fun onSuccess(contact: NextivaContact) {
                        if (contact.dbId != null) {
                            mListItem?.nextivaContact = contact
                            nextivaContact = contact
                        } else {
                            mListItem?.nextivaContact = null
                            nextivaContact = null
                        }
                    }

                    override fun onError(e: Throwable) {
                        mListItem?.nextivaContact = null
                        nextivaContact = null
                    }
                })
        }
    }

    val cardBackground = if (!checkBoxIsChecked) {
        Modifier.background(color = colorResource(R.color.connectWhite))
    } else {
        Modifier
            .border(
                width = dimensionResource(R.dimen.hairline_small).value.dp,
                color = colorResource(R.color.connectSecondaryBrightBlue)
            )
            .background(color = colorResource(R.color.connectSecondaryLightBlue))
    }

    mListItem?.isChecked?.let {
        showCheckBox = true
        checkBoxIsChecked = it
        collapseListItem()
    } ?: run {
        showCheckBox = false
    }

    Column(
        modifier = cardBackground
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    if (mListItem?.isChecked != null) {
                        expanded = false
                        onClick()
                    } else {
                        if (expanded) {
                            resetPlayState()
                            nextivaMediaPlayer.finishPlayingAudioFile()
                            nextivaMediaPlayer.setCurrentActiveAudioFileMessageId("")
                        } else {
                            mListItem?.voicemail?.actualVoiceMailId?.let {
                                resetPlayState()
                                nextivaMediaPlayer.finishPlayingAudioFile()
                                nextivaMediaPlayer.setCurrentActiveAudioFileMessageId(it)
                            }
                        }
                    }
                })
            .animateContentSize()
            .padding(vertical = dimensionResource(R.dimen.general_vertical_margin_medium))
            .contentDescription(
                stringResource(
                    id = R.string.voicemail_list_item_content_description,
                    if (mListItem?.voicemail?.isRead == true) {
                        "Read"
                    } else {
                        "Unread"
                    },
                    title
                )
            )
    ) {
        Row(modifier = Modifier
            .padding(
                start = dimensionResource(R.dimen.general_padding_xmedium),
                end = dimensionResource(R.dimen.general_padding_medium)
            ),
            verticalAlignment = Alignment.CenterVertically) {
            if (showCheckBox) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(dimensionResource(R.dimen.avatar_connect_call_size))
                        .padding(start = dimensionResource(R.dimen.general_padding_small))
                ) {
                    Checkbox(
                        checked = checkBoxIsChecked,
                        onCheckedChange = { onClick() },
                        colors = CheckboxDefaults.colors(
                            checkmarkColor = colorResource(R.color.connectWhite),
                            checkedColor = colorResource(R.color.nextivaPrimaryBlue),
                            uncheckedColor = colorResource(R.color.connectGrey09)
                        )
                    )
                }
            }

            ConnectAvatarView(
                avatarInfo = avatarInfo,
                avatarManager = avatarManager,
                modifier = Modifier.size(
                    dimensionResource(R.dimen.avatar_connect_call_size).value.dp
                )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(
                        start = dimensionResource(id = R.dimen.general_horizontal_margin_xmedium),
                        end = dimensionResource(id = R.dimen.general_padding_medium)
                    )
            ) {
                val colorResource = colorResource(
                    if (mListItem?.isBlocked == true) {
                        R.color.connectGreyDisabled
                    } else {
                        if (mListItem?.voicemail?.isRead == true) R.color.connectSecondaryDarkBlue else R.color.connectPrimaryBlue
                    }
                )

                Text(
                    text = title,
                    color = colorResource,
                    fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                    fontWeight = FontWeight.W800,
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    fontFamily = FontLato,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.contentDescription(stringResource(id = R.string.voicemail_list_item_name_content_description, title))
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = dimensionResource(id = R.dimen.general_padding_xsmall))
                ) {
                    AndroidView(modifier = Modifier
                        .size(17.dp),
                        factory = { context ->
                            FontTextView(context).apply {
                                setIcon(R.string.fa_voicemail, Enums.FontAwesomeIconType.REGULAR)
                                setTextColor(mContext.getColor(R.color.connectGrey09))
                            }
                        })

                    Text(
                        modifier = Modifier
                            .padding(start = dimensionResource(R.dimen.general_padding_xsmall))
                            .contentDescription(
                                stringResource(id = R.string.voicemail_list_item_number_content_description, title)
                            ),
                        text = mListItem?.voicemail?.formattedPhoneNumber ?: mListItem?.strippedNumber ?: "",
                        color = colorResource(R.color.connectGrey10),
                        fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                        maxLines = 1,
                        fontFamily = FontLato,
                        overflow = TextOverflow.Ellipsis
                    )
                }

            }

            Column(
                modifier = Modifier,
                horizontalAlignment = AbsoluteAlignment.Right
            ) {
                Text(
                    text = time ?: "",
                    modifier = Modifier.padding(
                        end = dimensionResource(R.dimen.general_padding_small),
                        bottom = dimensionResource(R.dimen.general_padding_xsmall)
                    ),
                    fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                    color = colorResource(R.color.connectGrey10),
                    fontFamily = FontLato,
                )

                if (mListItem?.isBlocked == true) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = colorResource(R.color.surfaceError),
                                shape = RoundedCornerShape(dimensionResource(R.dimen.general_view_xxsmall).value.dp)
                            )
                            .padding(
                                horizontal = dimensionResource(R.dimen.general_horizontal_margin_xxsmall),
                                vertical = dimensionResource(R.dimen.hairline_large)
                            )
                    ) {
                        Text(
                            text = mContext.getString(R.string.connect_contact_details_blocked_label),
                            color = colorResource(R.color.connectSecondaryRed),
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

        }

        if (expanded) {
            Text(
                text = startTime,
                modifier = Modifier.padding(
                    start = dimensionResource(R.dimen.general_padding_xxxxlarge),
                    top = dimensionResource(R.dimen.general_padding_xsmall),
                    bottom = dimensionResource(R.dimen.general_padding_small)
                ),
                color = colorResource(R.color.connectGrey10),
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                maxLines = 1,
                fontFamily = FontLato,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = dimensionResource(R.dimen.general_padding_small)),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .clickable {
                            var userNeedsInternetToPlayVoiceMail = true
                            val messageDetailsPath = mListItem?.voicemail?.actualVoiceMailId

                            messageDetailsPath?.split("/")?.last()?.let { messageUuid ->
                                val existsVoicemailInCache = nextivaMediaPlayer.getAudioFileFromCache(mContext, messageUuid)
                                userNeedsInternetToPlayVoiceMail = existsVoicemailInCache == null
                            }

                            // if file exists in cache -> user can play it offline
                            // if file does not exist in cache -> user can play it only if internet is available

                            if ((userNeedsInternetToPlayVoiceMail && nextivaMediaPlayer.isInternetAvailableToPlayMedia()) || !userNeedsInternetToPlayVoiceMail) {
                                if (nextivaMediaPlayer.isPlaying()) {
                                    nextivaMediaPlayer.pausePlaying()
                                    isPlaying = false
                                } else {
                                    if ((mListItem?.voicemail?.duration ?: 0) == sliderPosition && sliderPosition != 0) {
                                        resetPlayState()
                                    } else {
                                        mListItem?.voicemail?.actualVoiceMailId?.let { voicemailId ->
                                            mListItem.voicemail.messageId?.let { messageId ->
                                                nextivaMediaPlayer.playVoicemail(
                                                    messageDetailsPath = voicemailId,
                                                    context = mContext,
                                                    callId = messageId,
                                                    isRead = mListItem.voicemail.isRead == true
                                                )
                                            }
                                        }
                                        isPlaying = true
                                    }
                                }
                            }
                        }
                        .padding(dimensionResource(id = R.dimen.general_padding_small))
                        .contentDescription(
                            stringResource(id = R.string.voicemail_list_item_play_button_content_description, title)
                        )
                ){
                    AndroidView(modifier = Modifier.size(dimensionResource(id = R.dimen.material_text_display1)),
                        factory = { context ->
                            FontTextView(context).apply {
                                background = context.getDrawable(R.drawable.rounded_background)
                                gravity = Gravity.CENTER
                                backgroundTintList =
                                    context.getColorStateList(R.color.connectPrimaryBlue)
                                setIcon(if(!isPlaying) R.string.fa_play else R.string.fa_pause, Enums.FontAwesomeIconType.SOLID)
                                setTextColor(context.getColor(R.color.connectWhite))
                            }
                        },
                        update = {
                            it.setIcon(if(!isPlaying) R.string.fa_play else R.string.fa_pause, Enums.FontAwesomeIconType.SOLID)
                        })
                }

                Slider(
                    value = sliderPosition.toFloat(),
                    onValueChange = {
                        sliderPosition = it.toInt()
                    },
                    valueRange = 0f..(mListItem?.voicemail?.duration?.toFloat() ?: 0f),
                    onValueChangeFinished = {
                        nextivaMediaPlayer.setProgress(sliderPosition,true)
                    },
                    steps = 0,
                    modifier = Modifier
                        .height(1.dp)
                        .weight(1f)
                        .contentDescription(
                            stringResource(id = R.string.voicemail_list_item_seek_bar_content_description, title)
                        ),
                    colors = SliderDefaults.colors(
                        thumbColor = colorResource(id = R.color.connectGrey08),
                        activeTrackColor = colorResource(id = R.color.connectGrey08),
                        inactiveTrackColor = colorResource(id = R.color.connectGrey03)
                    )
                )

                Text(
                    text = duration,
                    modifier = Modifier
                        .padding(start = dimensionResource(R.dimen.general_padding_xsmall))
                        .contentDescription(
                            stringResource(
                                id = R.string.voicemail_list_item_duration_content_description,
                                title
                            )
                        ),
                    color = colorResource(R.color.connectGrey10),
                    fontSize = 10.sp,
                    maxLines = 1,
                    fontFamily = FontLato,
                    overflow = TextOverflow.Ellipsis,

                    )

                IconButton(
                    onClick = {
                        nextivaMediaPlayer.toggleSpeakerPhone(mContext)
                        isSpeakerEnabled = nextivaMediaPlayer.isSpeakerPhoneEnabled()
                    }) {
                    AndroidView(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.material_text_display1)),
                        factory = { context ->
                            FontTextView(context).apply {
                                background = context.getDrawable(R.drawable.rounded_background)
                                gravity = Gravity.CENTER
                                setIcon(R.string.fa_volume_up, Enums.FontAwesomeIconType.SOLID)
                                setTextColor(context.getColor(R.color.connectWhite))
                                setSpeakerButtonAttributes(this, title, isSpeakerEnabled, mContext)
                            }
                        },
                        update = {
                            setSpeakerButtonAttributes(it, title, isSpeakerEnabled, mContext)
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        top = dimensionResource(id = R.dimen.general_vertical_margin_large),
                        end = dimensionResource(R.dimen.general_padding_small)
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_voicemail_check),
                    contentDescription = stringResource(
                        id = R.string.voicemail_list_item_read_button_content_description,
                        title,
                        if (mListItem?.voicemail?.isRead == true) "Read" else "Unread"),
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(id = R.dimen.general_padding_small),
                            end = dimensionResource(id = R.dimen.general_padding_small)
                        )
                        .size(dimensionResource(id = R.dimen.general_horizontal_margin_large))
                        .clickable { readButton() },
                    colorFilter = ColorFilter.tint(
                        color = colorResource(if (mListItem?.voicemail?.isRead == true) R.color.connectPrimaryGrey else R.color.connectPrimaryBlue),
                    )
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clickable {
                            if (!mListItem?.voicemail?.address.isNullOrEmpty()) {
                                resetPlayState()
                                phoneButton()
                            }
                        }
                        .padding(dimensionResource(id = R.dimen.general_padding_small))
                        .contentDescription(
                            stringResource(
                                id = R.string.voicemail_list_item_call_button_content_description,
                                title
                            )
                        )
                ){
                    AndroidView(modifier = Modifier.size(dimensionResource(id = R.dimen.material_text_display1)),
                        factory = { context ->
                            FontTextView(context).apply {
                                background = context.getDrawable(R.drawable.rounded_background)
                                gravity = Gravity.CENTER
                                backgroundTintList = context.getColorStateList(R.color.connectPrimaryBlue)
                                setTextColor(context.getColor(R.color.connectWhite))
                                setIcon(R.string.fa_phone_alt, Enums.FontAwesomeIconType.SOLID)
                            }
                        })
                }

                if (isSmsEnabled) {
                    Box(
                        modifier = Modifier
                            .clickable {
                                if (isValidSmsNumber) {
                                    smsButton()
                                }
                            }
                            .padding(dimensionResource(id = R.dimen.general_padding_small))
                    ) {
                        AndroidView(
                            modifier = Modifier
                                .size(dimensionResource(id = R.dimen.material_text_display1))
                                .contentDescription(
                                    stringResource(id = R.string.voicemail_list_item_sms_button_content_description, title)
                                ),
                            factory = { context ->
                                FontTextView(context).apply {
                                    background = context.getDrawable(R.drawable.rounded_background)
                                    gravity = Gravity.CENTER
                                    backgroundTintList = if (isValidSmsNumber) {
                                        context.getColorStateList(R.color.connectPrimaryBlue)
                                    } else {
                                        context.getColorStateList(R.color.connectPrimaryBlue).withAlpha(75)
                                    }
                                    setTextColor(context.getColor(R.color.connectWhite))
                                    setIcon(R.string.fa_comment_dots, Enums.FontAwesomeIconType.SOLID)
                                }
                            })
                    }
                }

                Box(
                    modifier = Modifier
                        .clickable {
                            if (!nextivaContact.isNull()) {
                                contactButton()
                            }
                        }
                        .padding(dimensionResource(id = R.dimen.general_padding_small))
                ){
                    AndroidView(
                        modifier = Modifier
                            .size(dimensionResource(id = R.dimen.material_text_display1))
                            .contentDescription(
                                stringResource(id = R.string.voicemail_list_item_contact_button_content_description, title)
                            ),
                        factory = { context ->
                            FontTextView(context).apply {
                                background = context.getDrawable(R.drawable.rounded_background)
                                gravity = Gravity.CENTER
                                backgroundTintList = context.getColorStateList(R.color.connectGrey09)
                                setTextColor(context.getColor(R.color.connectWhite))
                                setIcon(R.string.fa_user, Enums.FontAwesomeIconType.SOLID)
                            }
                        },
                        update = {
                            it.backgroundTintList = if(!nextivaContact.isNull())
                                mContext.getColorStateList(R.color.connectGrey09)
                            else
                                mContext.getColorStateList(R.color.disabledTextGrey)
                        })
                }

                Box(
                    modifier = Modifier
                        .clickable { deleteButton() }
                        .padding(dimensionResource(id = R.dimen.general_padding_small))
                ){
                    AndroidView(modifier = Modifier
                        .size(dimensionResource(id = R.dimen.material_text_display1))
                        .contentDescription(
                            stringResource(id = R.string.voicemail_list_item_delete_button_content_description, title)
                        ),
                        factory = { context ->
                            FontTextView(context).apply {
                                background = context.getDrawable(R.drawable.rounded_background)
                                gravity = Gravity.CENTER
                                backgroundTintList = context.getColorStateList(R.color.connectPrimaryRed)
                                setTextColor(context.getColor(R.color.connectWhite))
                                setIcon(R.string.fa_trash_alt, Enums.FontAwesomeIconType.SOLID)
                            }
                        })
                }
            }

            if (hasTranscription) {
                ExpandableText(text = mListItem?.voicemail?.transcription.orEmpty())

                if (hasRating) {
                    Text(
                        text = stringResource(id = R.string.voicemail_list_rate_this_service),
                        modifier = Modifier.padding(
                            top = dimensionResource(id = R.dimen.general_padding_medium),
                            start = dimensionResource(id = R.dimen.general_padding_small)
                        ),
                        color = colorResource(id = R.color.connectGrey10),
                        fontSize = dimensionResource(id = R.dimen.material_text_caption).value.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(resId = R.font.lato_heavy))
                    )

                    Row(
                        modifier = Modifier.padding(top = dimensionResource(id = R.dimen.general_padding_small)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clickable { thumbsDownButton() }
                                .padding(dimensionResource(id = R.dimen.general_padding_small))
                        ){
                            AndroidView(modifier = Modifier
                                .size(dimensionResource(id = R.dimen.material_text_display1)),
                                factory = { context ->
                                    FontTextView(context).apply {
                                        background = context.getDrawable(R.drawable.rounded_background)
                                        gravity = Gravity.CENTER
                                        backgroundTintList =
                                            context.getColorStateList(R.color.connectGrey03)
                                        setTextColor(context.getColor(R.color.connectGrey10))
                                        setIcon(R.string.fa_thumbs_down, Enums.FontAwesomeIconType.SOLID)
                                    }
                                },
                                update = {
                                    it.setTextColor(
                                        if (mListItem?.voicemail?.rating == Enums.VoicemailRating.NEGATIVE) {
                                            mContext.getColor(R.color.connectPrimaryBlue)
                                        } else {
                                            mContext.getColor(R.color.connectGrey10)
                                        }
                                    )
                                })
                        }

                        Box(
                            modifier = Modifier
                                .clickable { thumbsUpButton() }
                                .padding(dimensionResource(id = R.dimen.general_padding_small))
                        ){
                            AndroidView(modifier = Modifier
                                .size(dimensionResource(id = R.dimen.material_text_display1)),
                                factory = { context ->
                                    FontTextView(context).apply {
                                        background = context.getDrawable(R.drawable.rounded_background)
                                        gravity = Gravity.CENTER
                                        backgroundTintList =
                                            context.getColorStateList(R.color.connectGrey03)
                                        setTextColor(context.getColor(R.color.connectGrey10))
                                        setIcon(R.string.fa_thumbs_up, Enums.FontAwesomeIconType.SOLID)
                                    }
                                },
                                update = {
                                    it.setTextColor(
                                        if(mListItem?.voicemail?.rating == Enums.VoicemailRating.NEGATIVE)
                                            mContext.getColor(R.color.connectPrimaryBlue)
                                        else
                                            mContext.getColor(R.color.connectGrey10)
                                    )
                                })
                        }

                    }
                }
            }
        }
    }
}

fun setSpeakerButtonAttributes(
    fontTextView: FontTextView,
    title: String,
    speakerEnabled: Boolean,
    mContext: Context
) {
    if (speakerEnabled) {
        fontTextView.backgroundTintList = mContext.getColorStateList(R.color.connectPrimaryBlue)
        fontTextView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_speaker_button_content_description,
            title,
            "On"
        )
    } else {
        fontTextView.backgroundTintList = mContext.getColorStateList(R.color.connectGrey09)
        fontTextView.contentDescription = mContext.getString(
            R.string.voicemail_list_item_speaker_button_content_description,
            title,
            "Off"
        )
    }

}
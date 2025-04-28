package com.nextiva.nextivaapp.android.core.common.ui

import android.text.TextUtils
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.common.FileUtil
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolid
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2

@Composable
fun AttachmentFileView(
    id: String,
    faIconId: Int,
    faFontFamily: FontFamily = FontAwesome,
    filename: String,
    borderShape: RoundedCornerShape,
    audioPlayer: AttachmentAudioFilePlayer,
    audioLength: String? = null,
    progressDragged: (Int) -> Unit,
    playButtonClicked: () -> Unit,
    speakerButtonClicked: (String) -> Unit
) {
    val audioIsPlaying: Boolean by audioPlayer.isPlaying.observeAsState(false)
    val audioProgress: Int by audioPlayer.progress.observeAsState(0)
    val audioIsSpeakerEnabled: Boolean by audioPlayer.speakerEnabledLiveData(id).observeAsState(false)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(borderShape)
            .background(colorResource(R.color.connectWhite))
            .border(
                width = dimensionResource(R.dimen.hairline_small),
                color = colorResource(R.color.connectGrey03),
                shape = borderShape
            )
    ) {

        Column {

            // file icon and filename
            Row {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(dimensionResource(R.dimen.general_padding_medium))
                ) {
                    Text(
                        text = stringResource(faIconId),
                        color = colorResource(R.color.connectGrey09),
                        fontFamily = faFontFamily,
                        fontSize = dimensionResource(R.dimen.material_text_display1).value.sp,
                        fontWeight = FontWeight.W300,
                        textAlign = TextAlign.Center
                    )
                }

                AndroidView(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(end = dimensionResource(R.dimen.general_padding_medium)),
                    factory = { context ->
                        TextView(context).apply {
                            text = filename
                            maxLines = 1
                            ellipsize = TextUtils.TruncateAt.MIDDLE
                            setTextColor(
                                ContextCompat.getColor(context, R.color.connectSecondaryDarkBlue)
                            )
                            setTextAppearance(R.style.DS_Body2Heavy)
                        }
                    }
                ) {
                    it.text = filename
                }
            }

            // Audio player controls
            if (FileUtil.isAudioFile(filename)) {
                // local slider value state
                var sliderValueRaw by remember { mutableStateOf(audioProgress) }

                // getting current interaction with slider - are we pressing or dragging?
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val isDragged by interactionSource.collectIsDraggedAsState()
                val isInteracting = isPressed || isDragged

                // calculating actual slider value to display
                // depending on whether we are interacting or not
                // using either the local value or the viewModel value
                val sliderValue by derivedStateOf {
                    if (id != audioPlayer.activeItemId) {
                        0
                    } else if (isInteracting) {
                        sliderValueRaw
                    } else {
                        audioProgress
                    }
                }

                Row(
                    modifier = Modifier
                        .padding(bottom = dimensionResource(R.dimen.general_padding_medium))
                ) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.general_padding_medium))
                            .size(dimensionResource(R.dimen.general_view_large))
                            .clip(CircleShape)
                            .background(color = colorResource(R.color.connectPrimaryBlue))
                            .clickable { playButtonClicked() },
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center),
                            text = stringResource(if (audioIsPlaying && id == audioPlayer.activeItemId) R.string.fa_pause else R.string.fa_play),
                            color = colorResource(R.color.connectWhite),
                            fontFamily = FontAwesomeSolid,
                            fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                            fontWeight = FontWeight.W300,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically),
                    ) {
                        Slider(
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .height(dimensionResource(R.dimen.general_view_large)),
                            value = sliderValue.toFloat(),
                            onValueChange = { sliderValueRaw = it.toInt() },
                            onValueChangeFinished = { progressDragged(sliderValue) },
                            interactionSource = interactionSource,
                            valueRange = 0.toFloat()..100.toFloat(),
                            enabled = id == audioPlayer.activeItemId,
                            colors = SliderDefaults.colors(
                                thumbColor = colorResource(R.color.connectPrimaryBlue),
                                activeTrackColor = colorResource(R.color.connectPrimaryBlue),
                                inactiveTrackColor = colorResource(R.color.connectGrey03)
                            )
                        )
                    }

                    audioLength?.let { length ->
                        Text(
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            text = length,
                            color = colorResource(R.color.connectSecondaryDarkBlue),
                            style = TypographyCaption2,
                            textAlign = TextAlign.Center
                        )
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = dimensionResource(R.dimen.general_padding_medium))
                            .size(dimensionResource(R.dimen.general_view_large))
                            .clip(CircleShape)
                            .background(color = colorResource(
                                if (audioIsSpeakerEnabled)
                                    R.color.connectPrimaryBlue
                                else
                                    R.color.connectGrey09))
                            .clickable { speakerButtonClicked(id) }
                    ) {
                        Text(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(end = dimensionResource(id = R.dimen.general_padding_xxsmall)),
                            text = stringResource(R.string.fa_volume_up),
                            color = colorResource(R.color.connectWhite),
                            fontFamily = FontAwesomeSolid,
                            fontSize = dimensionResource(R.dimen.material_text_subhead).value.sp,
                            fontWeight = FontWeight.W300,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun AttachmentFileViewDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(R.color.connectWhite))
    ) {

        Box(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.general_padding_small),
                    end = dimensionResource(R.dimen.general_padding_xxlarge)
                )
                .shadow(
                    elevation = dimensionResource(R.dimen.general_elevation_small),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)),
                    clip = true,
                    ambientColor = colorResource(id = R.color.connectSecondaryDarkBlue_50),
                    spotColor = colorResource(id = R.color.connectSecondaryDarkBlue_50)
                )
        ) {
            AttachmentFileView(
                id = "",
                faIconId = R.string.fa_file_pdf,
                filename = "testPdfFile.pdf",
                borderShape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)),
                audioPlayer = AttachmentAudioFilePlayer(),
                progressDragged = { },
                playButtonClicked = { },
                speakerButtonClicked = { }
            )
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.general_padding_medium)))

        Box(
            modifier = Modifier
                .padding(
                    start = dimensionResource(R.dimen.general_padding_small),
                    end = dimensionResource(R.dimen.general_padding_xxlarge)
                )
                .shadow(
                    elevation = dimensionResource(R.dimen.general_elevation_small),
                    shape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)),
                    clip = true,
                    ambientColor = colorResource(id = R.color.connectSecondaryDarkBlue_50),
                    spotColor = colorResource(id = R.color.connectSecondaryDarkBlue_50)
                )
        ) {
            AttachmentFileView(
                id = "",
                faIconId = R.string.fa_file_pdf,
                filename = "2022 Holidays (US and Mexico) - Time off Request.mp3",
                borderShape = RoundedCornerShape(dimensionResource(R.dimen.connect_message_bubble_square_corner)),
                audioPlayer = AttachmentAudioFilePlayer(),
                progressDragged = { },
                playButtonClicked = { },
                speakerButtonClicked = { }
            )
        }

    }
}
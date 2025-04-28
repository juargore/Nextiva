package com.nextiva.nextivaapp.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.core.notifications.view.component.SimpleCheckbox
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH6
import com.nextiva.nextivaapp.android.models.AudioCodec
import com.nextiva.nextivaapp.android.models.VideoCodec
import com.nextiva.nextivaapp.android.viewmodels.SipConfigurationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SipConfigurationActivity: AppCompatActivity() {


    private lateinit var viewModel: SipConfigurationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[SipConfigurationViewModel::class.java]

        setContentView(ComposeView(this).apply {
            setContent {
                Box(Modifier.safeDrawingPadding()) {
                    SipConfigurationMain()
                }
            }
        })
    }

    @Composable
    fun SipConfigurationMain() {
        val enabledAudioCodecs by viewModel.getEnabledAudioCodecsFlow().collectAsState(initial = viewModel.getEnabledAudioCodecs())
        val isEchoCancellationExpanded = remember { mutableStateOf(false) }
        val echoItemPosition = remember { mutableStateOf(viewModel.getEchoCancellationItemPosition()) }
        val isAggressivenessExpanded = remember { mutableStateOf(false) }
        val aggressivenessItemPosition = remember { mutableStateOf(viewModel.getEchoAggressivenessItemPosition()) }
        val isNoiseSuppressionEnabled = remember { mutableStateOf(viewModel.getNoiseSuppressionEnabled()) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.connectGrey01))
        ) {
            Column {
                ScreenTitleBarView(
                    title = stringResource(id = R.string.sip_configuration_title),
                    onBackButton = { finish() },
                    titleStyle = TypographyH6)

                Text(
                    modifier = Modifier.padding(
                        top = dimensionResource(id = R.dimen.general_padding_medium),
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        bottom = dimensionResource(id = R.dimen.general_padding_small)),
                    text = stringResource(id = R.string.sip_configuration_audio_codecs),
                    style = TypographyBody1Heavy,
                    color = colorResource(id = R.color.connectSecondaryDarkBlue)
                )

                viewModel.getAudioCodecs()?.forEach { audioCodec ->
                    CodecRow(audioCodec = audioCodec, isEnabled = enabledAudioCodecs.enabledCodecs.contains(audioCodec.name)) {
                        viewModel.enableAudioCodec(audioCodec.name, it)
                    }
                }

                Text(
                    modifier = Modifier.padding(
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        top = dimensionResource(id = R.dimen.general_padding_medium),
                        bottom = dimensionResource(id = R.dimen.general_padding_small)),
                    text = stringResource(id = R.string.sip_configuration_video_codecs),
                    style = TypographyBody1Heavy,
                    color = colorResource(id = R.color.connectSecondaryDarkBlue)
                )

                Row(modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        end = dimensionResource(id = R.dimen.general_padding_medium),
                        top = dimensionResource(id = R.dimen.general_padding_medium)
                    )
                    .fillMaxWidth()) {
                    Button(
                        modifier = Modifier
                            .padding(end = dimensionResource(id = R.dimen.general_padding_small))
                            .weight(weight = 1F)
                            .height(dimensionResource(id = R.dimen.general_view_xlarge)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.connectPrimaryBlue)),
                        shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                        onClick = {
                            viewModel.enableAll()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.sip_configuration_enable_all),
                            color = colorResource(id = R.color.connectWhite),
                            style = TypographyButton
                        )
                    }

                    Button(
                        modifier = Modifier
                            .padding(start = dimensionResource(id = R.dimen.general_padding_small))
                            .weight(weight = 1F)
                            .height(dimensionResource(id = R.dimen.general_view_xlarge)),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.connectPrimaryBlue)),
                        shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                        onClick = {
                            viewModel.disableAll()
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.sip_configuration_disable_all),
                            color = colorResource(id = R.color.connectWhite),
                            style = TypographyButton
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isEchoCancellationExpanded.value = true }) {
                    Text(
                        modifier = Modifier.padding(
                            start = dimensionResource(id = R.dimen.general_padding_medium),
                            top = dimensionResource(id = R.dimen.general_padding_medium),
                            bottom = dimensionResource(id = R.dimen.general_padding_small)),
                        text = stringResource(id = R.string.sip_echo_cancellation_option, viewModel.echoCancellationOptions[echoItemPosition.value].first),
                        style = TypographyBody1,
                        color = colorResource(id = R.color.connectSecondaryDarkBlue)
                    )
                }

                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isAggressivenessExpanded.value = true }) {
                    Text(
                        modifier = Modifier.padding(
                            start = dimensionResource(id = R.dimen.general_padding_medium),
                            top = dimensionResource(id = R.dimen.general_padding_medium),
                            bottom = dimensionResource(id = R.dimen.general_padding_small)),
                        text = stringResource(id = R.string.sip_aec_aggressiveness,
                            viewModel.echoCancellationAggressiveness[aggressivenessItemPosition.value].first
                        ),
                        style = TypographyBody1,
                        color = colorResource(id = R.color.connectSecondaryDarkBlue)
                    )
                }

                DropdownMenu(expanded = isEchoCancellationExpanded.value, onDismissRequest = { isEchoCancellationExpanded.value = false }) {
                    viewModel.echoCancellationOptions.forEachIndexed { index, option ->
                        DropdownMenuItem(text = { Text(text = option.first) }, onClick = {
                            isEchoCancellationExpanded.value = false
                            echoItemPosition.value = index
                            viewModel.setEchoCancellation(option)
                        })
                    }
                }

                DropdownMenu(expanded = isAggressivenessExpanded.value, onDismissRequest = { isAggressivenessExpanded.value = false }) {
                    viewModel.echoCancellationAggressiveness.forEachIndexed { index, option ->
                        DropdownMenuItem(text = { Text(text = option.first) }, onClick = {
                            isAggressivenessExpanded.value = false
                            aggressivenessItemPosition.value = index
                            viewModel.setEchoAggressiveness(option)
                        })
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(
                            start = dimensionResource(id = R.dimen.general_padding_medium),
                            end = dimensionResource(id = R.dimen.general_padding_medium),
                            top = dimensionResource(id = R.dimen.general_padding_small),
                            bottom = dimensionResource(id = R.dimen.general_padding_small)
                        )
                ) {
                    SimpleCheckbox(
                        text = stringResource(id = R.string.sip_noise_suppression),
                        textStyle = TypographyBody1,
                        textColor = colorResource(id = R.color.connectSecondaryDarkBlue),
                        checked = isNoiseSuppressionEnabled.value,
                        onCheckChanged = {
                            isNoiseSuppressionEnabled.value = it
                            viewModel.setNoiseSuppression(it)
                        })
                }
            }
        }
    }

    @Composable
    fun CodecRow(audioCodec: AudioCodec? = null,
                 videoCodec: VideoCodec? = null,
                 isEnabled: Boolean,
                 onCheckedChanged: (Boolean) -> Unit) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(
                    start = dimensionResource(id = R.dimen.general_padding_medium),
                    end = dimensionResource(id = R.dimen.general_padding_medium),
                    top = dimensionResource(id = R.dimen.general_padding_small),
                    bottom = dimensionResource(id = R.dimen.general_padding_small)
                )
        ) {
            SimpleCheckbox(
                text = audioCodec?.name ?: videoCodec?.name ?: "No name",
                textStyle = TypographyBody1,
                textColor = colorResource(id = R.color.connectSecondaryDarkBlue),
                checked = isEnabled,
                onCheckChanged = { onCheckedChanged(it) })
        }
    }
}
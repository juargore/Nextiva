package com.nextiva.nextivaapp.android.core.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.models.NextivaContact
import com.nextiva.pjsip.pjsip_lib.sipservice.CallState
import com.nextiva.pjsip.pjsip_lib.sipservice.SipCall

@Composable
fun ActiveCallBanner(sipCall: SipCall, contact: NextivaContact?, isNightModeEnabled: Boolean, durationLiveData: MutableLiveData<String>, onClick: () -> Unit) {

    val systemUiController = rememberSystemUiController()
    val green = colorResource(R.color.connectPrimaryGreen)
    val grey = colorResource(R.color.connectGrey01)

    val duration: String? by durationLiveData.map { duration ->
        duration?.let {
            val parts = duration.split(":")
            if (parts.size > 2) {
                val (hours, minutes, seconds) = parts
                return@map if (hours == "00") {
                    "$minutes:$seconds"
                } else {
                    "$hours:$minutes:$seconds"
                }
            }
        }

        return@map ""
    }.observeAsState()

    DisposableEffect(systemUiController) {
        systemUiController.setStatusBarColor(color = green, darkIcons = false)
        onDispose {
            if (sipCall.state != CallState.CONNECTED) {
                systemUiController.setStatusBarColor(color = grey, darkIcons = isNightModeEnabled)
            }
        }
    }

    Row(Modifier
        .background(green)
        .clickable { onClick() }) {

        Text(modifier = Modifier.padding(dimensionResource(R.dimen.general_padding_xsmall))
            .padding(top = dimensionResource(R.dimen.general_padding_small), bottom = dimensionResource(R.dimen.general_padding_small ))
            .weight(1f)
            .width(0.dp),
            text = "${contact?.uiName ?: sipCall.number ?: "Unavailable"} - $duration",
            color = colorResource(R.color.connectWhite),
            textAlign = TextAlign.Center,
            style = TypographyBody1Heavy
        )
    }
}
/*
 * Copyright (c) 2023. Nextiva, inc. to Present.
 * All rights reserved
 */

package com.nextiva.nextivaapp.android.sip.tone

import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager.getRingtone
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums.Sip.CallTones
import com.nextiva.nextivaapp.android.features.rooms.view.components.ScreenTitleBarView
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH6
import com.nextiva.androidNextivaAuth.util.SharedPreferencesManager
import com.nextiva.nextivaapp.android.sip.tone.data.ToneItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ToneActivity : BaseActivity() {
    companion object {
        private const val PARAMS_TONE_TYPE: String = "PARAMS_TONE_TYPE"

        fun newIntent(context: Context, toneType: Int): Intent {
            val intent = Intent(context, ToneActivity::class.java)

            intent.putExtra(PARAMS_TONE_TYPE, toneType)

            return intent
        }
    }

    private lateinit var viewModel: ToneViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ToneViewModel::class.java]
        viewModel.toneType = intent.getIntExtra(PARAMS_TONE_TYPE, CallTones.ToneTypes.RING)

        setContentView(ComposeView(this).apply {
            setContent {
                val toneList by remember { mutableStateOf(viewModel.toneList) }

                ToneListScreen(
                    this.context,
                    toneList,
                    onBackButton(),
                    setPlayingTone(),
                    viewModel
                )
            }
        })
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        viewModel.selectTone(this, getSavedToneUri())
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onPause() {
        super.onPause()
        stopPlayingTone()
    }

    override fun onStop() {
        super.onStop()
        stopPlayingTone()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayingTone()
    }

    private fun stopPlayingTone() {
        viewModel.selectedTone.let {
            it?.ringtone?.stop()
        }
    }

    private fun setPlayingTone(): (tone: ToneItem) -> Unit {
        return {
            viewModel.selectTone(this, it.uri)
            viewModel.selectedTone = it
            saveSelectedToneToPreferences(it.uri)

        }
    }

    fun onBackButton(): () -> Unit {
        return { finish() }
    }


    private fun saveSelectedToneToPreferences(ringtoneUri: Uri) {
        mSharedPreferencesManager.setString(
            if(viewModel.toneType == CallTones.ToneTypes.NOTIFICATION)
                SharedPreferencesManager.NOTIFICATION_TONE_URI
            else
                SharedPreferencesManager.RINGTONE_URI,
            ringtoneUri.toString()
        )
        viewModel.updateChannelTone(ringtoneUri)
    }

    private fun getSavedToneUri(): Uri? {
        val uriString =
            mSharedPreferencesManager.getString(
                if(viewModel.toneType == CallTones.ToneTypes.NOTIFICATION)
                    SharedPreferencesManager.NOTIFICATION_TONE_URI
                else
                    SharedPreferencesManager.RINGTONE_URI
                , "")
        return uriString?.let { Uri.parse(it) }
    }
}


@Composable
fun ToneListScreen(
    context: Context,
    ringtonesList: List<ToneItem>,
    onBackButton: () -> Unit = {},
    setPlayingRingtone: (ToneItem) -> Unit = {},
    viewModel: ToneViewModel
) {
    var currentlyPlayingRingtone: Ringtone? by remember { mutableStateOf(null) }
    var selectedToneUri by remember { mutableStateOf(viewModel.selectedTone?.uri) }
    val ringtones = remember { mutableStateListOf(*ringtonesList.toTypedArray()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.connectGrey01))
    ) {
        Column {
            ScreenTitleBarView(
                title = viewModel.getTitle(context),
                onBackButton = onBackButton,
                titleStyle = TypographyH6
            )

            ToneList(ringtones, selectedToneUri) { selectedRingtoneItem ->
                selectedToneUri = selectedRingtoneItem.uri
                ringtones.forEach { it.isSelected = it == selectedRingtoneItem }

                currentlyPlayingRingtone?.stop()
                setPlayingRingtone(selectedRingtoneItem)
                currentlyPlayingRingtone = selectedRingtoneItem.ringtone

                if (selectedRingtoneItem.ringtone != null && !selectedRingtoneItem.ringtone.isPlaying) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        selectedRingtoneItem.ringtone.volume = 1f
                        selectedRingtoneItem.ringtone.isLooping = false
                    }

                    selectedRingtoneItem.ringtone.play()
                }
            }
        }
    }
}

@Composable
fun ToneList(
    ringtones: List<ToneItem>,
    selectedUri: Uri?,
    onRingtoneSelected: (ToneItem) -> Unit
) {
    LazyColumn {
        items(ringtones, key = { it.uri.toString() }) { ringtoneItem ->
            RingtoneRow(ringtoneItem, ringtoneItem.uri == selectedUri, onRingtoneSelected)
        }
    }
}

@Composable
fun RingtoneRow(
    toneItem: ToneItem,
    isSelected: Boolean,
    onRingtoneSelected: (ToneItem) -> Unit
) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onRingtoneSelected(toneItem)
        }
        .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            colors = RadioButtonDefaults.colors(colorResource(R.color.connectSecondaryDarkBlue)),
            selected = isSelected,
            onClick = { onRingtoneSelected(toneItem) }
        )
        Text(
            text = toneItem.title,
            modifier = Modifier.padding(start = 8.dp),
            color = colorResource(R.color.connectSecondaryDarkBlue)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun RingtoneRowPreview() {
    val context = LocalContext.current
    val uri = Uri.parse("test")
    val toneItem = ToneItem(
        "Title",
        uri,
        getRingtone(context, uri)
    )
    RingtoneRow(toneItem, false) {}
}

@Preview(showBackground = true)
@Composable
fun RingtoneListPreview() {
    val context = LocalContext.current
    val uri = Uri.parse("test")
    val toneItem = ToneItem(
        "Title",
        uri,
        getRingtone(context, uri),
        true
    )
    val uri1 = Uri.parse("test1")
    val toneItem1 = ToneItem(
        "Title1",
        uri1,
        getRingtone(context, uri1)
    )
    val uri2 = Uri.parse("test2")
    val toneItem2 = ToneItem(
        "Title2",
        uri2,
        getRingtone(context, uri2)
    )
    val ringtones = listOf(toneItem, toneItem1, toneItem2)
    ToneList(ringtones, uri) {}
}

@Preview(showBackground = true)
@Composable
fun RingtoneListScreenPreview() {
    val context = LocalContext.current
    val uri = Uri.parse("test")
    val toneItem = ToneItem(
        "Title",
        uri,
        getRingtone(context, uri)
    )
    val uri1 = Uri.parse("test1")
    val toneItem1 = ToneItem(
        "Title1",
        uri1,
        getRingtone(context, Uri.EMPTY),
        true
    )
    val uri2 = Uri.parse("test2")
    val toneItem2 = ToneItem(
        "Title2",
        uri2,
        getRingtone(context, Uri.EMPTY)
    )
    val ringtones = listOf(toneItem, toneItem1, toneItem2)
    ToneListScreen(context, ringtones, {}, {}, ToneViewModel())
}
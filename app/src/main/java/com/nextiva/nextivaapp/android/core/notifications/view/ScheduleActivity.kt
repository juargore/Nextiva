package com.nextiva.nextivaapp.android.core.notifications.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBarPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.nextiva.nextivaapp.android.BaseActivity
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.core.notifications.models.Schedule
import com.nextiva.nextivaapp.android.core.notifications.view.CreateScheduleActivity.Companion.INTENT_EXTRA_SCHEDULE_ID
import com.nextiva.nextivaapp.android.core.notifications.viewmodel.ScheduleListViewModel
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesome
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyButton
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyH5
import com.nextiva.nextivaapp.android.features.ui.theme.TypographySubtitle1
import com.nextiva.nextivaapp.android.util.extensions.contentDescription
import com.nextiva.nextivaapp.android.util.extensions.nullIfEmpty
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleActivity : BaseActivity() {
    companion object {
        const val INTENT_EXTRA_ON_API_SUCCESS = "onAPiSuccess"
        fun newIntent(context: Context): Intent {
            return Intent(context, ScheduleActivity::class.java)
        }
    }


    private lateinit var viewModel: ScheduleListViewModel

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[ScheduleListViewModel::class.java]

        setContentView(ComposeView(this).apply {
            setContent {
                HomeScreen(context, window, viewModel)
            }
        })
    }

    @ExperimentalMaterialApi
    @Composable
    fun HomeScreen(context: Context, window: Window, viewModel: ScheduleListViewModel) {
        val animationSpec = remember {
            Animatable(0f)
                .run {
                    TweenSpec<Float>(durationMillis = 300)
                }
        }
        val modalBottomSheetState = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true,
            animationSpec = animationSpec
        )

        val coroutineScope = rememberCoroutineScope()
        val isEmpty = remember { mutableStateOf(true) }
        val schedules = viewModel.schedules.collectAsLazyPagingItems()
        val selectedSchedule by viewModel.getDndScheduleFlow().collectAsState(initial = null)
        val shouldShowSnackBar = remember { mutableStateOf(false) }
        val dismissBottomSheet = {
            coroutineScope.launch {
                modalBottomSheetState.hide()
            }
        }

        val createScheduleActivityLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
            onResult = { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data?.getBooleanExtra(INTENT_EXTRA_ON_API_SUCCESS, false) == true) {
                        shouldShowSnackBar.value = true

                        result.data?.getStringExtra(INTENT_EXTRA_SCHEDULE_ID)?.nullIfEmpty()?.let { scheduleId ->
                                viewModel.setPresenceDndSchedule(scheduleId)
                                dismissBottomSheet()
                            }
                    }
                }
            })

        if (shouldShowSnackBar.value) { schedules.refresh() }

        ModalBottomSheetLayout(
            modifier = Modifier
                .navigationBarsPadding()
                .captionBarPadding()
                .imePadding()
                .background(when (modalBottomSheetState.targetValue) {
                    ModalBottomSheetValue.Expanded,
                    ModalBottomSheetValue.HalfExpanded,
                        -> colorResource(R.color.blackOverlay40)
                    else -> colorResource(R.color.connectGrey01)
                })
                .windowInsetsPadding(WindowInsets.statusBars),
            sheetState = modalBottomSheetState,
            sheetElevation = 8.dp,
            scrimColor = colorResource(id = R.color.blackOverlay40),
            sheetShape = RoundedCornerShape(
                topStart = dimensionResource(id = R.dimen.bottom_sheet_rounded_corner),
                topEnd = dimensionResource(id = R.dimen.bottom_sheet_rounded_corner)
            ),
            sheetContent = {
                val onBackButton: () -> Unit = { dismissBottomSheet() }

                if (schedules.itemCount == 0 && isEmpty.value) {
                    BottomSheetEmpty(
                        onCreateClicked = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                            shouldShowSnackBar.value = false
                            launchCreateScheduleIntent(null, createScheduleActivityLauncher)
                        }, onBackButton = onBackButton
                    )

                } else {
                    BottomSheetSelection(schedules,
                        onBackButton = onBackButton,
                        onScheduleSelected = { schedule ->
                            onBackButton()

                            schedule.scheduleId?.let { scheduleId ->
                                viewModel.setPresenceDndSchedule(scheduleId)
                            }
                        },
                        onCreateClicked = {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                            }
                            shouldShowSnackBar.value = false
                            launchCreateScheduleIntent(null, createScheduleActivityLauncher)
                        })
                }
            }) {
            MainSelection(
                selectSchedule = {
                    coroutineScope.launch {
                        modalBottomSheetState.show()
                    }
                },
                onCreateScheduleClick = {
                    shouldShowSnackBar.value = false
                    launchCreateScheduleIntent(null, createScheduleActivityLauncher)
                },
                onEditScheduleClick = {
                    shouldShowSnackBar.value = false
                    selectedSchedule?.let {
                        launchCreateScheduleIntent(it, createScheduleActivityLauncher)
                    }
                },
                removeSelectedSchedule = {
                    viewModel.deletePresenceDndSchedule()
                },
                selectedSchedule,
                shouldShowSnackBar
            )
        }
    }

    private fun launchCreateScheduleIntent(schedule: Schedule?, launcher: ActivityResultLauncher<Intent>) {
        schedule?.let {
            intent = CreateScheduleActivity.newIntent(this, schedule)
        } ?: run {
            intent = CreateScheduleActivity.newIntent(this)
        }

        launcher.launch(intent)
    }

    @Composable
    fun BottomSheetTitle(onBackButton: () -> Unit) {
        Column(
            Modifier
                .fillMaxWidth()
                .background(colorResource(id = R.color.connectGrey01))
        ) {

            Image(
                painter = painterResource(R.drawable.ic_bottom_sheet_pull_down),
                contentDescription = getString(R.string.notifications_schedules_pull_down_icon_accessibility_id),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(dimensionResource(id = R.dimen.general_padding_xsmall))
            )

            Row(
                modifier = Modifier
                    .padding(
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        end = dimensionResource(id = R.dimen.general_padding_xmedium),
                        top = dimensionResource(id = R.dimen.general_padding_small),
                        bottom = dimensionResource(id = R.dimen.general_padding_small)
                    )
            ) {
                Text(
                    modifier = Modifier.contentDescription(stringResource(id = R.string.notifications_schedules_title_accessibility_id)),
                    text = stringResource(id = R.string.notification_schedules_title),
                    style = TypographyH5,
                    color = colorResource(R.color.connectSecondaryDarkBlue)
                )

                Spacer(modifier = Modifier.weight(1.0f))

                TextButton(onClick = { onBackButton() }) {
                    Text(
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .contentDescription(stringResource(id = R.string.notifications_schedule_close_icon_accessibility_id)),
                        text = stringResource(R.string.fa_times),
                        style = TextStyle(
                            color = colorResource(R.color.connectGrey10),
                            fontSize = dimensionResource(R.dimen.material_text_title).value.sp,
                            fontFamily = FontAwesome,
                            fontWeight = FontWeight.Normal,
                        )
                    )
                }
            }
        }
    }

    @Composable
    fun BottomSheetSelection(
        schedules: LazyPagingItems<Schedule>?,
        onScheduleSelected: (Schedule) -> Unit,
        onBackButton: () -> Unit,
        onCreateClicked: () -> Unit
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.connectWhite))
        ) {
            BottomSheetTitle(onBackButton)
            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.notifications_schedules_sub_text_accessibility_id))
                    .padding(
                        vertical = dimensionResource(id = R.dimen.general_padding_large),
                        horizontal = dimensionResource(id = R.dimen.general_padding_medium)
                    ),
                text = stringResource(id = R.string.notification_schedules_subtitle),
                style = TypographyBody1,
                color = colorResource(R.color.connectGrey10)
            )

            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.notifications_schedules_sub_header_personal_schedules_accessibility_id))
                    .padding(
                        bottom = dimensionResource(id = R.dimen.general_padding_medium),
                        start = dimensionResource(id = R.dimen.general_padding_medium),
                        end = dimensionResource(id = R.dimen.general_padding_medium)
                    ),
                text = stringResource(id = R.string.notification_schedules_personal),
                style = TypographySubtitle1,
                color = colorResource(R.color.connectSecondaryDarkBlue)
            )

            LazyColumn(
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {

                schedules?.let { schedules ->
                    items(
                        count = schedules.itemCount,
                        key = schedules.itemKey(),
                        contentType = schedules.itemContentType()
                    ) { index ->
                        val schedule = schedules[index]
                        schedule?.let {
                            val dayAndHourString = schedule.getDayString(LocalContext.current)
                            ScheduleItemView(
                                title = schedule.name ?: "",
                                days = dayAndHourString.first,
                                hours = dayAndHourString.second,
                                isSelected = schedule.isDndSchedule
                            ) {
                                onScheduleSelected(schedule)
                            }
                        }
                    }
                }
            }

            Divider(
                color = colorResource(R.color.connectGrey01),
                modifier = Modifier.fillMaxWidth(),
                thickness = dimensionResource(R.dimen.general_view_xxsmall)
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(id = R.dimen.general_view_xxxxxlarge))
                    .padding(dimensionResource(id = R.dimen.general_padding_medium)),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.connectPrimaryBlue)),
                shape = RoundedCornerShape(dimensionResource(id = androidx.cardview.R.dimen.cardview_default_radius)),
                onClick = { onCreateClicked() }
            ) {
                Text(
                    modifier = Modifier
                        .contentDescription(stringResource(id = R.string.notifications_schedule_create_schedule_accessibility_id)),
                    text = stringResource(id = R.string.notification_schedules_add_button),
                    color = colorResource(id = R.color.connectWhite),
                    style = TypographyButton
                )
            }
        }
    }

    @Composable
    private fun BottomSheetEmpty(onCreateClicked: () -> Unit, onBackButton: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.connectWhite))
        ) {
            BottomSheetTitle(onBackButton)
            Spacer(modifier = Modifier.weight(0.7f))

            Box(
                modifier = Modifier
                    .size(dimensionResource(id = R.dimen.general_view_xxxxxxxxxxxxlarge))
                    .clip(CircleShape)
                    .background(colorResource(id = R.color.connectPrimaryLightBlue))
                    .align(Alignment.CenterHorizontally),
            ) {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .contentDescription(stringResource(id = R.string.notifications_schedules_image_accessibility_id)),
                    text = stringResource(R.string.fa_clock),
                    style = TextStyle(
                        color = colorResource(R.color.connectGrey08),
                        fontSize = dimensionResource(R.dimen.general_view_xxlarge).value.sp,
                        fontFamily = FontAwesome,
                        fontWeight = FontWeight.Normal,
                    )
                )
            }

            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.notifications_schedules_no_schedules_text_accessibility_id))
                    .padding(top = dimensionResource(id = R.dimen.general_padding_large))
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.notification_schedules_empty_title),
                color = colorResource(id = R.color.connectSecondaryDarkBlue),
                style = TypographyH5
            )

            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.notifications_schedules_no_schedules_sub_text_accessibility_id))
                    .padding(top = dimensionResource(id = R.dimen.general_padding_xmedium))
                    .align(Alignment.CenterHorizontally),
                text = stringResource(id = R.string.notification_schedules_empty_subtitle),
                color = colorResource(id = R.color.connectGrey10),
                style = TypographyBody1
            )

            Text(
                modifier = Modifier
                    .contentDescription(stringResource(id = R.string.notifications_schedule_create_schedule_accessibility_id))
                    .padding(dimensionResource(id = R.dimen.general_padding_large))
                    .align(Alignment.CenterHorizontally)
                    .clickable { onCreateClicked() },
                text = stringResource(id = R.string.notification_preferences_create),
                color = colorResource(id = R.color.connectPrimaryBlue),
                style = TypographyBody1Heavy
            )

            Spacer(modifier = Modifier.weight(0.7f))
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun ScheduleActivityPreview() {
        BottomSheetSelection(null,
            onScheduleSelected = {},
            onBackButton = {},
            onCreateClicked = {})
    }

}

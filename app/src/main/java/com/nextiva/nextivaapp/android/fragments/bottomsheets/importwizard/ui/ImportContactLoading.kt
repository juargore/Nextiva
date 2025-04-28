package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.models.BulkContactsResult
import com.nextiva.nextivaapp.android.util.extensions.orZero
import com.nextiva.nextivaapp.android.viewmodels.BottomSheetImportContactLoadingViewModel
import kotlinx.coroutines.delay

@Preview
@Composable
fun previewLoading() {
    importContactLoadingUI(
        BottomSheetImportContactLoadingViewModel.State.Loading(1),
        {},{},{},{}
    )
}

@Preview
@Composable
fun previewError() {
    importContactLoadingUI(
        BottomSheetImportContactLoadingViewModel.State.Error(),
        {},{},{},{}
    )
}

@Preview
@Composable
fun previewSuccess() {
    importContactLoadingUI(
        BottomSheetImportContactLoadingViewModel.State.Success(
            BulkContactsResult(
                id = "", corpAcctNumber = null, submittedBy = null, recordCount = null, active = null, jobSubType = null, jobType = null, webhook = null, createdAt = null, endedAt = null,
                metadata = BulkContactsResult.Metadata(duplicateUpdateStrategyType = "skip"),
                result = BulkContactsResult.Result(
                    inserted = 3, failed = 2, skipped = 3, updated = 3,
                    converted = 0, deleted = 0, processed = null, status = null,
                    jobType = null, jobSubType = null, eventSynced = null, exported = null, received = null, submittedBy = null, results = null
                )), false
        ),
        {},{},{},{}
    )
}

@Composable
fun importContactLoadingUI(
    uiState: BottomSheetImportContactLoadingViewModel.State,
    onDone: (() -> Unit),
    onRetry: (() -> Unit),
    onCancel: (() -> Unit),
    onFinish: (() -> Unit)
) {
    MaterialTheme(typography = typography) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(
                    id = R.drawable.ic_bottom_sheet_pull_down
                ),
                contentDescription = "",
                modifier = Modifier
                    .padding(
                        top = dimensionResource(
                            id = R.dimen.general_padding_xsmall
                        )
                    )
                    .align(Alignment.CenterHorizontally)
            )
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
            ) {
                when (uiState) {
                    is BottomSheetImportContactLoadingViewModel.State.Loading -> {
                        loadingUI(Modifier.align(Alignment.Center), uiState.size)
                    }

                    is BottomSheetImportContactLoadingViewModel.State.Error -> {
                        errorUI(Modifier.align(Alignment.Center), uiState.error, onRetry, onCancel)
                    }

                    is BottomSheetImportContactLoadingViewModel.State.Success -> {
                        successUI(Modifier.align(Alignment.Center), uiState.bulkContactsResult, uiState.finished, onDone, onFinish)
                    }
                }
            }
        }
    }
}

@Composable
fun loadingUI(modifier: Modifier = Modifier, listSize: Int) {

    val loadingText = if (listSize == 1) {
        stringResource(R.string.import_contact_loading_bottom_sheet_message_single)
    } else {
        stringResource(R.string.import_contact_loading_bottom_sheet_message, listSize)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.import_contact_loading_image
            ),
            contentDescription = null,
            modifier = Modifier.padding(
                top = dimensionResource(
                    id = R.dimen.general_padding_xlarge
                )
            )
        )
        Text(
            text = loadingText,
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            modifier = Modifier.padding(
                top = dimensionResource(
                    id = R.dimen.general_padding_large
                ),
                bottom = dimensionResource(
                    id = R.dimen.general_padding_small
                )
            )
        )
        Box(
            modifier = Modifier.padding(
                top = 32.dp,
                start = 12.dp,
                end = 12.dp,
                bottom = 64.dp
            )
        ) {
            loadingDots()
        }
    }
}
@Composable
fun loadingDots(
    dotSize: Dp = 24.dp,
    delaySpeed: Int = 450
) {
    @Composable
    fun Dot(scale: Float) = Spacer(
        Modifier
            .size(dotSize)
            .scale(scale)
            .background(
                color = colorResource(id = R.color.connectGrey08),
                shape = CircleShape
            )
    )

    val infiniteTransition = rememberInfiniteTransition(label = "")

    @Composable
    fun animateScaleWithDelay(delay: Int) = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = delaySpeed * 4
                0f at delay with LinearEasing
                1f at delay + delaySpeed with LinearEasing
                0f at delay + delaySpeed * 2
            }
        ), label = ""
    )

    val scale1 by animateScaleWithDelay(0)
    val scale2 by animateScaleWithDelay(delaySpeed)
    val scale3 by animateScaleWithDelay(delaySpeed * 2)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val spaceSize = 2.dp

        Dot(scale1)
        Spacer(Modifier.width(spaceSize))
        Dot(scale2)
        Spacer(Modifier.width(spaceSize))
        Dot(scale3)
    }
}

@Composable
fun errorUI(
    modifier: Modifier = Modifier,
    errorMessage: String?,
    onRetry: (() -> Unit),
    onCancel: (() -> Unit)
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.error_exclamation_image),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 48.dp)
                .wrapContentSize()
        )
        Text(
            text = stringResource(id = R.string.import_contact_loading_bottom_sheet_error_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 32.dp, end = 32.dp),
            style = MaterialTheme.typography.h5,
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            textAlign = TextAlign.Center
        )
        val errorMsg = errorMessage ?: stringResource(id = R.string.import_contact_loading_bottom_sheet_error_message)
        Text(
            text = errorMsg,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp, top = 8.dp, end = 32.dp, bottom = 24.dp),
            style = MaterialTheme.typography.body2,
            color = colorResource(id = R.color.connectSecondaryDarkBlue),
            textAlign = TextAlign.Center
        )
        connectButton(
            modifier = Modifier.padding(bottom = 8.dp),
            textResId = R.string.general_try_again,
            onClick = onRetry
        )
        connectButton(
            modifier = Modifier.padding(bottom = 8.dp),
            textResId = R.string.general_cancel,
            isBlue = false,
            onClick = onCancel
        )
    }
}

@Composable
fun successUI(
    modifier: Modifier = Modifier,
    bulkContactsResult: BulkContactsResult,
    isFinished: Boolean,
    onClick: (() -> Unit),
    onFinish: (() -> Unit)
) {
    val vi = remember {  mutableStateListOf (isFinished, isFinished, isFinished, isFinished) }
    val timeInMilli = remember { mutableListOf<Long>(0,0,0,0) }

    val created = bulkContactsResult.result?.inserted.orZero()
    val failed = bulkContactsResult.result?.failed.orZero()
    val duplicated = bulkContactsResult.result?.skipped.orZero() + bulkContactsResult.result?.updated.orZero()
    val totalImported = created + duplicated
    val animationElapsedTime = 500L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_green_arrow_success),
            contentDescription = null,
            modifier = Modifier
                .padding(top = 48.dp)
                .wrapContentSize()
        )
        Text(
            text = stringResource(id = R.string.connect_import_local_contacts_bottom_sheet_import_complete),
            modifier = Modifier.padding(top = 32.dp, bottom = 32.dp),
            style = MaterialTheme.typography.h5,
            color = colorResource(id = R.color.connectSecondaryDarkBlue)
        )

        val horzPad = (LocalConfiguration.current.screenWidthDp * 0.3) / 2

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = horzPad.dp, end = horzPad.dp, bottom = 100.dp)
        ) {
            if (created > 0) {
                timeInMilli[0] = animationElapsedTime
                AnimatedImportResult(
                    visible = vi[0],
                    description = stringResource(
                        id = R.string.connect_import_local_contacts_bottom_sheet_import_created,
                        created
                    ),
                    iconRes = R.drawable.ic_success
                )
            }

            timeInMilli[1] = animationElapsedTime
            AnimatedImportResult(
                visible = vi[1],
                description = if (duplicated > 0) {
                    stringResource(
                        R.string.connect_import_local_contacts_bottom_sheet_import_duplicates,
                        duplicated
                    )
                } else {
                    stringResource(id = R.string.connect_import_local_contacts_bottom_sheet_import_no_duplicates)
                }
                ,
                iconRes = R.drawable.ic_success
            )

            if (failed > 0) {
                timeInMilli[2] = animationElapsedTime
                AnimatedImportResult(
                    visible = vi[2],
                    description = stringResource(
                        id = R.string.connect_import_local_contacts_bottom_sheet_import_failed,
                        failed
                    ),
                    iconRes = R.drawable.ic_failed_exclamation
                )
            }

            timeInMilli[3] = animationElapsedTime
            AnimatedImportResult(
                visible = vi[3],
                description = if (totalImported > 0) {
                    stringResource(
                        R.string.connect_import_local_contacts_bottom_sheet_import_total,
                        totalImported
                    )
                } else {
                    stringResource(R.string.connect_import_local_contacts_bottom_sheet_import_no_imports)
                },
                iconRes = if (totalImported > 0) R.drawable.ic_success else R.drawable.ic_failed_exclamation
            )
        }
        connectButton(
            modifier = Modifier.padding(bottom = 8.dp),
            textResId = R.string.connect_import_local_contacts_done,
            onClick = onClick
        )
    }

    LaunchedEffect(key1 = Unit) {
        timeInMilli.forEachIndexed() { index, timeInMillis ->
            if (timeInMillis > 0) {
                if (!isFinished) delay(timeInMillis)
                vi[index] = true
            }
        }
        onFinish()
    }
}

@Composable
fun AnimatedImportResult(
    visible: Boolean,
    description: String,
    iconRes: Int
) {
    AnimatedVisibility(
        visible = visible,
        enter = expandHorizontally { 0 } + fadeIn(initialAlpha = 0.3f, animationSpec = tween(durationMillis = 650))
    ) {
        ImportResult(description = description, iconRes = iconRes)
    }

    if(!visible) {
        Box(modifier = Modifier.alpha(0f)) {
            ImportResult(description = description, iconRes = iconRes)
        }
    }
}

@Composable
fun ImportResult(
    description: String,
    iconRes: Int
) {
    val painter = painterResource(id = iconRes )
    Row(
        modifier = Modifier.padding(top = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.wrapContentSize()
        )
        Text(
            text = description,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(start = 8.dp),
            color = colorResource(id = R.color.connectSecondaryDarkBlue)
        )
    }
}

@Composable
fun connectButton(
    modifier: Modifier = Modifier,
    textResId: Int,
    isBlue: Boolean = true,
    onClick: (() -> Unit)
) {
    val backgroundColor = if(isBlue)  R.color.connectPrimaryBlue else R.color.connectWhite
    val textColor = if(isBlue)  R.color.connectWhite else R.color.connectPrimaryBlue

    Button(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = colorResource(id = backgroundColor)
        )
    ) {
        Text(
            text = stringResource(id = textResId),
            style = MaterialTheme.typography.button,
            textAlign = TextAlign.Center,
            color = colorResource(id = textColor),
        )
    }
}
package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolidV6
import com.nextiva.nextivaapp.android.features.ui.theme.FontLato
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyOverlineHeavy
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactSelectionViewState
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectTextButtonViewState

@Composable
fun ConnectContactSelectionView(
    viewState: ConnectContactSelectionViewState,
    lazyPagingList: LazyPagingItems<ConnectContactListItemViewState>
) {
    Column {
        viewState.connectHeaderViewState?.let { ConnectHeaderView(viewState = it) }
        Column(
            Modifier
                .background(color = colorResource(id = R.color.connectGrey01))
                .padding(
                    start = dimensionResource(id = R.dimen.general_view_xmedium),
                    end = dimensionResource(id = R.dimen.general_view_xmedium)
                )
                .fillMaxWidth()
        ) {
            HeaderInformationView(viewState)

            NextivaTextFieldComponent(
                onValueChanged = { text ->
                    viewState.textFieldChangedListener?.invoke(text)
                },
                isDigitOnly = viewState.isDigitOnly?: false,
                prefillText = viewState.prefillTextFieldValue,
                onTrailerIconClick = viewState.onTrailingIconClick,
                trailingIcon = viewState.trailingIcon,
                shouldClearCurrentSearch = viewState.shouldClearCurrentSearch
            )
        }

        if (viewState.shouldShowRecentContactsSection == true) {
            if (lazyPagingList.itemCount == 0) {

                ConnectNoResultsFoundView(modifier = Modifier.weight(1f))

            } else {
                RecentContactsView(
                    modifier = Modifier
                        .weight(1f),
                    lazyPagingList,
                    viewState.shouldShowSearchStyleView ?: false
                )
            }
        } else {
            showEmptyView(modifier = Modifier.weight(1f))
        }

        viewState.cancelTxtBtnViewState?.let { BottomView(modifier = Modifier, it) }
    }
}

@Composable
fun showEmptyView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.connectWhite)),
    ) {}
}

@Composable
fun NextivaTextFieldComponent(
    modifier: Modifier = Modifier,
    trailingIcon: Int?,
    isDigitOnly: Boolean = false,
    prefillText: String? = null,
    onValueChanged: (String) -> Unit,
    shouldClearCurrentSearch: Boolean? = false,
    onTrailerIconClick: (() -> Unit)? = null,
) {
    NextivaTextFieldView(
        modifier = modifier
            .padding(
                top = dimensionResource(id = R.dimen.general_view_medium),
                bottom = dimensionResource(id = R.dimen.general_view_medium)
            )
            .background(color = colorResource(id = R.color.connectWhite)),
        title = "",
        prefillText = prefillText,
        hint = stringResource(id = R.string.connect_contact_selection_search_hint),
        isDigitsOnly = isDigitOnly,
        trailingIcon = trailingIcon,
        shouldClearSearch = shouldClearCurrentSearch,
        onTrailerIconClick = onTrailerIconClick,
        onValueChanged = { text ->
            onValueChanged(text)
        },
        hintTextStyle = TypographyBody2,
        textStyle = TextStyle(
            fontFamily = FontLato,
            fontWeight = FontWeight.Normal,
            fontSize = dimensionResource(id = R.dimen.material_text_headline).value.sp,
            textDecoration = null
        ),
        textColor = R.color.connectPrimaryBlue
    )
}

@Composable
fun BottomView(modifier: Modifier = Modifier, viewState: ConnectTextButtonViewState) {
    Column(
        modifier = modifier
    ) {
        ConnectTextButton(viewState = viewState)
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.general_view_xlarge)))
    }
}

@Composable
fun RecentContactsView(
    modifier: Modifier,
    lazyPagingList: LazyPagingItems<ConnectContactListItemViewState>,
    shouldShowSearchStyleListView: Boolean
) {
    Column(
        modifier
            .background(color = colorResource(id = R.color.connectWhite))
            .nestedScroll(rememberNestedScrollInteropConnection())
            .padding(
                start = dimensionResource(id = R.dimen.general_view_medium),
                end = dimensionResource(id = R.dimen.general_view_xmedium),
                top = dimensionResource(id = R.dimen.general_view_xmedium),
                bottom = dimensionResource(id = R.dimen.general_view_xmedium)
            )
    ) {
        Text(
            modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.general_view_small)),
            text = stringResource(id = R.string.connect_contacts_recent_contacts).uppercase(),
            style = TypographyOverlineHeavy,
            color = colorResource(id = R.color.connectGrey10),
        )
        Surface(
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(color = colorResource(id = R.color.connectWhite))
                    .fillMaxWidth()
                    .nestedScroll(rememberNestedScrollInteropConnection()),
                verticalArrangement = Arrangement.spacedBy(
                    dimensionResource(id = R.dimen.general_view_small)
                )
            ) {
                items(
                    count = lazyPagingList.itemCount,
                    key = { index ->
                        val item = lazyPagingList[index]
                        item?.id ?: index
                    }
                ) { item ->
                    val listItem = lazyPagingList[item]
                    listItem?.let {
                        if (shouldShowSearchStyleListView) {
                            ConnectContactSearchListItemView(listItem)
                        } else {
                            ConnectContactListItemView(
                                modifier = modifier.clickable(
                                    onClick = {
                                        it.onItemClick?.invoke(
                                            listItem.contactPhoneNumbers?.firstOrNull()?.strippedNumber
                                                ?: ""
                                        )
                                    }
                                ),
                                viewState = listItem
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderInformationView(viewState: ConnectContactSelectionViewState) {
    Row(
        modifier = Modifier.height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.Start
    ) {
        viewState.selectionInfoIcon?.let { icon ->
            Column {
                Box(
                    modifier = Modifier
                        .size(
                            height = dimensionResource(id = R.dimen.general_view_xlarge),
                            width = dimensionResource(id = R.dimen.general_view_xlarge)
                        )
                        .background(
                            color = colorResource(id = R.color.connectSecondaryLightBlue),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier = Modifier,
                        text = stringResource(id = icon),
                        fontSize = dimensionResource(R.dimen.material_text_body1).value.sp,
                        fontFamily = FontAwesomeSolidV6,
                        color = colorResource(id = R.color.connectGrey10)
                    )
                }
            }
        }
        Column(
            Modifier
                .padding(start = dimensionResource(id = R.dimen.general_view_xsmall))
                .fillMaxHeight(),
        ) {
            Text(
                modifier = Modifier,
                style = TypographyBody1Heavy,
                color = colorResource(id = R.color.connectSecondaryDarkBlue),
                text = viewState.title ?: "",
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                style = TypographyBody2,
                color = colorResource(id = R.color.connectGrey10),
                text = viewState.subTitle ?: "",
            )
        }
    }
}

@Preview
@Composable
fun ConnectContactSelectionViewPreview() {
}


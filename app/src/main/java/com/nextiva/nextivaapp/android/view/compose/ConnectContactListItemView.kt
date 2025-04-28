package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.models.AvatarInfo
import com.nextiva.nextivaapp.android.view.AvatarView
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState

@Composable
fun ConnectContactListItemView(
    modifier: Modifier = Modifier,
    viewState: ConnectContactListItemViewState
) {
    Row(
        modifier = modifier
    ) {
        viewState.avatarInfo?.let {
            ComposeAvatarView(avatarInfo = it)
        }
        ContactInfoView(viewState)
    }
}

@Composable
fun ContactInfoView(viewState: ConnectContactListItemViewState) {
    Column(
        modifier = Modifier.padding(start = dimensionResource(id = R.dimen.general_view_xsmall)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.general_padding_xsmall))
    ) {
        Text(
            text = "${viewState.contactName ?: ""} ${if (viewState.hasLeftNWay) "(left)" else ""}",
            style = TypographyBody2Heavy.withTextColor(color = colorResource(id = if (viewState.hasLeftNWay) R.color.connectGrey09 else R.color.connectSecondaryDarkBlue)),
        )

        val phoneNumber = viewState.contactPhoneNumbers?.firstOrNull{!it.strippedNumber.isNullOrEmpty()}

        if (phoneNumber != null && !phoneNumber.strippedNumber.isNullOrEmpty()) {
            Row() {
                Text(
                    text = phoneNumber.strippedNumber!!,
                    style = TypographyCaption1.withTextColor(color = colorResource(id = if (viewState.hasLeftNWay) R.color.connectGrey09 else R.color.connectGrey10)),
                    modifier = Modifier.padding(
                        end = dimensionResource(id = R.dimen.general_view_xxsmall),
                        top = dimensionResource(id = R.dimen.general_view_xxsmall),
                        bottom = dimensionResource(id = R.dimen.general_view_xxsmall)
                    )
                )

                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_small)))

                if (phoneNumber.label?.isNotEmpty() == true) {
                    val labelColors =
                        getColorByPhoneType(viewState.contactPhoneNumbers[0].type ?: 0)
                    Surface(
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                        elevation = 1.dp,
                        color = colorResource(
                            id = labelColors.first
                        )
                    ) {
                        Text(
                            text = phoneNumber.label.toString().replaceFirstChar { it.uppercase() },
                            style = TypographyCaption2.withTextColor(color = colorResource(id = labelColors.second)),
                            modifier = Modifier.padding(
                                start = dimensionResource(id = R.dimen.general_view_xxsmall),
                                end = dimensionResource(id = R.dimen.general_view_xxsmall),
                                top = dimensionResource(id = R.dimen.general_view_xxsmall),
                                bottom = dimensionResource(id = R.dimen.general_view_xxsmall)
                            ),
                        )
                    }
                }
            }
        }
    }
}

fun getColorByPhoneType(type: Int) = when (type) {
    Enums.Contacts.PhoneTypes.MOBILE_PHONE -> Pair(
        R.color.phoneTypeMobile,
        R.color.phoneTypeMobileLabelTxt
    )

    Enums.Contacts.PhoneTypes.WORK_PHONE -> Pair(
        R.color.phoneTypeWork,
        R.color.phoneTypeWorkLabelTxt
    )

    Enums.Contacts.PhoneTypes.HOME_PHONE -> Pair(
        R.color.phoneTypeHome,
        R.color.phoneTypeHomeLabelTxt
    )

    else -> Pair(R.color.phoneTypeOther, R.color.phoneTypeOtherLabelTxt)
}

@Composable
fun ComposeAvatarView(modifier: Modifier = Modifier, avatarInfo: AvatarInfo) {
    val context = LocalContext.current
    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier
                .width(dimensionResource(id = R.dimen.general_view_xlarge))
                .height(dimensionResource(id = R.dimen.general_view_xlarge)),
            factory = {
                AvatarView(context).apply {
                    setAvatar(avatarInfo, true)
                }
            },
            update = {
                it.setAvatar(avatarInfo, true)
            }
        )
    }
}

@Preview
@Composable
fun ConnectContactListItemViewPreview() {
    val contactPhoneNumbers = arrayListOf<PhoneNumber>(
        PhoneNumber(Enums.Contacts.PhoneTypes.PHONE, "555-555-5555")
    )
    ConnectContactSearchListItemView(
        ConnectContactListItemViewState(
            contactName = "Anjan N",
            contactPhoneNumbers = contactPhoneNumbers
        )
    )
}


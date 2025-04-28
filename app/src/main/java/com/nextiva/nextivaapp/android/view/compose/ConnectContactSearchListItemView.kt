package com.nextiva.nextivaapp.android.view.compose
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.constants.Enums
import com.nextiva.nextivaapp.android.db.model.PhoneNumber
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody2Heavy
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyCaption2
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.view.compose.viewstate.ConnectContactListItemViewState
import java.util.regex.Pattern
@Composable
fun ConnectContactSearchListItemView(viewState: ConnectContactListItemViewState) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            viewState.avatarInfo?.let { ComposeAvatarView(avatarInfo = it) }

            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_xsmall)))

            Text(
                modifier = Modifier.padding(start = dimensionResource(id = R.dimen.general_view_xxsmall)),
                text = getText(viewState.contactName ?: "", viewState.searchTerm),
                style = TypographyBody2Heavy.withTextColor(color = colorResource(id = R.color.connectSecondaryDarkBlue)),
            )
        }
        Column(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.general_view_large))) {
            viewState.contactPhoneNumbers?.forEach { phoneNumber ->
                if (!phoneNumber.strippedNumber.isNullOrEmpty()) {
                    PhoneNumberView(
                        modifier = Modifier.clickable(
                            onClick = {
                                viewState.onItemClick?.invoke(phoneNumber.strippedNumber.toString())
                            }
                        ), phoneNumber, viewState.searchTerm
                    )
                }
            }
        }
    }
}
@Composable
fun getText(input: String, searchTerm: String?): AnnotatedString {
    if (searchTerm.isNullOrEmpty()) return AnnotatedString(input)
    val context = LocalContext.current
    val highlightedColor = ContextCompat.getColor(context, R.color.connectSecondaryYellow)
    val builder = AnnotatedString.Builder(input)
    val pattern = Regex(Pattern.quote(searchTerm), RegexOption.IGNORE_CASE)
    val matches = pattern.findAll(input)
    matches.forEach { match ->
        builder.addStyle(
            style = SpanStyle(background = Color(highlightedColor)),
            start = match.range.first,
            end = match.range.last + 1
        )
    }
    return builder.toAnnotatedString()
}
@Composable
fun PhoneNumberView(modifier: Modifier = Modifier, phoneNumber: PhoneNumber, searchTerm: String?) {
    Row(
        modifier = modifier
            .padding(
                top = dimensionResource(
                    id = R.dimen.general_view_xxsmall
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = getText(input = phoneNumber.strippedNumber ?: "", searchTerm = searchTerm),
            style = TypographyBody1.withTextColor(color = colorResource(id = R.color.connectSecondaryDarkBlue)),
        )

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.general_padding_small)))

        if (phoneNumber.label?.isNotEmpty() == true) {
            val phoneTypeColors = getColorByPhoneType(phoneNumber.type)
            Surface(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.general_view_xsmall)),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                color = colorResource(
                    id = phoneTypeColors.first
                )
            ) {
                Text(
                    text = phoneNumber.label.toString().replaceFirstChar { it.uppercase() },
                    style = TypographyCaption2.withTextColor(color = colorResource(id = phoneTypeColors.second)),
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
@Preview
@Composable
private fun ConnectContactSearchListItemViewPreview() {
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


package com.nextiva.nextivaapp.android.view.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R
import com.nextiva.nextivaapp.android.features.ui.theme.FontAwesomeSolidV6
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyBody1
import com.nextiva.nextivaapp.android.features.ui.theme.TypographyOverlineHeavy
import com.nextiva.nextivaapp.android.features.ui.theme.withTextColor
import com.nextiva.nextivaapp.android.util.extensions.contentDescription

@Composable
fun NextivaTextFieldView(
    modifier: Modifier = Modifier,
    title: String,
    hint: String,
    prefillText: String? = "",
    shouldClearSearch: Boolean? = false,
    isDigitsOnly: Boolean = false,
    onValueChanged: (String) -> Unit,
    hintTextStyle: TextStyle = TypographyBody1,
    textStyle: TextStyle = TypographyBody1,
    textColor: Int = R.color.connectSecondaryDarkBlue,
    onTrailerIconClick: (() -> Unit)? = null,
    trailingIcon: Int? = null
) {
    val pattern = remember { Regex("^\\d+\$") }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier) {
        if (title.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .contentDescription(title.uppercase())
                    .padding(bottom = dimensionResource(R.dimen.general_padding_xsmall)),
                text = title.uppercase(),
                style = TypographyOverlineHeavy,
                color = colorResource(id = R.color.connectGrey10)
            )
        }

        var text by remember { mutableStateOf(prefillText ?: "") }

        BasicTextField(modifier = Modifier
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .contentDescription(
                stringResource(
                    id = R.string.create_schedule_specified_custom_dates_occurrence_accessibility_id,
                    text
                )
            )
            .fillMaxWidth(),
            textStyle = if (text.isEmpty()) TypographyBody1 else textStyle.withTextColor(
                colorResource(textColor)
            ),
            value = if(shouldClearSearch == true) "" else text,
            singleLine = true,
            maxLines = 1,
            onValueChange = {
                if (isDigitsOnly) {
                    if (it.isEmpty() || it.matches(pattern)) {
                        text = it
                    }
                } else {
                    text = it
                }

                onValueChanged(text)
            },
            keyboardOptions = if (isDigitsOnly) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions(
                keyboardType = KeyboardType.Text
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .heightIn(max = dimensionResource(id = R.dimen.general_view_xxlarge))
                        .contentDescription(
                            stringResource(
                                id = R.string.create_schedule_specified_custom_dates_occurrence_accessibility_id,
                                text
                            )
                        )
                        .border(
                            width = dimensionResource(R.dimen.hairline_small),
                            color = if (isFocused) colorResource(id = R.color.connectPrimaryBlue) else colorResource(
                                id = R.color.connectPrimaryGrey
                            ),
                            shape = RoundedCornerShape(size = dimensionResource(R.dimen.general_padding_xsmall))
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(
                                start = dimensionResource(id = R.dimen.general_view_xsmall),
                                end = dimensionResource(id = R.dimen.general_view_xsmall)
                            )
                            .fillMaxSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            InnerTextFieldWithHint(hint, text, innerTextField, hintTextStyle)
                        }

                        Column(
                            modifier = Modifier.fillMaxHeight(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            TrailingIconView(
                                trailingIcon,
                                onTrailerIconClick,
                                text
                            )
                        }
                    }
                }
            })
    }
}

@Composable
fun InnerTextFieldWithHint(
    hint: String,
    text: String,
    innerTextField: @Composable () -> Unit,
    hintTextStyle: TextStyle
) {
    Box() {
        if (text.isEmpty()) {
            Text(
                text = hint,
                style = hintTextStyle,
                color = colorResource(id = R.color.connectGrey08),
            )
        }
        innerTextField()
    }
}

@Composable
fun TrailingIconView(trailingIcon: Int?, onTrailerIconClick: (() -> Unit)?, text: String) {
    if (trailingIcon != null && text.length >=3) {
        Box(
            modifier = Modifier
                .clickable(onClick = {
                    onTrailerIconClick?.invoke()
                })
                .background(
                    color = colorResource(id = R.color.connectPrimaryBlue),
                    shape = CircleShape
                )
        ) {
            Text(
                modifier = Modifier.padding(dimensionResource(id = R.dimen.general_view_xsmall)),
                text = stringResource(trailingIcon),
                textAlign = TextAlign.Center,
                fontSize = dimensionResource(R.dimen.material_text_caption).value.sp,
                fontWeight = FontWeight.W900,
                color = colorResource(id = R.color.connectWhite),
                fontFamily = FontAwesomeSolidV6
            )
        }
    }
}

@Composable
fun NextivaTextFieldView(
    columnModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    title: String,
    hint: String,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    Column(columnModifier) {
        Text(
            modifier = titleModifier.padding(bottom = dimensionResource(R.dimen.general_padding_xsmall)),
            text = title.uppercase(),
            style = TypographyOverlineHeavy,
            color = colorResource(id = R.color.connectGrey10)
        )

        BasicTextField(modifier = textFieldModifier
            .contentDescription(
                stringResource(
                    id = R.string.create_schedule_specified_custom_dates_occurrence_accessibility_id,
                    value
                )
            )
            .fillMaxWidth(),
            textStyle = TypographyBody1.withTextColor(colorResource(id = R.color.connectSecondaryDarkBlue)),
            value = value,
            onValueChange = onValueChanged,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .border(
                            width = dimensionResource(R.dimen.hairline_small),
                            color = colorResource(id = R.color.connectPrimaryGrey),
                            shape = RoundedCornerShape(size = dimensionResource(R.dimen.general_padding_xsmall))
                        )
                        .padding(dimensionResource(R.dimen.general_padding_xmedium)),
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = hint,
                            style = TypographyBody1,
                            color = colorResource(id = R.color.connectGrey08)
                        )
                    }

                    innerTextField()
                }
            })
    }
}

@Composable
fun NextivaOutlinedTextFieldView(
    columnModifier: Modifier = Modifier,
    titleModifier: Modifier = Modifier,
    textFieldModifier: Modifier = Modifier,
    title: String,
    hint: String,
    keyboardActions: KeyboardActions,
    keyboardOptions: KeyboardOptions,
    value: String,
    onValueChanged: (String) -> Unit,
) {
    Column(
        modifier = columnModifier
            .padding(start = dimensionResource(R.dimen.general_padding_small))


    ) {
        Text(
            modifier = titleModifier.padding(bottom = dimensionResource(R.dimen.general_padding_xsmall)),
            text = title.uppercase(),
            style = TypographyOverlineHeavy,
            color = colorResource(id = R.color.connectGrey10)
        )
        OutlinedTextField(
            modifier = textFieldModifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.zero_out)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(id = R.color.connectWhite),
                cursorColor = colorResource(R.color.connectSecondaryDarkBlue),
                placeholderColor = colorResource(R.color.connectGrey08),
                textColor = colorResource(R.color.connectSecondaryDarkBlue)
            ),
            value = value,
            onValueChange = onValueChanged,
            keyboardActions = keyboardActions,
            keyboardOptions = keyboardOptions,
            placeholder = { Text(hint) })

    }
}

@Preview
@Composable
fun NextivaTextFieldViewPreview() {
    NextivaTextFieldView(title = "", hint = "Enter a number", onValueChanged = {

    }, trailingIcon = R.string.fa_phone_icon)
}

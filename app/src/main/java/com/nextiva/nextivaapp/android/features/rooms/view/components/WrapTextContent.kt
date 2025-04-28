package com.nextiva.nextivaapp.android.features.rooms.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nextiva.nextivaapp.android.features.ui.theme.Typography
import kotlin.math.ceil

// Workaround for when Jetpack Compose does not tightly bound multi-line text
@Composable
fun WrapTextContent(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
) {
    SubcomposeLayout(modifier) { constraints ->
        val composable = @Composable { localOnTextLayout: (TextLayoutResult) -> Unit ->
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                onTextLayout = localOnTextLayout,
                style = style
            )
        }
        var textWidthOpt: Int? = null
        subcompose("measureView") {
            composable { layoutResult ->
                textWidthOpt = (0 until layoutResult.lineCount)
                    .maxOf { line ->
                        ceil(layoutResult.getLineRight(line) - layoutResult.getLineLeft(line)).toInt()
                    }
            }
        }[0].measure(constraints)
        val textWidth = textWidthOpt ?: 0
        val placeable = subcompose("content") {
            composable(onTextLayout)
        }[0].measure(constraints.copy(minWidth = textWidth, maxWidth = textWidth))

        layout(width = textWidth, height = placeable.height) {
            try {
                placeable.place(0, 0)
            } catch (e: java.lang.IllegalStateException) {
                // Not sure why this happens occasionally.  Logging to collect more data.
                FirebaseCrashlytics.getInstance().recordException(e)
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun WrapTextContentDefaultPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        WrapTextContent(
            text = buildAnnotatedString { append("Lorem ipsum dolor sit amet, consectetr adipiscing elit, consectetr adipiscing elit.") },
            color = Color.White,
            style = Typography.body2,
            modifier = Modifier
                .padding(20.dp)
                .border(width = 2.dp, color = Color.Red)
                .background(Color.DarkGray)
        )

        // Non-wrapped Text for comparison with the Wrapped
        Text(
            text = "Lorem ipsum dolor sit amet, consectetr adipiscing elit, consectetr adipiscing elit.",
            color = Color.White,
            style = Typography.body2,
            modifier = Modifier
                .padding(20.dp)
                .border(width = 2.dp, color = Color.Red)
                .background(Color.DarkGray)
        )
    }
}
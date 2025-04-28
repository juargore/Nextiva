package com.nextiva.nextivaapp.android.features.ui

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.nextiva.nextivaapp.android.features.rooms.view.components.WrapTextContent
import java.util.regex.Pattern

@Composable
fun LinkifyText(text: String,
                color: Color = Color.Unspecified,
                linkColor: Color = Color.Blue,
                style: TextStyle = LocalTextStyle.current,
                modifier: Modifier = Modifier
) {
    val longPressTimeout = LocalViewConfiguration.current.longPressTimeoutMillis
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember {
        mutableStateOf<TextLayoutResult?>(null)
    }
    val linksList = extractUrls(text)
    val annotatedString = buildAnnotatedString {
        append(text)
        linksList.forEach {
            addStyle(
                style = SpanStyle(
                    color = linkColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = "URL",
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }
    WrapTextContent(
        text = annotatedString,
        color = color,
        style = style,
        modifier = modifier.pointerInput(Unit) {
            if (linksList.isNotEmpty()) {
                awaitEachGesture {
                    val down: PointerInputChange = awaitFirstDown()
                    val startAt = System.currentTimeMillis()
                    waitForUpOrCancellation()?.let {
                        if (System.currentTimeMillis() - startAt < longPressTimeout) {
                            val offsetPosition = down.position
                            layoutResult.value?.let {
                                val position = it.getOffsetForPosition(offsetPosition)
                                annotatedString.getStringAnnotations(position, position)
                                    .firstOrNull()
                                    ?.let { result ->
                                        if (result.tag == "URL") {
                                            uriHandler.openUri(result.item)
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        },
        onTextLayout = { layoutResult.value = it },
    )
}

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
            + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

private val phonePattern: Pattern = Pattern.compile(
    """(\+?[0-9]{1,3}? ?-?\(?[0-9]{1,3}\)? ?-?[0-9]{3,5} ?-?[0-9]{4}( ?-?[0-9]{3})?)""",
    Pattern.CASE_INSENSITIVE
)

fun extractUrls(text: String): List<LinkInfos> {
    var matcher = urlPattern.matcher(text)
    var matchStart: Int
    var matchEnd: Int
    val links = arrayListOf<LinkInfos>()

    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()

        var url = text.substring(matchStart, matchEnd)
        if (!url.startsWith("http://") && !url.startsWith("https://"))
            url = "https://$url"

        links.add(LinkInfos(url, matchStart, matchEnd))
    }

    matcher = phonePattern.matcher(text)

    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()

        var phone = text.substring(matchStart, matchEnd)
        phone = "tel://$phone"

        links.add(LinkInfos(phone, matchStart, matchEnd))
    }

    return links
}

data class LinkInfos(
    val url: String,
    val start: Int,
    val end: Int
)

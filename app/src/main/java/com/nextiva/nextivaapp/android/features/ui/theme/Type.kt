package com.nextiva.nextivaapp.android.features.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R

val FontAwesome = FontFamily(
    Font(R.font.fa_regular_400, FontWeight.Normal),
    Font(R.font.fa_solid_900, FontWeight.Bold),
)
val FontAwesomeSolid = FontFamily(
    Font(R.font.fa_solid_900, FontWeight.Bold)
)

val FontAwesomeV6 = FontFamily(
    Font(R.font.fa_v6_regular_400, FontWeight.Normal),
    Font(R.font.fa_v6_solid_900, FontWeight.Bold)
)

val FontAwesomeSolidV6 = FontFamily(
    Font(R.font.fa_v6_solid_900, FontWeight.Bold)
)

val FontAwesomeDuoToneV6 = FontFamily(
    Font(R.font.fa_v6_duotone_900, FontWeight.Normal)
)


val FontLato = FontFamily(
    Font(R.font.lato_black, FontWeight.Black),
    Font(R.font.lato_black_italic, FontWeight.Black, FontStyle.Italic),
    Font(R.font.lato_bold, FontWeight.Bold),
    Font(R.font.lato_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.lato_heavy, FontWeight.ExtraBold),
    Font(R.font.lato_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.lato_light, FontWeight.Light),
    Font(R.font.lato_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.lato_regular, FontWeight.Normal),
    Font(R.font.lato_thin, FontWeight.Thin),
    Font(R.font.lato_thin_italic, FontWeight.Thin, FontStyle.Italic),
)

// Set of Material typography styles to start with
val Typography = Typography(
    body1 = TextStyle(
        fontFamily = FontLato,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    body2 = TextStyle(
        fontFamily = FontLato,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp
    ),

    /* Other default text styles to override
    button = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp
    ),
    caption = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
    */
)

val TypographyH6 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 18.sp
)

val TypographyBody1 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W400,
    fontSize = 16.sp
)

val TypographyBody1Heavy = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 16.sp
)

val TypographyBody2 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W400,
    fontSize = 14.sp
)

val TypographyBody2Span = SpanStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W400,
    fontSize = 14.sp
)

val TypographyBody2Heavy = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 14.sp,
)

val TypographyBody2HeavySpan = SpanStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 14.sp)

val TypographyCaption1 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W400,
    fontSize = 12.sp
)

val TypographyCaption2 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W400,
    fontSize = 10.sp
)

val TypographyCaption1Heavy = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 12.sp
)

val TypographyCaption2Bold = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 10.sp
)

val TypographySubtitle1 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W500,
    fontSize = 18.sp
)

val TypographyH5 = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 24.sp
)

val TypographyButton = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.ExtraBold,
    fontSize = 14.sp
)

val TypographyForm = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 10.sp
)

val TypographyOverlineHeavy = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 10.sp
)

val TypographyLargeHeavy = TextStyle(
    fontFamily = FontLato,
    fontWeight = FontWeight.W800,
    fontSize = 24.sp

)

fun TextStyle.withBackgroundColor(color: Color): TextStyle {
    return TextStyle(
        fontFamily = this.fontFamily,
        fontWeight = this.fontWeight,
        fontSize = this.fontSize,
        background = color
    )
}

fun TextStyle.withTextColor(color: Color): TextStyle {
    return TextStyle(
        fontFamily = this.fontFamily,
        fontWeight = this.fontWeight,
        fontSize = this.fontSize,
        color = color
    )
}

fun SpanStyle.withTextColor(color: Color): SpanStyle {
    return SpanStyle(
        fontFamily = this.fontFamily,
        fontWeight = this.fontWeight,
        fontSize = this.fontSize,
        color = color
    )
}
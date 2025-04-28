package com.nextiva.nextivaapp.android.fragments.bottomsheets.importwizard.ui

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.nextiva.nextivaapp.android.R

class NextivaTheme {

    val latoFont = FontFamily(
        Font(R.font.lato_regular, FontWeight.Normal),
        Font(R.font.lato_bold, FontWeight.Bold),
        Font(R.font.lato_heavy, FontWeight.ExtraBold),
    )

    val typography = Typography(
        body2 = TextStyle(
            fontFamily = latoFont,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),
        h5 = TextStyle(
            fontFamily = latoFont,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp
        ),
        button = TextStyle(
            fontFamily = latoFont,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 14.sp
        )
    )
}

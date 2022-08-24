package com.idanrayan.instantmessagesusingnearby.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.idanrayan.instantmessagesusingnearby.R

private val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_regular, FontWeight.Normal),
)

val Typography = Typography(
    defaultFontFamily = PoppinsFontFamily,
    body1 = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    h2 = TextStyle(
        fontWeight = FontWeight.Light,
        fontSize = 19.sp
    ),
    h1 = TextStyle(
        fontSize = 32.sp,
        fontWeight = FontWeight.Light,
    ),
    button = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
    ),
    overline = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Normal,
    ),
)
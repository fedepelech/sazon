package com.desarrolloaplicaciones.sazon.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = SazonPrimary,
    onPrimary = Color.White,
    secondary = SazonGreen,
    onSecondary = Color.White,
    background = SazonBackground,
    onBackground = Color.White,
    surface = SazonBackground,
    onSurface = Color.White,
    error = SazonError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = SazonPrimary,
    onPrimary = Color.White,
    secondary = SazonGreen,
    onSecondary = Color.White,
    background = SazonBackground,
    onBackground = SazonRed,
    surface = SazonBackground,
    onSurface = Color.Black,
    error = SazonError,
    onError = Color.White
)

@Composable
fun SazonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(
            displayLarge = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp
            ),
            displayMedium = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 45.sp
            ),
            displaySmall = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp
            ),
            headlineLarge = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp
            ),
            bodyLarge = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp
            ),
            bodyMedium = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
            ),
            bodySmall = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            ),
            titleLarge = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            ),
            titleMedium = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            titleSmall = TextStyle(
                fontFamily = Roboto,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        ),
        content = content
    )
}
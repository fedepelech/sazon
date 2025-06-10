package com.desarrolloaplicaciones.sazon.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.desarrolloaplicaciones.sazon.R

val provider = GoogleFont.Provider(providerAuthority = "com.google.android.gms.fonts", providerPackage = "com.google.android.gms", certificates = R.array.com_google_android_gms_fonts_certs)
val robotoFont = GoogleFont("Roboto")
val Roboto = FontFamily(
    Font(googleFont = robotoFont, fontProvider = provider)
)

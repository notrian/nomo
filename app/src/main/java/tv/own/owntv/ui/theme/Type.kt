package tv.own.owntv.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Typography
import tv.own.owntv.R

/**
 * Fixed brand display face for hero/page/detail titles and the sidebar wordmark — used
 * regardless of the user's chosen main font, the same way the feather-gradient brand mark is a
 * fixed accent rather than a themeable one. Not exposed via [FontCustomization]; a user who wants
 * Anton everywhere can still pick [AppFontFamily.ANTON] as their main/popup family separately.
 */
val BrandDisplayFontFamily = FontFamily(Font(R.font.anton_regular))

/**
 * Fixed brand meta/mono face for channel numbers, timestamps, and LIVE/rating/download badges —
 * same rationale as [BrandDisplayFontFamily].
 */
val BrandMetaFontFamily = FontFamily(
    Font(R.font.ibm_plex_mono_medium, FontWeight.Medium),
    Font(R.font.ibm_plex_mono_semibold, FontWeight.SemiBold),
)

/**
 * Typography tuned for the 10-foot TV experience: larger sizes and generous weights so text
 * stays legible from across the room. The selected family is supplied by [OwnTVTheme].
 */
fun ownTVTypography(fontFamily: FontFamily = FontFamily.SansSerif): Typography {
    // Preserve TV Material's established metrics for slots OwnTV has not size-tuned, but apply the
    // selected family to every slot. Category rails and Live rows use titleSmall/bodySmall.
    val defaults = Typography()
    return Typography(
        displayLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            lineHeight = 48.sp,
        ),
        displayMedium = defaults.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = defaults.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
        ),
        headlineMedium = defaults.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = defaults.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 18.sp,
            lineHeight = 24.sp,
        ),
        titleSmall = defaults.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 22.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
            lineHeight = 20.sp,
        ),
        bodySmall = defaults.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            lineHeight = 18.sp,
        ),
        labelMedium = TextStyle(
            fontFamily = fontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            lineHeight = 16.sp,
        ),
        labelSmall = defaults.labelSmall.copy(fontFamily = fontFamily),
    )
}

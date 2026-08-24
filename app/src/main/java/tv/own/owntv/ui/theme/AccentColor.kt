package tv.own.owntv.ui.theme

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import tv.own.owntv.R

/**
 * Material You-style accent presets. OwnTV can't rely on true wallpaper-based dynamic color (a phone
 * feature that isn't dependable on Android TV), so instead the user picks an accent and the M3 color
 * scheme is seeded from it. Each preset carries its tonal `primary` / `primaryContainer` roles for
 * both dark and light themes (M3 uses lighter tones on dark surfaces, darker tones on light).
 *
 * Neutrals (background, surface containers, text, outline) are theme-only and live in [OwnTVColors].
 */
enum class AccentColor(
    @param:StringRes val labelRes: Int,
    private val primaryDark: Color,
    private val onPrimaryDark: Color,
    private val primaryContainerDark: Color,
    private val onPrimaryContainerDark: Color,
    private val primaryLight: Color,
    private val onPrimaryLight: Color,
    private val primaryContainerLight: Color,
    private val onPrimaryContainerLight: Color,
) {
    // Retuned toward the "feather" brand palette (blue/violet/teal) — see Color.kt's
    // FeatherBlue/FeatherViolet/FeatherTeal, which these three presets now seed from directly.
    TEAL(
        R.string.settings_accent_teal,
        primaryDark = Color(0xFF2EC4B6), onPrimaryDark = Color(0xFF00332C),
        primaryContainerDark = Color(0xFF114940), onPrimaryContainerDark = Color(0xFF8FF3E3),
        primaryLight = Color(0xFF0E8577), onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFC7F5EC), onPrimaryContainerLight = Color(0xFF002420),
    ),
    BLUE(
        R.string.settings_accent_blue,
        primaryDark = Color(0xFF6FA4FF), onPrimaryDark = Color(0xFF00265E),
        primaryContainerDark = Color(0xFF163E7A), onPrimaryContainerDark = Color(0xFFD6E5FF),
        primaryLight = Color(0xFF2E63D6), onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFD8E5FF), onPrimaryContainerLight = Color(0xFF001B3D),
    ),
    VIOLET(
        R.string.settings_accent_violet,
        primaryDark = Color(0xFFC4A6F5), onPrimaryDark = Color(0xFF38215E),
        primaryContainerDark = Color(0xFF4F3785), onPrimaryContainerDark = Color(0xFFE9DBFF),
        primaryLight = Color(0xFF7B3FD1), onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFE8DBFF), onPrimaryContainerLight = Color(0xFF22004D),
    ),
    GREEN(
        R.string.settings_accent_green,
        primaryDark = Color(0xFF6FDB94), onPrimaryDark = Color(0xFF00391C),
        primaryContainerDark = Color(0xFF1F5135), onPrimaryContainerDark = Color(0xFF8BF8AF),
        primaryLight = Color(0xFF1B6B3F), onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFA6F2C0), onPrimaryContainerLight = Color(0xFF00210F),
    ),
    AMBER(
        R.string.settings_accent_amber,
        primaryDark = Color(0xFFFFB95C), onPrimaryDark = Color(0xFF452B00),
        primaryContainerDark = Color(0xFF624000), onPrimaryContainerDark = Color(0xFFFFDDB3),
        primaryLight = Color(0xFF8A5100), onPrimaryLight = Color(0xFFFFFFFF),
        primaryContainerLight = Color(0xFFFFDDB3), onPrimaryContainerLight = Color(0xFF2C1600),
    );

    fun primary(isDark: Boolean) = if (isDark) primaryDark else primaryLight
    fun onPrimary(isDark: Boolean) = if (isDark) onPrimaryDark else onPrimaryLight
    fun primaryContainer(isDark: Boolean) = if (isDark) primaryContainerDark else primaryContainerLight
    fun onPrimaryContainer(isDark: Boolean) = if (isDark) onPrimaryContainerDark else onPrimaryContainerLight
}

package tv.own.owntv.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Material 3 tonal palette for OwnTV (teal-seeded). NEUTRAL + secondary/tertiary roles are
 * theme-only; the `primary` roles are seeded per [AccentColor] (default teal == these values).
 *
 * Dark uses a near-black background (#0A0A0A) so the panel colours (Phase 6) pop against the
 * deep dark surface. Neutrals (background/surface/secondary) are true greyscale — no accent hue
 * baked in — so every accent choice reads cleanly against them; only `primary` carries colour.
 */

// Brand mark color (the OwnTV play logo) — constant.
val AccentCyan = Color(0xFF52DBC8)

// ---------------- DARK (M3 dark over near-black #0A0A0A) ----------------
val DarkBackground = Color(0xFF0A0A0A) // Option A — nav + inter-panel gap surface
val DarkSurface = Color(0xFF121212)
val DarkSurfaceContainerLowest = Color(0xFF0D0D0D)
val DarkSurfaceContainerLow = Color(0xFF1A1A1A)
val DarkSurfaceContainer = Color(0xFF1E1E1E)
val DarkSurfaceContainerHigh = Color(0xFF282828)
val DarkSurfaceContainerHighest = Color(0xFF333333)
val DarkOnSurface = Color(0xFFE1E1E1)
val DarkOnSurfaceVariant = Color(0xFFC4C4C4)
val DarkOutline = Color(0xFF8E8E8E)
val DarkOutlineVariant = Color(0xFF444444)
// Secondary family — a neutral grey "off"/unselected tone (settings toggles, profile icon tile,
// unselected nav rows). Deliberately colourless so it never fights whichever accent is active.
val DarkSecondary = Color(0xFFC0C0C0)
val DarkOnSecondary = Color(0xFF2A2A2A)
val DarkSecondaryContainer = Color(0xFF414141)
val DarkOnSecondaryContainer = Color(0xFFDCDCDC)
// Tertiary stays a distinct blue — an intentional info/notice colour (offline banner, warning
// chips), not accent-tied and not part of the neutral-grey cleanup above.
val DarkTertiary = Color(0xFFA9CBE4)
val DarkOnTertiary = Color(0xFF0B3445)
val DarkTertiaryContainer = Color(0xFF294B5D)
val DarkOnTertiaryContainer = Color(0xFFC5E7FF)
val DarkError = Color(0xFFFFB4AB)

// ---------------- LIGHT (M3 light) ----------------
val LightBackground = Color(0xFFF8F8F8)
val LightSurface = Color(0xFFF8F8F8)
val LightSurfaceContainerLowest = Color(0xFFFFFFFF)
val LightSurfaceContainerLow = Color(0xFFF2F2F2)
val LightSurfaceContainer = Color(0xFFECECEC)
val LightSurfaceContainerHigh = Color(0xFFE6E6E6)
val LightSurfaceContainerHighest = Color(0xFFE1E1E1)
val LightOnSurface = Color(0xFF1A1A1A)
val LightOnSurfaceVariant = Color(0xFF444444)
val LightOutline = Color(0xFF747474)
val LightOutlineVariant = Color(0xFFC4C4C4)
// Secondary family — same neutral-grey rationale as the dark set above.
val LightSecondary = Color(0xFF595959)
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFDCDCDC)
val LightOnSecondaryContainer = Color(0xFF161616)
// Tertiary stays a distinct blue — see the dark-theme note above.
val LightTertiary = Color(0xFF416278)
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFC5E7FF)
val LightOnTertiaryContainer = Color(0xFF001E2F)
val LightError = Color(0xFFBA1A1A)
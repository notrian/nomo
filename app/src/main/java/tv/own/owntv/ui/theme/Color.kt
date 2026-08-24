package tv.own.owntv.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Material 3 tonal palette for NomoTV — the "feather" ink/paper redesign (teal-seeded).
 * NEUTRAL + secondary/tertiary roles are theme-only; the `primary` roles are seeded per
 * [AccentColor] (default teal == these values).
 *
 * Dark uses a near-black "ink" background (#0A0A0D) with a 5-tier surface-container ramp rising
 * through "ink-raised" (#17171D) and "ink-card" (#1D1D24). Neutrals (background/surface/secondary)
 * are true greyscale — no accent hue baked in — so every accent choice reads cleanly against them;
 * only `primary` carries colour. Light mode is left close to its previous values: it was already a
 * clean, accent-independent grayscale ramp whose near-white background reads as "paper" without
 * needing a redesign of its own — only its shared error/live-red value moves with the rest.
 */

// Brand mark color (the OwnTV play logo) — constant. Now the feather-teal brand seed.
val AccentCyan = Color(0xFF2EC4B6)

// ---------------- DARK ("ink/paper" — feather palette) ----------------
val DarkBackground = Color(0xFF0A0A0D) // ink
val DarkSurface = Color(0xFF0A0A0D)
val DarkSurfaceContainerLowest = Color(0xFF0A0A0D)
val DarkSurfaceContainerLow = Color(0xFF131318)
val DarkSurfaceContainer = Color(0xFF17171D) // ink-raised
val DarkSurfaceContainerHigh = Color(0xFF1D1D24) // ink-card
val DarkSurfaceContainerHighest = Color(0xFF24242C)
val DarkOnSurface = Color(0xFFF4F4F7) // paper
val DarkOnSurfaceVariant = Color(0xFF9C9CA8) // mist
val DarkOutline = Color(0xFF6A6A76)
val DarkOutlineVariant = Color(0xFF2A2A32)
// Secondary family — a neutral grey "off"/unselected tone (settings toggles, profile icon tile,
// unselected nav rows). Deliberately colourless so it never fights whichever accent is active.
val DarkSecondary = Color(0xFFB8B8C4)
val DarkOnSecondary = Color(0xFF26262C)
val DarkSecondaryContainer = Color(0xFF3A3A44)
val DarkOnSecondaryContainer = Color(0xFFD8D8E0)
// Tertiary stays a distinct blue — an intentional info/notice colour (offline banner, warning
// chips), not accent-tied and not part of the neutral-grey cleanup above.
val DarkTertiary = Color(0xFFA9CBE4)
val DarkOnTertiary = Color(0xFF0B3445)
val DarkTertiaryContainer = Color(0xFF294B5D)
val DarkOnTertiaryContainer = Color(0xFFC5E7FF)
// Shared with the "favorite"/live-red role — matches the feather palette's --live red exactly.
val DarkError = Color(0xFFFF4B4B)

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
// Darkened from the dark-theme #FF4B4B live-red for adequate contrast on a light background,
// same hue family as DarkError.
val LightError = Color(0xFFD63232)

// ---------------- Brand mark gradient ("feather") ----------------
// Fixed brand accent — the logo mark and a small, explicit set of gradient-fill call sites
// (progress bars, storage bars). NOT a user-selectable accent: [AccentColor] presets are the
// only user-facing color choice, this gradient never substitutes for one.
val FeatherBlue = Color(0xFF3E7BFA)
val FeatherViolet = Color(0xFF9B5DE5)
val FeatherTeal = Color(0xFF2EC4B6)
val FeatherGradientColors = listOf(FeatherBlue, FeatherViolet, FeatherTeal)
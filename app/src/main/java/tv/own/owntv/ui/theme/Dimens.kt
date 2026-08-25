package tv.own.owntv.ui.theme

import androidx.compose.ui.unit.dp

/** Shared spacing / sizing tokens for the 4-layer TV shell. */
object Dimens {
    val ScreenPaddingH = 32.dp
    val ScreenPaddingV = 24.dp

    // Layer 1 — MD3 navigation panel. Expands to a drawer (labels) when focused,
    // collapses to an icon rail when focus moves into a submenu.
    val SidebarWidthExpanded = 272.dp
    val SidebarWidthCollapsed = 88.dp
    val TopBarHeight = 48.dp
    /** Normal chrome has no tall audio controls, so it can sit closer to the content panel. */
    val TopBarCompactHeight = 40.dp

    /** Inset between a browse screen's edge and its content (Live/Movies/Series). */
    val BrowseContainerPadding = 12.dp

    // MD3 settings tonal icon tile
    val IconTileSize = 42.dp
    val IconTileCorner = 12.dp

    val GapTiny = 4.dp
    val GapSmall = 8.dp
    val GapMedium = 16.dp
    val GapLarge = 24.dp

    // Feather radius scale (mockup: --radius-sm/md/lg). New named tokens for Phase 2/3 call sites
    // to reference instead of inline literals; existing constants below are unrenamed to avoid
    // touching every current call site, but line up with this scale where noted.
    val RadiusSmall = 8.dp
    val RadiusMedium = 14.dp
    val RadiusLarge = 22.dp

    // Poster tiles (PosterCard) — values match the shipped look exactly; centralized for tuning.
    val PosterCardCorner = 14.dp // == RadiusMedium
    val PosterArtCorner = 8.dp // == RadiusSmall
    val PosterPadding = 6.dp
    val PosterProgressHeight = 4.dp

    // M3 expressive shape scale (larger, rounder than the defaults).
    val CornerSmall = 12.dp
    val CornerMedium = 18.dp
    val CornerLarge = 24.dp
    val CardCorner = 22.dp // == RadiusLarge

    val FocusBorderWidth = 2.dp

    val HomeRowPaddingH = 20.dp

    // Hero carousel
    val HeroBaseWidth = 180.dp
    val HeroMetaHeight = 84.dp
    val HeroGap = 14.dp
    val HeroCardCorner = 18.dp
    val HeroPosterCorner = 14.dp
    val HeroMaxCardHeight = 354.dp
    val HeroMinCardHeight = 200.dp
    val HeroOverlayMaxWidth = 400.dp
    val HeroProgressHeight = 3.dp
}

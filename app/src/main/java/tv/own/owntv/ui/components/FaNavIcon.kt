package tv.own.owntv.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.features.shell.MainSection
import androidx.compose.foundation.layout.wrapContentSize

/**
 * Font Awesome 6 Free — Solid. Drop the downloaded `fa-solid-900.otf` into
 * app/src/main/res/font/ as `fa_solid.otf` (Android font-resource file names may only contain
 * lowercase letters, digits and underscores — hyphens aren't allowed, which is why it's renamed
 * from the "fa-solid-900.otf" download).
 */
val FaSolidFontFamily = FontFamily(Font(R.font.fa_solid))

/** Solid-style Unicode codepoints (Font Awesome 6 Free) used by [FaNavIcon]. Swap any of these if
 *  you'd rather use a different glyph from the set. */
private object FaGlyph {
    const val HOUSE = "\uf015" // fa-house
    const val TV = "\uf401" // fa-tv
    const val FILM = "\uf008" // fa-film
    const val LAYER_GROUP = "\uf5fd" // fa-layer-group
    const val DOWNLOAD = "\ue053" // fa-download
    const val TABLE_CELLS = "\uf00a" // fa-table-cells
    const val MAGNIFYING_GLASS = "\uf002" // fa-magnifying-glass
    const val GEAR = "\uf013" // fa-gear
}

/** Renders a single Font Awesome glyph centered in [modifier]'s box, tinted with [color]. */
@Composable
fun FaIcon(glyph: String, color: Color, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        // Glyph is sized off the box's own width so it scales with whatever .size(...) the caller
        // applies, the same way the old Canvas-based icons did.
        val fontSize = with(androidx.compose.ui.platform.LocalDensity.current) { maxWidth.toSp() }
        Text(
            text = glyph,
            fontFamily = FaSolidFontFamily,
            fontSize = fontSize,
            lineHeight = fontSize,
            color = color,
            maxLines = 1,
            textAlign = TextAlign.Center,
            softWrap = false,
            modifier = Modifier.wrapContentSize(unbounded = true),
            )
    }
}

/** Drop-in Font Awesome replacement for the old Canvas-drawn [NavDuotoneIcon]. Same call shape:
 *  `FaNavIcon(section = section, color = iconColor, modifier = Modifier.size(20.dp))`. */
@Composable
fun FaNavIcon(section: MainSection, color: Color, modifier: Modifier = Modifier) {
    val glyph = when (section) {
        MainSection.HOME -> FaGlyph.HOUSE
        MainSection.LIVE_TV -> FaGlyph.TV
        MainSection.MOVIES -> FaGlyph.FILM
        MainSection.SERIES -> FaGlyph.LAYER_GROUP
        MainSection.DOWNLOADS -> FaGlyph.DOWNLOAD
        MainSection.EPG -> FaGlyph.TABLE_CELLS
        MainSection.SEARCH -> FaGlyph.MAGNIFYING_GLASS
        MainSection.SETTINGS -> FaGlyph.GEAR
    }
    FaIcon(glyph = glyph, color = color, modifier = modifier)
}

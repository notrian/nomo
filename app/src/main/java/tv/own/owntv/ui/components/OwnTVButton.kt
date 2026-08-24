package tv.own.owntv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.ui.theme.LocalActionSurface
import tv.own.owntv.ui.theme.OwnTVTheme

/** Visual emphasis for [OwnTVButton]. PRIMARY fills with the accent; SECONDARY is a tonal fill
 *  that lifts to the primary container on focus; GHOST is a faint translucent fill for a button
 *  that should read as secondary chrome (matches the mockup's `.btn-ghost`, e.g. hero "More Info"
 *  next to a filled primary "Watch Live"). */
enum class OwnTVButtonStyle { PRIMARY, SECONDARY, GHOST }

/**
 * Remote-friendly TV button built on [FocusableSurface]. PRIMARY fills with the brand accent;
 * SECONDARY is an outline that fills on focus.
 */
@Composable
fun OwnTVButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: OwnTVButtonStyle = OwnTVButtonStyle.PRIMARY,
    icon: OwnTVIcon? = null,
    enabled: Boolean = true,
    selected: Boolean = false,
    onLongClick: (() -> Unit)? = null,
    // Denser pill (tighter padding + smaller icon) for space-constrained popups like the storage picker.
    compact: Boolean = false,
) {
    val colors = OwnTVTheme.colors
    val shape = RoundedCornerShape(14.dp) // radius-md, matches .btn

    val primary = style == OwnTVButtonStyle.PRIMARY
    val ghost = style == OwnTVButtonStyle.GHOST
    // Frost with whatever surface the host renders on (DIALOGS inside a popup, CARDS on a panel),
    // read from LocalActionSurface. Null → flat (e.g. the fullscreen player over opaque video).
    // Pills are small chrome, so use a lighter frost than the big panels (cf. top-bar chips 0.45).
    val surface = LocalActionSurface.current
    // Ghost's faint translucent fill matches .btn-ghost{background:rgba(255,255,255,.08)} using the
    // theme's own foreground tone (reads correctly in both dark and light, unlike a literal white).
    val ghostFill = colors.onSurface.copy(alpha = 0.08f).compositeOver(colors.surfaceContainerHigh)
    val ghostFocusedFill = colors.onSurface.copy(alpha = 0.14f).compositeOver(colors.surfaceContainerHigh)

    FocusableSurface(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier,
        enabled = enabled,
        selected = selected,
        shape = shape,
        focusedScale = 1.012f,
        // M3 tonal: PRIMARY keeps the primary fill; SECONDARY is a tonal surface that lifts to the
        // primary container on focus; GHOST is a faint translucent fill that brightens on focus.
        unfocusedContainerColor = when {
            primary -> colors.primary
            ghost -> ghostFill
            else -> colors.card
        },
        focusedContainerColor = when {
            primary -> colors.primary
            ghost -> ghostFocusedFill
            else -> colors.primaryContainer
        },
        selectedContainerColor = when {
            primary || selected -> colors.primary
            ghost -> ghostFill
            else -> colors.card
        },
        surface = surface,
        glassFrostScale = 0.9f,
        // Always-on glass edge so the pill reads as glass even when unfocused.
        glassIdleRimAlpha = 0.18f,
    ) { focused ->
        val contentColor = when {
            primary || (selected && !focused) -> colors.onPrimary
            ghost -> colors.onSurface
            focused -> colors.onPrimaryContainer
            else -> colors.textPrimary
        }

        Row(
            modifier = Modifier.padding(
                horizontal = if (compact) 13.dp else 22.dp,
                vertical = if (compact) 6.dp else 12.dp,
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp),
        ) {
            if (selected) {
                Box(Modifier.size(if (compact) 6.dp else 7.dp).background(contentColor, CircleShape))
            }
            if (icon != null) {
                OwnTVIcon(icon = icon, tint = contentColor, filled = true, modifier = Modifier.size(if (compact) 14.dp else 20.dp))
            }
            // Long labels (German / Finnish / Russian) must ellipsize, not hard-clip. The previous
            // `softWrap = false` with no `overflow` sliced the text mid-glyph on almost every translated
            // button. `weight(1f, fill = false)` lets a short label stay content-sized while a long one
            // is constrained to the remaining row width and ellipsizes. (See docs/internationalization.md
            // Phase 0a.)
            Text(
                text = label,
                style = if (compact) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelLarge,
                color = contentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
    }
}

/**
 * Circular icon-only button — matches the mockup's `.btn-icon` (a 44dp translucent circle, e.g.
 * the hero's "Add to Favorites" action next to the primary Watch/More Info buttons). [active]
 * fills it with the brand [FeatherGradientColors] gradient instead of the idle translucent tint,
 * matching `.btn-icon.active`.
 */
@Composable
fun OwnTVIconButton(
    icon: OwnTVIcon,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    active: Boolean = false,
    enabled: Boolean = true,
) {
    val colors = OwnTVTheme.colors
    val surface = LocalActionSurface.current
    val idleFill = colors.onSurface.copy(alpha = 0.08f).compositeOver(colors.surfaceContainerHigh)
    val focusedFill = colors.onSurface.copy(alpha = 0.14f).compositeOver(colors.surfaceContainerHigh)

    FocusableSurface(
        onClick = onClick,
        modifier = modifier.size(44.dp).semantics { this.contentDescription = contentDescription },
        enabled = enabled,
        shape = CircleShape,
        focusedScale = 1.06f,
        unfocusedContainerColor = idleFill,
        focusedContainerColor = focusedFill,
        selectedContainerColor = idleFill,
        surface = surface,
        glassFrostScale = 0.9f,
        glassIdleRimAlpha = 0.18f,
    ) { focused ->
        if (active) {
            Box(
                modifier = Modifier.matchParentSize().background(
                    Brush.linearGradient(tv.own.owntv.ui.theme.FeatherGradientColors),
                    CircleShape,
                ),
            )
        }
        val tint = if (active) colors.background else if (focused) colors.onPrimaryContainer else colors.onSurface
        OwnTVIcon(icon = icon, tint = tint, filled = active, modifier = Modifier.size(19.dp))
    }
}
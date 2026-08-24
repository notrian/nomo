package tv.own.owntv.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.ui.theme.FeatherGradientColors
import tv.own.owntv.ui.theme.GlassSurface
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * A single toggleable chip for a horizontal filter row — matches the mockup's `.chip-btn` /
 * `.chip-btn.active` (active = the feather-gradient fill). This is the shared building block for
 * Movies/Series/Live's genre filter rows and the Detail page's season-picker; don't let each
 * screen roll its own chip. Adapted from [SortChip]'s focus/shape/token pattern.
 */
@Composable
fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    FocusableSurface(
        onClick = onClick,
        selected = selected,
        modifier = modifier,
        shape = RoundedCornerShape(14.dp), // radius-md, matches .chip-btn
        focusedContainerColor = colors.surfaceContainerHighest,
        unfocusedContainerColor = colors.surfaceContainer,
        selectedContainerColor = colors.surfaceContainer,
        renderSelectionContainer = false,
        contentAlignment = Alignment.Center,
        surface = GlassSurface.CARDS,
    ) { focused ->
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = when {
                selected -> colors.background // dark text on the bright gradient fill
                focused -> colors.primary
                else -> colors.onSurfaceVariant
            },
            modifier = Modifier
                .then(
                    if (selected) {
                        Modifier.background(Brush.linearGradient(FeatherGradientColors), RoundedCornerShape(14.dp))
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = 18.dp, vertical = 9.dp),
        )
    }
}

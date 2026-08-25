package tv.own.owntv.features.shell.components

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.delay
import tv.own.owntv.ui.theme.Dimens
import tv.own.owntv.ui.theme.GlassSurface
import tv.own.owntv.ui.theme.OwnTVTheme
import tv.own.owntv.ui.theme.glass
import java.util.Date

// Top-bar chips: corner matches the nav buttons (14dp, not full-pill) and a lighter frost than the
// big panels so the small chrome reads as glass without being heavy. Matches the mockup's .util-chip.
private val TopBarChipCorner = 14.dp
private const val TopBarFrost = 0.45f

/**
 * The util-strip — a right-aligned row of small, always-visible, purely informational chips above
 * the content: the Audio Mode now-playing bar (the one interactive/focusable piece here, only
 * present during Audio Mode), the persistent source-status chip, and a clock. These two status
 * chips are deliberately NOT D-pad targets — there is no reliable focus path into a strip sitting
 * above every content screen, and a chip that's only reachable/visible while the sidebar happens to
 * hold focus reads as a bug (which it was). Keeping them plain and always-drawn fixes that outright.
 */
@Composable
fun TopBar(
    sourceStatusLabel: String,
    sourceStatusActive: Boolean,
    // Audio Mode (plan §8): the now-playing bar, shown while PlayerMode.AUDIO is active. Null = not
    // in Audio Mode.
    audioBar: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    // Audio Mode can expand into a two-line now-playing card and 36 dp transport control. Preserve the
    // existing 48 dp strip for it; ordinary pills need only 40 dp, reclaiming 4 dp above and below.
    val hasAudioBar = audioBar != null
    val barHeight = if (hasAudioBar) Dimens.TopBarHeight else Dimens.TopBarCompactHeight
    val verticalInset = if (hasAudioBar) 4.dp else 2.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            // The complete top strip follows the mockup's small screen-edge inset.
            .padding(end = 20.dp, top = verticalInset, bottom = verticalInset),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            audioBar?.invoke()
            SourceStatusChip(label = sourceStatusLabel, active = sourceStatusActive)
            ClockChip()
        }
    }
}

/** A persistent dot + status text summarizing the active playlist/source. Display-only. */
@Composable
private fun SourceStatusChip(label: String, active: Boolean) {
    val colors = OwnTVTheme.colors
    val shape = RoundedCornerShape(TopBarChipCorner)
    Box(
        Modifier
            .widthIn(max = 240.dp)
            .clip(shape)
            .glass(GlassSurface.TOPBAR, colors.surfaceContainer.copy(alpha = 0.6f), shape, frostScale = TopBarFrost, condenseChrome = true)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier.size(7.dp).clip(CircleShape)
                    .background(if (active) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.5f)),
            )
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = colors.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun ClockChip() {
    val colors = OwnTVTheme.colors
    val context = LocalContext.current
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) { while (true) { delay(15_000); now = System.currentTimeMillis() } }
    val formatted = remember(now) { DateFormat.getTimeFormat(context).format(Date(now)) }
    val shape = RoundedCornerShape(TopBarChipCorner)
    Box(
        Modifier.clip(shape)
            .glass(GlassSurface.TOPBAR, colors.surfaceContainer.copy(alpha = 0.6f), shape, frostScale = TopBarFrost, condenseChrome = true)
            .padding(horizontal = 14.dp, vertical = 7.dp),
    ) {
        Text(formatted, style = MaterialTheme.typography.labelLarge, color = colors.onSurfaceVariant)
    }
}

package tv.own.owntv.features.shell.components

import androidx.compose.runtime.Immutable

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import tv.own.owntv.R
import tv.own.owntv.ui.components.OwnTVButton
import tv.own.owntv.ui.components.OwnTVButtonStyle
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.components.OwnTVIconButton
import tv.own.owntv.ui.theme.BrandDisplayFontFamily
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * Read-only, already-merged data for the [MediaDetailsScreen] window. The caller applies the §7.1/§4.1
 * provider/TMDB merge and builds image.tmdb.org URLs, so this window is source-agnostic and reused for
 * movie / series / episode.
 */
@Immutable
data class MediaDetailsUi(
    val title: String,
    val subtitle: String? = null,       // e.g. "S2 · E5 · aired 2019-04-14"
    val backdropUrl: String? = null,    // 16:9 hero
    val posterUrl: String? = null,      // 2:3 poster (or 16:9 still for episodes)
    val metaLine: String = "",          // "2026 · ★ 7.6 · 2h 10m"
    val genres: List<String> = emptyList(),
    val plot: String? = null,
    val cast: List<tv.own.owntv.core.metadata.CastMember> = emptyList(),
)

/**
 * The mockup's Detail page (`.detail-hero` + `.detail-body`): a full-bleed backdrop hero (~56% of
 * the screen) with a visible back button and bottom-anchored title/meta/description/actions, then
 * a body below (currently just cast — Series routes its season-picker/episode-list through its own
 * richer header instead of through this generic window, see [tv.own.owntv.features.series.SeriesScreen]).
 *
 * [onPlay]/[onToggleFavorite] are optional so the window still works exactly as before (read-only,
 * Back exits) for callers that don't have a play/favorite action to offer — only Movies' primary
 * poster-tap flow passes them today.
 */
@Composable
fun MediaDetailsScreen(
    details: MediaDetailsUi,
    onExit: () -> Unit,
    onPlay: (() -> Unit)? = null,
    playLabel: String? = null,
    isFavorite: Boolean = false,
    onToggleFavorite: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    val scroll = rememberScrollState()
    val backFocus = remember { FocusRequester() }
    val playFocus = remember { FocusRequester() }
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) { runCatching { (if (onPlay != null) playFocus else backFocus).requestFocus() } }
    BackHandler { onExit() }

    // Nothing below the hero's action row is focusable (cast is display-only), so Up/Down that fails
    // to move focus bubbles up here and becomes a scroll — same "D-pad drives the scroll" contract
    // the original read-only window had, just coexisting with real Left/Right focus movement now.
    val step = 260f
    val onKey: (androidx.compose.ui.input.key.KeyEvent) -> Boolean = onKey@{ e ->
        if (e.type != KeyEventType.KeyDown) return@onKey false
        when (e.key) {
            Key.DirectionDown -> { scope.launch { scroll.animateScrollBy(step) }; true }
            Key.DirectionUp -> { scope.launch { scroll.animateScrollBy(-step) }; true }
            else -> false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(colors.background).onKeyEvent(onKey)) {
        Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
            // Full-bleed hero — matches the mockup's .detail-hero (~56vh, gradient into the page bg).
            Box(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.56f)) {
                Box(modifier = Modifier.fillMaxSize().background(colors.surfaceContainerLowest)) {
                    if (!details.backdropUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = details.backdropUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize().background(
                        Brush.verticalGradient(
                            0f to colors.background.copy(alpha = 0.04f),
                            0.5f to colors.background.copy(alpha = 0.2f),
                            1f to colors.background,
                        ),
                    ),
                )
                OwnTVIconButton(
                    icon = OwnTVIcon.BACK,
                    contentDescription = stringResource(R.string.common_back),
                    onClick = onExit,
                    modifier = Modifier.padding(20.dp).align(Alignment.TopStart).focusRequester(backFocus),
                )
                Column(
                    modifier = Modifier.align(Alignment.BottomStart).widthIn(max = 760.dp).padding(horizontal = 40.dp, vertical = 32.dp),
                ) {
                    Text(
                        details.title,
                        style = MaterialTheme.typography.displayLarge.copy(fontFamily = BrandDisplayFontFamily, fontWeight = FontWeight.Normal),
                        color = colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!details.subtitle.isNullOrBlank() || details.metaLine.isNotBlank() || details.genres.isNotEmpty()) {
                        Spacer(Modifier.height(14.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (!details.subtitle.isNullOrBlank()) DetailMetaChip(details.subtitle)
                            if (details.metaLine.isNotBlank()) DetailMetaChip(details.metaLine)
                            details.genres.take(3).forEach { DetailMetaChip(it) }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.focusGroup(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (onPlay != null) {
                            OwnTVButton(
                                label = playLabel ?: stringResource(R.string.content_play),
                                onClick = onPlay,
                                icon = OwnTVIcon.PLAY,
                                modifier = Modifier.focusRequester(playFocus),
                            )
                        }
                        if (onToggleFavorite != null) {
                            OwnTVIconButton(
                                icon = OwnTVIcon.FAVORITE,
                                contentDescription = stringResource(
                                    if (isFavorite) R.string.content_remove_favourite else R.string.content_add_favourite,
                                ),
                                onClick = onToggleFavorite,
                                active = isFavorite,
                            )
                        }
                    }
                }
            }

            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp, vertical = 24.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                if (!details.plot.isNullOrBlank()) {
                    Column {
                        Text(stringResource(R.string.content_media_overview), style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                        Spacer(Modifier.height(6.dp))
                        Text(details.plot, style = MaterialTheme.typography.bodyLarge, color = colors.onSurfaceVariant, modifier = Modifier.widthIn(max = 760.dp))
                    }
                }
                if (details.cast.isNotEmpty()) {
                    Column {
                        Text(stringResource(R.string.content_media_cast), style = MaterialTheme.typography.titleMedium, color = colors.onSurface)
                        Spacer(Modifier.height(10.dp))
                        CastGrid(details.cast)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMetaChip(label: String) {
    val colors = OwnTVTheme.colors
    Text(
        label,
        style = MaterialTheme.typography.labelLarge,
        color = colors.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .border(1.dp, colors.outlineVariant, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp),
    )
}

/**
 * Cast as photo + name cards that wrap onto as many lines as needed. Display-only (see the class
 * doc on [MediaDetailsScreen] for how D-pad Up/Down scrolls this into view).
 */
@Composable
private fun CastGrid(cast: List<tv.own.owntv.core.metadata.CastMember>) {
    val colors = OwnTVTheme.colors
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        cast.forEach { member ->
            Column(
                modifier = Modifier.width(88.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val photo = tv.own.owntv.core.metadata.MetadataImages.profile(member.profilePath)
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(36.dp))
                        .background(colors.surfaceContainerHigh),
                    contentAlignment = Alignment.Center,
                ) {
                    if (photo != null) {
                        coil3.compose.AsyncImage(
                            model = photo,
                            contentDescription = null,
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        // Plenty of credited actors have no TMDB photo; initials read better than a gap.
                        Text(
                            member.name.split(' ').mapNotNull { it.firstOrNull() }.take(2)
                                .joinToString("").uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = colors.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    member.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                )
            }
        }
    }
}

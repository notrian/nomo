package tv.own.owntv.features.favorites

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import tv.own.owntv.R
import tv.own.owntv.core.model.MediaType
import tv.own.owntv.features.live.LiveViewModel
import tv.own.owntv.features.movies.MovieViewModel
import tv.own.owntv.features.series.SeriesViewModel
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.components.PosterCard
import tv.own.owntv.ui.components.trapVerticalFocusExit
import tv.own.owntv.ui.theme.Dimens
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * The mockup's dedicated Favorites page: one grid, sectioned by content type (Live/Movies/Series)
 * since they come from three different Paging sources rather than a single merged query — see
 * [FavoritesViewModel]'s doc comment. Matches `.grid`/`.card`/`.empty-state` from the mockup.
 *
 * Reuses the same shared [MovieViewModel]/[SeriesViewModel]/[LiveViewModel] instances Movies/
 * Series/Live already resolve (Koin scopes them to the activity, so this is the same object, not
 * a second one) rather than duplicating playback logic — a favorited movie plays directly, a
 * favorited channel tunes directly, and a favorited series is opened on the shared SeriesViewModel
 * and the caller is asked to switch the shell to the Series tab so it mounts already-opened.
 */
@Composable
fun FavoritesScreen(
    onFullscreen: () -> Unit,
    onChildFocused: () -> Unit,
    onOpenSeries: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    val vm: FavoritesViewModel = koinViewModel()
    val movieVm: MovieViewModel = koinViewModel()
    val seriesVm: SeriesViewModel = koinViewModel()
    val liveVm: LiveViewModel = koinViewModel()
    val scope = rememberCoroutineScope()
    val channels = vm.favoriteChannels.collectAsLazyPagingItems()
    val movies = vm.favoriteMovies.collectAsLazyPagingItems()
    val series = vm.favoriteSeries.collectAsLazyPagingItems()
    val empty = channels.itemCount == 0 && movies.itemCount == 0 && series.itemCount == 0

    Box(
        modifier
            .fillMaxSize()
            .trapVerticalFocusExit()
            .focusGroup()
            .onFocusChanged { if (it.hasFocus) onChildFocused() }
            .padding(horizontal = Dimens.ScreenPaddingH, vertical = Dimens.ScreenPaddingV),
    ) {
        Text(
            stringResource(R.string.content_favorites_title),
            style = MaterialTheme.typography.headlineLarge,
            color = colors.onSurface,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        if (empty) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                androidx.compose.foundation.layout.Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    OwnTVIcon(
                        OwnTVIcon.FAVORITE,
                        tint = colors.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 12.dp).aspectRatio(1f).fillMaxWidth(0.12f),
                    )
                    Text(
                        stringResource(R.string.content_favorites_empty_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = colors.onSurface,
                    )
                    Text(
                        stringResource(R.string.content_favorites_empty_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.onSurfaceVariant,
                    )
                }
            }
            return
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 130.dp),
            modifier = Modifier.fillMaxSize().padding(top = 44.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            if (channels.itemCount > 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(stringResource(R.string.content_favorites_section_live))
                }
                items(count = channels.itemCount, key = channels.itemKey { it.id }, contentType = channels.itemContentType { "channel" }) { i ->
                    val c = channels[i] ?: return@items
                    PosterCard(
                        posterUrl = c.logoUrl,
                        title = c.name,
                        onClick = {
                            scope.launch {
                                // Zap context = the favorited channels already loaded, so Ch+/− in the
                                // player cycles through this same favorites list.
                                if (liveVm.ensurePlayingByIdAsync(c.id, channels.itemSnapshotList.items.filterNotNull())) onFullscreen()
                            }
                        },
                        onLongClick = { vm.removeFavorite(MediaType.LIVE, c.id) },
                    )
                }
            }
            if (movies.itemCount > 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(stringResource(R.string.content_favorites_section_movies))
                }
                items(count = movies.itemCount, key = movies.itemKey { it.id }, contentType = movies.itemContentType { "movie" }) { i ->
                    val m = movies[i] ?: return@items
                    PosterCard(
                        posterUrl = m.posterUrl,
                        title = m.name,
                        rating = m.rating,
                        isFavorite = true,
                        onClick = { movieVm.play(m, 0); onFullscreen() },
                        onLongClick = { vm.removeFavorite(MediaType.MOVIE, m.id) },
                    )
                }
            }
            if (series.itemCount > 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SectionHeader(stringResource(R.string.content_favorites_section_series))
                }
                items(count = series.itemCount, key = series.itemKey { it.id }, contentType = series.itemContentType { "series" }) { i ->
                    val s = series[i] ?: return@items
                    PosterCard(
                        posterUrl = s.posterUrl,
                        title = s.name,
                        rating = s.rating,
                        isFavorite = true,
                        onClick = { seriesVm.openSeries(s); onOpenSeries() },
                        onLongClick = { vm.removeFavorite(MediaType.SERIES, s.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(label: String) {
    val colors = OwnTVTheme.colors
    Text(
        label,
        style = MaterialTheme.typography.titleMedium,
        color = colors.onSurface,
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
    )
}

package tv.own.owntv.features.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import tv.own.owntv.core.database.dao.ChannelDao
import tv.own.owntv.core.database.dao.FavoriteDao
import tv.own.owntv.core.database.dao.MovieDao
import tv.own.owntv.core.database.dao.SeriesDao
import tv.own.owntv.core.database.dao.SourceDao
import tv.own.owntv.core.database.entity.ChannelEntity
import tv.own.owntv.core.database.entity.MovieEntity
import tv.own.owntv.core.database.entity.SeriesEntity
import tv.own.owntv.core.model.MediaType
import tv.own.owntv.core.repository.activeProfileSources
import tv.own.owntv.features.settings.data.SettingsRepository

/**
 * The new Favorites destination (matches the mockup's dedicated Favorites page). Read-only
 * composition of three already-working per-type favorite queries (Movies/Series/Live each already
 * expose a favorites filter inside their own screen's category rail) — this doesn't add any new
 * favorite-tracking logic, just aggregates what [FavoriteDao] already records into one grid.
 *
 * Deliberately NOT scoped to the "active playlist" filter the other Browse screens respect
 * (`ActiveProfileSources.sourceIds`): a favorite is a personal, cross-playlist pick, so it stays
 * visible here regardless of which single playlist is currently selected elsewhere.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModel(
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao,
    private val channelDao: ChannelDao,
    private val favoriteDao: FavoriteDao,
    private val sourceDao: SourceDao,
    private val settings: SettingsRepository,
) : ViewModel() {

    private val profileId: Flow<Long> = activeProfileSources(settings, sourceDao).map { it.profileId }

    private val pagingConfig = PagingConfig(pageSize = 40, prefetchDistance = 20, initialLoadSize = 60)

    val favoriteChannels: Flow<PagingData<ChannelEntity>> = profileId
        .flatMapLatest { pid ->
            if (pid < 0) flowOf(PagingData.empty())
            else Pager(pagingConfig) { channelDao.pagingFavorites(pid) }.flow
        }
        .cachedIn(viewModelScope)

    val favoriteMovies: Flow<PagingData<MovieEntity>> = profileId
        .flatMapLatest { pid ->
            if (pid < 0) flowOf(PagingData.empty())
            else Pager(pagingConfig) { movieDao.pagingFavorites(pid) }.flow
        }
        .cachedIn(viewModelScope)

    val favoriteSeries: Flow<PagingData<SeriesEntity>> = profileId
        .flatMapLatest { pid ->
            if (pid < 0) flowOf(PagingData.empty())
            else Pager(pagingConfig) { seriesDao.pagingFavorites(pid) }.flow
        }
        .cachedIn(viewModelScope)

    /** Long-press on a favorited card removes it — the direct, one-step un-favorite this screen's
     *  whole purpose implies, mirroring the mockup's tap-the-heart-to-toggle card language. */
    fun removeFavorite(type: MediaType, itemId: Long) {
        viewModelScope.launch {
            val pid = profileId.first()
            if (pid >= 0) favoriteDao.remove(pid, type, itemId)
        }
    }
}

package tv.own.owntv.features.shell.components

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.focusGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.ui.components.FilterChip
import tv.own.owntv.ui.components.FocusableSurface
import tv.own.owntv.ui.components.dialogPanel
import tv.own.owntv.ui.components.modalScrim
import tv.own.owntv.ui.components.trapAllFocusExit
import tv.own.owntv.ui.theme.GlassSurface
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * Horizontal category filter row — the mockup's `.filter-rail` of `.chip-btn`s. Replaces the
 * vertical [CategoryRail] for Movies/Series/Live (see the redesign plan's Phase 3 restructure
 * decision). Real playlists can have 15-30 categories (folders, custom groups, Favorites,
 * History) — far more than the mockup's ~5 genre chips — so only the first [maxVisible] show
 * directly; a trailing "More" chip opens [CategoryPickerDialog] with the full list. The current
 * selection always stays visible in the row (swapped into the last slot) even when it would
 * otherwise fall past the cutoff, so the active category is never hidden behind "More".
 */
@Composable
fun CategoryFilterRow(
    categories: List<RailCategory>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    maxVisible: Int = 6,
) {
    if (categories.isEmpty()) return
    val overflow = categories.size > maxVisible
    val visibleSlots = if (overflow) maxVisible - 1 else categories.size
    val visible = remember(categories, selectedIndex, visibleSlots) {
        val base = (0 until visibleSlots).map { it to categories[it] }
        if (selectedIndex in 0 until visibleSlots || selectedIndex !in categories.indices) {
            base
        } else {
            // Selected item is past the cutoff — swap it into the last visible slot.
            base.dropLast(1) + (selectedIndex to categories[selectedIndex])
        }
    }
    var showMore by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visible.forEach { (idx, cat) ->
            FilterChip(label = cat.fullName, selected = idx == selectedIndex, onClick = { onSelect(idx) })
        }
        if (overflow) {
            FilterChip(label = stringResource(R.string.common_more), selected = false, onClick = { showMore = true })
        }
    }

    if (showMore) {
        CategoryPickerDialog(
            categories = categories,
            selectedIndex = selectedIndex,
            onSelect = { onSelect(it); showMore = false },
            onDismiss = { showMore = false },
        )
    }
}

@Composable
private fun CategoryPickerDialog(
    categories: List<RailCategory>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }
    BackHandler { onDismiss() }
    Box(
        modifier = Modifier.fillMaxSize().modalScrim().trapAllFocusExit().focusGroup(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .dialogPanel(width = 460.dp, padding = 24.dp)
                .heightIn(max = 640.dp)
                .verticalScroll(rememberScrollState()),
        ) {
            Text(stringResource(R.string.common_all_categories), style = MaterialTheme.typography.titleLarge, color = colors.onSurface)
            Spacer(Modifier.height(14.dp))
            categories.forEachIndexed { idx, cat ->
                FocusableSurface(
                    onClick = { onSelect(idx) },
                    selected = idx == selectedIndex,
                    modifier = Modifier.fillMaxWidth()
                        .then(if (idx == selectedIndex) Modifier.focusRequester(focus) else Modifier),
                    shape = RoundedCornerShape(12.dp),
                    surface = GlassSurface.DIALOGS,
                    contentAlignment = Alignment.CenterStart,
                ) { _ ->
                    Text(
                        cat.fullName,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (idx == selectedIndex) colors.primary else colors.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
                Spacer(Modifier.height(2.dp))
            }
        }
    }
}

package tv.own.owntv.features.shell.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.features.shell.MainSection
import tv.own.owntv.ui.components.FaNavIcon
import tv.own.owntv.ui.components.FocusableSurface
import tv.own.owntv.ui.components.OwnTVAvatar
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.theme.Dimens
import tv.own.owntv.ui.theme.OwnTVTheme

/**
 * Layer 1 — the navigation rail. Netflix-style overlay: it floats ON TOP of the content (the caller
 * lays this out in a Box above [ContentPane], so the content pane never shifts or resizes) and reads
 * as a flush-left, translucent+blurred panel rather than a bordered card. Collapsed to icon-only when
 * focus is elsewhere in the app; expands to icon + label (with an animated underline under the label
 * of the focused/active row) the moment focus enters it.
 */
@Composable
fun Sidebar(
    selected: MainSection,
    onSelect: (MainSection) -> Unit,
    visibleSections: Set<MainSection>,
    avatarId: Int,
    onPickAvatar: () -> Unit,
    profileName: String,
    sourceSummary: String?,
    onSwitchProfile: () -> Unit,
    onSearchClick: () -> Unit,
    selectedItemFocusRequester: FocusRequester,
    onFocused: () -> Unit,
    counts: (MainSection) -> Int = { 0 },
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    var hasFocus by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Expands to show labels the moment focus is anywhere inside the rail; collapses to icon-only
    // the moment focus leaves it for the content pane. No manual toggle needed.
    val expanded = hasFocus

    val focusSection = when {
        selected == MainSection.SEARCH -> MainSection.HOME
        selected == MainSection.SETTINGS -> MainSection.SETTINGS
        selected in visibleSections -> selected
        else -> MainSection.browseOrder.firstOrNull { it in visibleSections } ?: MainSection.SETTINGS
    }

    val railWidth by animateDpAsState(
        targetValue = if (expanded) Dimens.SidebarWidthExpanded else Dimens.SidebarWidthCollapsed,
        animationSpec = tween(220),
        label = "railWidth",
    )
    // Slightly transparent, frosted glass — not a bordered/rounded card, just a flat translucent
    // plane flush to the left edge. Even lighter footprint when collapsed to icon-only.
    val railAlpha by animateFloatAsState(
        targetValue = if (expanded) 0.78f else 0.55f,
        animationSpec = tween(220),
        label = "railAlpha",
    )
    val blurRadius by animateDpAsState(
        targetValue = if (expanded) 18.dp else 10.dp,
        animationSpec = tween(220),
        label = "railBlur",
    )

    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(railWidth)
            .onFocusChanged {
                val entered = it.hasFocus && !hasFocus
                hasFocus = it.hasFocus
                if (it.hasFocus) onFocused()
                if (entered) scope.launch { runCatching { selectedItemFocusRequester.requestFocus() } }
            }
            .focusGroup(),
    ) {
        // Backdrop layer — ONLY this is blurred/translucent. Compose's blur() affects everything
        // drawn inside its modifier chain, so the icons/text must live in a separate sibling
        // (below) rather than inside this same Column, or the whole rail (including the text)
        // would blur along with the background.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(blurRadius)
                .background(colors.background.copy(alpha = railAlpha)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AppLogo(expanded = expanded)
            Spacer(Modifier.height(16.dp))

            SearchEntry(expanded = expanded, onClick = onSearchClick)
            Spacer(Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    MainSection.browseOrder.filter { it in visibleSections }.forEach { section ->
                        NavItem(
                            label = stringResource(section.labelRes),
                            section = section,
                            active = section == selected,
                            expanded = expanded,
                            onClick = { onSelect(section) },
                            modifier = if (section == focusSection) {
                                Modifier.focusRequester(selectedItemFocusRequester)
                            } else Modifier,
                        )
                        Spacer(Modifier.height(2.dp))
                    }
                }
            }

            // Footer group order: Settings, then the read-only source status, then profile.
            NavItem(
                label = stringResource(MainSection.SETTINGS.labelRes),
                section = MainSection.SETTINGS,
                active = selected == MainSection.SETTINGS,
                expanded = expanded,
                onClick = { onSelect(MainSection.SETTINGS) },
                modifier = if (focusSection == MainSection.SETTINGS) {
                    Modifier.focusRequester(selectedItemFocusRequester)
                } else Modifier,
            )
            Spacer(Modifier.height(10.dp))

            SourceDisplay(expanded = expanded, sourceSummary = sourceSummary)
            Spacer(Modifier.height(12.dp))

            ProfileRow(
                expanded = expanded,
                avatarId = avatarId,
                profileName = profileName,
                onPickAvatar = onPickAvatar,
                onSwitchProfile = onSwitchProfile,
            )
        }
    }
}

@Composable
private fun AppLogo(expanded: Boolean, modifier: Modifier = Modifier) {
    val colors = OwnTVTheme.colors
    // Same lockstep-animation approach as NavItem: an animated lead-in padding instead of a discrete
    // Arrangement flip, so the mark doesn't jump to center then drift as the rail width tweens.
    val iconLeadPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 12.dp,
        animationSpec = tween(220),
        label = "logoIconPadding",
    )
    Row(
        modifier = modifier.fillMaxWidth().padding(start = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier.padding(start = iconLeadPadding).size(28.dp),
            contentAlignment = Alignment.Center,
        ) {
            OwnTVIcon(icon = OwnTVIcon.PLAY, tint = colors.primary, modifier = Modifier.size(22.dp), filled = true)
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = expanded,
            enter = androidx.compose.animation.fadeIn(tween(220)) +
                androidx.compose.animation.expandHorizontally(tween(220), expandFrom = Alignment.Start),
            exit = androidx.compose.animation.fadeOut(tween(180)) +
                androidx.compose.animation.shrinkHorizontally(tween(180), shrinkTowards = Alignment.Start),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(10.dp))
                Text(
                    stringResource(R.string.app_name),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Read-only display of the active IPTV source — never editable from here; changing sources still
 * happens in Settings → Manage Sources / the playlist picker. Lives as a compact footer pill above
 * Settings/profile. Fixed single-line height at all times: it used to grow to a two-line label when
 * expanded and shrink to a bare dot when collapsed, which shifted every row above it up and down on
 * every focus change — pinning the height (and keeping it one line) fixes that for good.
 */
@Composable
private fun SourceDisplay(expanded: Boolean, sourceSummary: String?) {
    val colors = OwnTVTheme.colors
    val noSourceLabel = stringResource(R.string.shell_no_source)
    val label = sourceSummary ?: noSourceLabel
    val connected = sourceSummary != null

    val rowHorizontalPadding by animateDpAsState(
        targetValue = if (expanded) 10.dp else 0.dp,
        animationSpec = tween(220),
        label = "sourceRowPadding",
    )
    val dotLeadPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 14.dp,
        animationSpec = tween(220),
        label = "sourceDotPadding",
    )
    // Collapses down to exactly its own height (36dp) so a fixed 18dp corner radius reads as a true
    // circle badge, matching the collapsed rail's icon-only rows — a wide rounded rectangle read as
    // an odd leftover "pill" once everything else went icon-only. Expands back out to the rail's
    // known content width (SidebarWidthExpanded minus the Column's 16dp side padding).
    val pillWidth by animateDpAsState(
        targetValue = if (expanded) Dimens.SidebarWidthExpanded - 32.dp else 36.dp,
        animationSpec = tween(220),
        label = "sourcePillWidth",
    )

    Row(
        modifier = Modifier
            .width(pillWidth)
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(colors.onSurface.copy(alpha = 0.05f))
            .padding(horizontal = rowHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        Box(
            modifier = Modifier
                .padding(start = dotLeadPadding)
                .size(8.dp)
                .clip(CircleShape)
                .background(if (connected) Color(0xFF6EE7A0) else colors.onSurfaceVariant),
        )
        androidx.compose.animation.AnimatedVisibility(
            visible = expanded,
            enter = androidx.compose.animation.fadeIn(tween(220)) +
                androidx.compose.animation.expandHorizontally(tween(220), expandFrom = Alignment.Start),
            exit = androidx.compose.animation.fadeOut(tween(180)) +
                androidx.compose.animation.shrinkHorizontally(tween(180), shrinkTowards = Alignment.Start),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(8.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 150.dp),
                )
            }
        }
    }
}

/**
 * Entry point into the existing Search destination — visually lives in the rail now (icon + label,
 * same ladder styling as the other nav rows), but just forwards to whatever Search already does.
 */
@Composable
private fun SearchEntry(expanded: Boolean, onClick: () -> Unit) {
    NavItem(
        label = stringResource(MainSection.SEARCH.labelRes),
        section = MainSection.SEARCH,
        active = false,
        expanded = expanded,
        onClick = onClick,
    )
}

@Composable
private fun NavItem(
    label: String,
    section: MainSection,
    active: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = OwnTVTheme.colors
    val density = LocalDensity.current

    FocusableSurface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        selected = active,
        shape = RoundedCornerShape(6.dp),
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        selectedContainerColor = Color.Transparent,
        showFocusBorder = false,
        renderSelectionContainer = false,
        contentAlignment = Alignment.CenterStart,
    ) { focused ->
        val isActive = active || focused
        // grey by default; white when hovered/focused/selected — the Netflix ladder from the mockup.
        val contentColor = if (isActive) colors.onSurface else colors.onSurfaceVariant
        val underlineWidth by animateFloatAsState(
            targetValue = if (isActive) 1f else 0f,
            animationSpec = tween(180),
            label = "underline",
        )
        // Hover/focus alone gets a plain white underline (matches contentColor). The row that's
        // actually the current section keeps the brand accent color on both its icon and underline
        // at all times — even when focus has moved elsewhere — so "selected" reads as a persistent
        // colored marker instead of only ever looking identical to "currently hovered."
        val underlineColor = if (active) colors.primary else colors.onSurface
        val iconColor = if (active) colors.primary else contentColor
        // The actual measured width of the label text (px), captured via onSizeChanged. The underline
        // is sized off of this rather than the column's own width, so it never stretches to match a
        // marquee's expanded layout width or the row's own max width — it always tracks the glyphs.
        var labelWidthPx by remember { mutableIntStateOf(0) }

        // Row width and icon lead-in animate on the SAME tween as the rail's own width (220ms) so the
        // icon slides in lockstep with the rail instead of snapping to a new Arrangement the instant
        // focus changes (which is what produced the "closes on the right, then slides left" glitch).
        val rowHorizontalPadding by animateDpAsState(
            targetValue = if (expanded) 8.dp else 0.dp,
            animationSpec = tween(220),
            label = "navItemRowPadding",
        )
        // Centers the 20dp icon within the collapsed rail's ~56dp content width; animates to 0 as the
        // rail expands and the row switches to a flush-left layout.
        val iconLeadPadding by animateDpAsState(
            targetValue = if (expanded) 0.dp else 18.dp,
            animationSpec = tween(220),
            label = "navItemIconPadding",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Fixed height regardless of expanded state. This row used to size itself off its
                // content (icon-only vs icon+text+underline are different heights), and since this
                // whole footer group sits below a weight(1f) spacer, any height change here shifted
                // the footer's position — the last bit of the "still teleporting a tad" drift.
                .height(40.dp)
                .padding(horizontal = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            FaNavIcon(
                section = section,
                color = iconColor,
                modifier = Modifier
                    .padding(start = iconLeadPadding)
                    .size(20.dp),
            )
            androidx.compose.animation.AnimatedVisibility(
                visible = expanded,
                enter = androidx.compose.animation.fadeIn(tween(220)) +
                    androidx.compose.animation.expandHorizontally(tween(220), expandFrom = Alignment.Start),
                exit = androidx.compose.animation.fadeOut(tween(180)) +
                    androidx.compose.animation.shrinkHorizontally(tween(180), shrinkTowards = Alignment.Start),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(14.dp))
                    Column(
                        modifier = Modifier.widthIn(max = 160.dp),
                    ) {
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                            color = contentColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .onSizeChanged { labelWidthPx = it.width }
                                .then(
                                    if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier,
                                ),
                        )
                        // Underline sits only under the label text, not the icon: width is driven off
                        // the text's own measured width (not the column's, which can stretch wider than
                        // the glyphs once basicMarquee is applied) and animates in/out.
                        Box(
                            modifier = Modifier
                                .padding(top = 3.dp)
                                .width(with(density) { (labelWidthPx * underlineWidth.coerceIn(0f, 1f)).toDp() })
                                .height(2.dp)
                                .background(underlineColor),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileRow(
    expanded: Boolean,
    avatarId: Int,
    profileName: String,
    onPickAvatar: () -> Unit,
    onSwitchProfile: () -> Unit,
) {
    val colors = OwnTVTheme.colors
    val leadPadding by animateDpAsState(
        targetValue = if (expanded) 0.dp else 11.dp,
        animationSpec = tween(220),
        label = "profileLeadPadding",
    )
    // Tracks the avatar tile's focus state (TV "hover" = D-pad focus) so the name label beside it
    // can pick up the same emphasis — bold + underline — while the row is focused.
    var profileFocused by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = leadPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        FocusableSurface(
            onClick = onSwitchProfile,
            onLongClick = onPickAvatar,
            modifier = Modifier.size(34.dp),
            // Square (rounded-corner) container instead of a circle: the avatar art itself is already
            // drawn as a rounded square, and clipping that to a circle was cropping its corners off —
            // that's the "cutoff" edges. Also drop the built-in focus/selection border ring so nothing
            // colored outlines it; the background tint + scale on focus is enough feedback.
            shape = RoundedCornerShape(9.dp),
            showFocusBorder = false,
            focusedScale = 1.02f,
            focusedContainerColor = colors.surfaceContainerHighest,
            unfocusedContainerColor = Color.Transparent,
            selectedContainerColor = Color.Transparent,
            contentAlignment = Alignment.Center,
        ) { focused ->
            LaunchedEffect(focused) { profileFocused = focused }
            OwnTVAvatar(avatarId = avatarId, modifier = Modifier.size(32.dp))
        }
        androidx.compose.animation.AnimatedVisibility(
            visible = expanded,
            enter = androidx.compose.animation.fadeIn(tween(220)) +
                androidx.compose.animation.expandHorizontally(tween(220), expandFrom = Alignment.Start),
            exit = androidx.compose.animation.fadeOut(tween(180)) +
                androidx.compose.animation.shrinkHorizontally(tween(180), shrinkTowards = Alignment.Start),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(Modifier.width(10.dp))
                Text(
                    profileName.ifBlank { stringResource(R.string.common_own_tv_user) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurface,
                    fontWeight = if (profileFocused) FontWeight.Bold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
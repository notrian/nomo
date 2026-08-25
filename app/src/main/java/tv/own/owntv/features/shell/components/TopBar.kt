package tv.own.owntv.features.shell.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import tv.own.owntv.R
import tv.own.owntv.core.weather.WeatherInfo
import tv.own.owntv.ui.components.FocusableSurface
import tv.own.owntv.ui.components.OwnTVIcon
import tv.own.owntv.ui.theme.Dimens
import tv.own.owntv.ui.theme.GlassSurface
import tv.own.owntv.ui.theme.OwnTVTheme
import tv.own.owntv.ui.theme.glass

// Top-bar chips: corner matches the nav buttons (14dp, not full-pill) and a lighter frost than the
// big panels so the small chrome reads as glass without being heavy. Matches the mockup's .util-chip.
private val TopBarChipCorner = 14.dp
private const val TopBarFrost = 0.45f

/**
 * The util-strip (mockup: `.util-strip`) — a right-aligned row of small status chips above the
 * content: the Audio Mode now-playing bar, a "Continue" resume chip, the weather chip, and a
 * persistent source-status chip. Unlike the mockup's `position:fixed` overlay, this occupies real
 * space at the top of the content column (content renders below it) — a TV-appropriate translation:
 * a floating overlay would cover the top-right corner of whatever hero/grid is on screen underneath
 * it on every single page, which the mockup (a scrolling web page) never has to contend with.
 *
 * The section/search chips the mockup's sidebar already owns (and a plain clock, which the mockup
 * doesn't show at all) are deliberately not part of this bar — see [Sidebar]'s own search entry.
 */
@Composable
fun TopBar(
    sourceStatusLabel: String,
    sourceStatusActive: Boolean,
    onSourceStatusClick: () -> Unit,
    sourceStatusDownFocusRequester: FocusRequester? = null,
    weatherInfo: WeatherInfo? = null,
    weatherFahrenheit: Boolean = false,
    // Batch 7 — shared "Continue" chip (resume last movie/episode/channel). Null label = nothing to resume.
    continueLabel: String? = null,
    continueIcon: OwnTVIcon = OwnTVIcon.PLAY,
    onContinueClick: () -> Unit = {},
    // The Continue and source-status chips are real D-pad targets sitting directly above the content
    // area — a held Up from a long grid/list can outrun composition and land here (the original
    // top-bar-focus-escape fix). Only focusable while the nav panel holds focus, same rule as before.
    chipsFocusable: Boolean = true,
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
            if (continueLabel != null) {
                ContinueChip(label = continueLabel, icon = continueIcon, onClick = onContinueClick, visible = chipsFocusable)
            }
            if (weatherInfo != null) WeatherChip(info = weatherInfo, fahrenheit = weatherFahrenheit)
            SourceStatusChip(
                label = sourceStatusLabel,
                active = sourceStatusActive,
                onClick = onSourceStatusClick,
                visible = chipsFocusable,
                downFocusRequester = sourceStatusDownFocusRequester,
            )
        }
    }
}

@Composable
private fun ContinueChip(label: String, icon: OwnTVIcon, onClick: () -> Unit, visible: Boolean) {
    val colors = OwnTVTheme.colors
    val alpha by animateFloatAsState(if (visible) 1f else 0f, label = "continueChipAlpha")
    FocusableSurface(
        onClick = onClick,
        modifier = Modifier
            .widthIn(max = 240.dp)
            .graphicsLayer { this.alpha = alpha }
            .focusProperties { canFocus = visible },
        shape = RoundedCornerShape(TopBarChipCorner),
        surface = GlassSurface.TOPBAR,
        glassFrostScale = TopBarFrost,
        glassIdleRimAlpha = 0.18f,
        glassCondensesWithContent = true,
        focusedContainerColor = colors.primary,
        unfocusedContainerColor = colors.primaryContainer.copy(alpha = 0.6f),
        contentAlignment = Alignment.Center,
    ) { focused ->
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val fg = if (focused) colors.onPrimary else colors.onPrimaryContainer
            OwnTVIcon(icon = icon, tint = fg, modifier = Modifier.size(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = fg,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .then(
                        if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier,
                    ),
            )
        }
    }
}

/**
 * Mockup: `sourceStatusChip` — a persistent dot + status text, clicking through to Settings ›
 * Manage Sources (or, when there's more than one playlist to pick between, the existing quick-switch
 * picker first — a real capability this app has that the single-source mockup demo doesn't model).
 */
@Composable
private fun SourceStatusChip(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    visible: Boolean,
    downFocusRequester: FocusRequester? = null,
) {
    val colors = OwnTVTheme.colors
    val alpha by animateFloatAsState(if (visible) 1f else 0f, label = "sourceStatusChipAlpha")
    FocusableSurface(
        onClick = onClick,
        modifier = Modifier
            .widthIn(max = 240.dp)
            .graphicsLayer { this.alpha = alpha }
            .focusProperties {
                canFocus = visible
                if (downFocusRequester != null) down = downFocusRequester
            },
        shape = RoundedCornerShape(TopBarChipCorner),
        surface = GlassSurface.TOPBAR,
        glassFrostScale = TopBarFrost,
        glassIdleRimAlpha = 0.18f,
        glassCondensesWithContent = true,
        focusedContainerColor = colors.primaryContainer,
        unfocusedContainerColor = colors.surfaceContainer.copy(alpha = 0.6f),
        contentAlignment = Alignment.Center,
    ) { focused ->
        val fg = if (focused) colors.onPrimaryContainer else colors.onSurfaceVariant
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                Modifier.size(7.dp).clip(CircleShape)
                    .background(if (active) colors.primary else colors.onSurfaceVariant.copy(alpha = 0.5f)),
            )
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = fg,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.then(
                    if (focused) Modifier.basicMarquee(iterations = Int.MAX_VALUE) else Modifier,
                ),
            )
        }
    }
}

@Composable
private fun WeatherChip(info: WeatherInfo, fahrenheit: Boolean) {
    val colors = OwnTVTheme.colors
    val temp = if (fahrenheit) {
        stringResource(R.string.common_weather_fahrenheit, (info.temperatureC * 9 / 5 + 32).toInt())
    } else {
        stringResource(R.string.common_weather_celsius, info.temperatureC.toInt())
    }
    val location = if (info.city.isNotBlank()) stringResource(R.string.common_weather_city, temp, info.city) else temp
    val shape = RoundedCornerShape(TopBarChipCorner)
    Box(Modifier.clip(shape).glass(GlassSurface.TOPBAR, colors.surfaceContainer.copy(alpha = 0.6f), shape, frostScale = TopBarFrost, condenseChrome = true).padding(horizontal = 14.dp, vertical = 7.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            WeatherConditionIcon(info = info, Modifier.size(16.dp))
            Text(location, style = MaterialTheme.typography.labelLarge, color = colors.onSurfaceVariant, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun WeatherConditionIcon(info: WeatherInfo, modifier: Modifier = Modifier) {
    val key = info.symbolKey()
    val sunC = Color(0xFFFFD166); val moonC = Color(0xFFDDF8FF)
    val cloudC = Color(0xFFDDEFE9); val rainC = Color(0xFF76A7FF)
    val snowC = Color(0xFFF0FCFF); val fogC = Color(0xFFDDF8FF)
    val thunderC = Color(0xFFFFD166)

    Canvas(modifier) {
        val s = size.minDimension / 100f
        val fill = androidx.compose.ui.graphics.drawscope.Fill
        val stk = Stroke(width = 4f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
        fun o(x: Float, y: Float) = Offset(x * s, y * s)

        fun sun(cx: Float, cy: Float, r: Float) {
            for (i in 0 until 10) { val a = i * kotlin.math.PI.toFloat() / 5f; drawLine(sunC, o(cx + kotlin.math.cos(a) * (r + 8f), cy + kotlin.math.sin(a) * (r + 8f)), o(cx + kotlin.math.cos(a) * (r + 20f), cy + kotlin.math.sin(a) * (r + 20f)), strokeWidth = 4f * s, cap = StrokeCap.Round) }
            drawCircle(sunC, r * s, o(cx, cy))
        }
        fun moon(cx: Float, cy: Float, r: Float) {
            drawCircle(moonC, r * s, o(cx, cy))
            drawCircle(Color.Black, (r * 0.92f * s), o(cx + r * 0.45f * s, cy - r * 0.20f * s), style = fill, blendMode = BlendMode.Clear)
            listOf(-0.42f to -0.28f, -0.20f to 0.25f, 0.02f to -0.06f).forEach { (dx, dy) -> drawCircle(moonC.copy(alpha = 0.55f), 2.2f * s, o(cx + dx * r, cy + dy * r)) }
        }
        fun cloud(cx: Float, cy: Float, k: Float) {
            drawCircle(cloudC, 16f * k * s, o(cx - 19f * k, cy + 5f * k))
            drawCircle(cloudC, 23f * k * s, o(cx, cy - 9f * k))
            drawCircle(cloudC, 18f * k * s, o(cx + 24f * k, cy + 2f * k))
            drawCircle(cloudC, 13f * k * s, o(cx + 39f * k, cy + 10f * k))
        }
        fun drops(cx: Float, cy: Float, n: Int, c: Color) {
            for (i in 0 until n) drawLine(c, o(cx + i * 18f, cy), o(cx - 5f + i * 18f, cy + 18f), strokeWidth = 4f * s, cap = StrokeCap.Round)
        }
        fun snow(cx: Float, cy: Float) {
            for (i in 0 until 3) { val x = cx + i * 20f; val y = cy + (i % 2) * 4f; val w = 3f * s; val c = StrokeCap.Round; drawLine(snowC, o(x - 7f, y), o(x + 7f, y), w, c); drawLine(snowC, o(x, y - 7f), o(x, y + 7f), w, c); drawLine(snowC, o(x - 5f, y - 5f), o(x + 5f, y + 5f), w, c); drawLine(snowC, o(x + 5f, y - 5f), o(x - 5f, y + 5f), w, c) }
        }
        fun fog(cx: Float, cy: Float) {
            for (i in 0 until 4) drawLine(fogC.copy(alpha = 0.74f), o(cx - 38f, cy + i * 12f), o(cx + 38f, cy + i * 12f), strokeWidth = 5f * s, cap = StrokeCap.Round)
        }
        fun bolt(cx: Float, cy: Float) { val p = Path().apply { moveTo(cx * s, cy * s); lineTo((cx - 12f) * s, (cy + 26f) * s); lineTo((cx + 1f) * s, (cy + 23f) * s); lineTo((cx - 8f) * s, (cy + 48f) * s); lineTo((cx + 18f) * s, (cy + 15f) * s); lineTo((cx + 4f) * s, (cy + 18f) * s); close() }; drawPath(p, thunderC, style = fill) }

        when (key) {
            "sunny" -> sun(50f, 50f, 23f)
            "clearNight" -> moon(50f, 50f, 28f)
            "partlyDay" -> { sun(36f, 36f, 17f); cloud(56f, 60f, 1f) }
            "partlyNight" -> { moon(36f, 35f, 20f); cloud(56f, 60f, 1f) }
            "cloudy" -> { cloud(46f, 48f, 1.15f); cloud(60f, 62f, 0.82f) }
            "fog" -> { cloud(50f, 36f, 0.9f); fog(50f, 58f) }
            "drizzle" -> { cloud(50f, 38f, 1f); drops(35f, 62f, 3, rainC.copy(alpha = 0.72f)) }
            "rain" -> { cloud(50f, 36f, 1.05f); drops(30f, 60f, 4, rainC) }
            "snow" -> { cloud(50f, 36f, 1.05f); snow(32f, 65f) }
            "thunder" -> { cloud(50f, 35f, 1.05f); bolt(52f, 48f); drops(28f, 66f, 2, rainC) }
            else -> cloud(46f, 48f, 1.15f)
        }
    }
}

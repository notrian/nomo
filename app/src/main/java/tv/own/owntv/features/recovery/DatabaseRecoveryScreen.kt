package tv.own.owntv.features.recovery

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import androidx.compose.ui.res.stringResource
import tv.own.owntv.R

/**
 * Shown instead of the shell when the database cannot be opened — a migration that failed, or a file
 * SQLite refuses. The app used to wipe itself in this situation (`fallbackToDestructiveMigration`);
 * it no longer does, so the user's data is still on the device and the fix is a code fix, not a
 * reinstall. This screen says exactly that and offers a retry, with a clearly-labelled last-resort
 * reset that only happens if the user asks for it twice.
 *
 * Deliberately outside the design system: [MainActivity] renders this BEFORE the normal
 * `OwnTVTheme`-wrapped tree even starts (see `probeDatabase()`), specifically so this screen has no
 * dependency on anything that could itself be affected by whatever broke the database — no
 * `OwnTVTheme.colors`, no `OwnTVButton` (which reads the theme internally too). The colors below are
 * the ink/paper palette's literal hex values (matching Color.kt's DarkBackground/DarkOnSurface/
 * DarkOnSurfaceVariant/DarkError) copied in directly rather than referenced, so this stays visually
 * consistent with the rest of the app without adding that dependency.
 */
@Composable
fun DatabaseRecoveryScreen(
    message: String?,
    onRetry: () -> Unit,
    onResetData: () -> Unit,
) {
    var confirmingReset by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { runCatching { focus.requestFocus() } }

    Box(Modifier.fillMaxSize().background(Color(0xFF0A0A0D)), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.widthIn(max = 760.dp).padding(48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(stringResource(R.string.recovery_title), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF4F4F7))
            Text(
                stringResource(R.string.recovery_data_safe_full),
                fontSize = 18.sp,
                color = Color(0xFFF4F4F7),
            )
            if (!message.isNullOrBlank()) {
                Text(message, fontSize = 14.sp, color = Color(0xFF9C9CA8))
            }
            Button(onClick = onRetry, modifier = Modifier.focusRequester(focus).focusable()) {
                Text(stringResource(R.string.recovery_retry))
            }
            Text(
                stringResource(R.string.recovery_backup_hint),
                fontSize = 14.sp,
                color = Color(0xFF9C9CA8),
            )
            Button(onClick = { if (confirmingReset) onResetData() else confirmingReset = true }) {
                Text(stringResource(if (confirmingReset) R.string.recovery_confirm_reset else R.string.recovery_reset))
            }
        }
    }
}

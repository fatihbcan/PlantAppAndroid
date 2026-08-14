package com.plantappmvi.android.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.plantappmvi.android.core.designsystem.R
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.core.presentation.resource.asString

@Composable
fun AppLoader(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        color = AppTheme.colors.brand,
        strokeWidth = AppTheme.dimens.strokeThick,
        modifier = modifier.size(AppTheme.dimens.spaceXxl),
    )
}

@Immutable
data class ErrorStateProps(
    val message: TextResource,
    val retryText: TextResource = TextResource.fromId(R.string.action_retry),
    val onRetry: (() -> Unit)? = null,
)

/**
 * The retry affordance is nullable rather than always-present: a section whose
 * failure the user cannot act on should not offer a button that does nothing.
 */
@Composable
fun ErrorState(
    props: ErrorStateProps,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceSm),
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.dimens.spaceLg),
    ) {
        Text(
            text = props.message.asString(),
            style = AppTheme.typography.bodyMd,
            color = AppTheme.colors.danger,
            textAlign = TextAlign.Center,
        )
        props.onRetry?.let { onRetry ->
            TextButton(onClick = onRetry) {
                Text(
                    text = props.retryText.asString(),
                    style = AppTheme.typography.label,
                    color = AppTheme.colors.brand,
                )
            }
        }
    }
}

@Immutable
data class EmptyStateProps(val message: TextResource)

@Composable
fun EmptyState(
    props: EmptyStateProps,
    modifier: Modifier = Modifier,
) {
    Text(
        text = props.message.asString(),
        style = AppTheme.typography.bodyMd,
        color = AppTheme.colors.onCanvasMuted,
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(AppTheme.dimens.spaceLg),
    )
}

@DayNightPreviews
@Composable
private fun ErrorStatePreview() = AppTheme {
    ErrorState(
        ErrorStateProps(
            message = TextResource.fromId(R.string.error_no_connection),
            onRetry = {},
        ),
    )
}

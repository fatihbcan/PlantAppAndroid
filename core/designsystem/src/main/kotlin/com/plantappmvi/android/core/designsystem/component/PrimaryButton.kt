package com.plantappmvi.android.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.core.presentation.resource.asString

@Immutable
data class PrimaryButtonProps(
    val text: TextResource,
    val isEnabled: Boolean = true,
    val isLoading: Boolean = false,
    val onClick: () -> Unit = {},
)

@Composable
fun PrimaryButton(
    props: PrimaryButtonProps,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = props.onClick,
        enabled = props.isEnabled && !props.isLoading,
        shape = AppTheme.shapes.button,
        colors = ButtonDefaults.buttonColors(
            containerColor = AppTheme.colors.brand,
            contentColor = AppTheme.colors.onPremium,
            disabledContainerColor = AppTheme.colors.brand.copy(alpha = DisabledAlpha),
            disabledContentColor = AppTheme.colors.onPremium.copy(alpha = DisabledAlpha),
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.dimens.controlHeight),
    ) {
        if (props.isLoading) {
            CircularProgressIndicator(
                color = AppTheme.colors.onPremium,
                strokeWidth = AppTheme.dimens.strokeThick,
                modifier = Modifier.size(AppTheme.dimens.iconMd),
            )
        } else {
            Text(text = props.text.asString(), style = AppTheme.typography.button)
        }
    }
}

private const val DisabledAlpha = 0.4f

@DayNightPreviews
@Composable
private fun PrimaryButtonPreview() = AppTheme {
    PrimaryButton(PrimaryButtonProps(text = TextResource.fromString("Continue")))
}

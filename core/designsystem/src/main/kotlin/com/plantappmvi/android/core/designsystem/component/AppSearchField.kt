package com.plantappmvi.android.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import com.plantappmvi.android.core.designsystem.R
import com.plantappmvi.android.core.designsystem.icon.AppIcons
import com.plantappmvi.android.core.designsystem.modifier.noRippleClickable
import com.plantappmvi.android.core.designsystem.theme.AppTheme
import com.plantappmvi.android.core.presentation.preview.DayNightPreviews
import com.plantappmvi.android.core.presentation.resource.TextResource
import com.plantappmvi.android.core.presentation.resource.asString

@Immutable
data class AppSearchFieldProps(
    val query: String,
    val placeholder: TextResource,
    val onQueryChange: (String) -> Unit = {},
    val onClear: () -> Unit = {},
)

/**
 * Built from [BasicTextField] rather than Material's `TextField`, which
 * carries a fixed minimum height plus its own internal padding — neither
 * matches the design's field, and fighting them costs more than drawing the
 * row directly.
 */
@Composable
fun AppSearchField(
    props: AppSearchFieldProps,
    modifier: Modifier = Modifier,
) {
    val selectionColors = TextSelectionColors(
        handleColor = AppTheme.colors.brand,
        backgroundColor = AppTheme.colors.brand.copy(alpha = SelectionAlpha),
    )
    val clearLabel = stringResource(R.string.action_clear_search)

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spaceMd),
            modifier = modifier
                .fillMaxWidth()
                .height(AppTheme.dimens.controlHeight)
                .clip(AppTheme.shapes.field)
                .background(AppTheme.colors.surface)
                .padding(horizontal = AppTheme.dimens.spaceLg),
        ) {
            AppIcon(
                icon = AppIcons.Search,
                size = AppTheme.dimens.iconSm,
                tint = AppTheme.colors.onCanvasSubtle,
            )

            Box(modifier = Modifier.weight(1f)) {
                if (props.query.isEmpty()) {
                    Text(
                        text = props.placeholder.asString(),
                        style = AppTheme.typography.bodyMd,
                        color = AppTheme.colors.onCanvasSubtle,
                    )
                }
                BasicTextField(
                    value = props.query,
                    onValueChange = props.onQueryChange,
                    singleLine = true,
                    textStyle = AppTheme.typography.bodyMd.copy(color = AppTheme.colors.onCanvas),
                    cursorBrush = SolidColor(AppTheme.colors.brand),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            if (props.query.isNotEmpty()) {
                AppIcon(
                    icon = AppIcons.Close,
                    size = AppTheme.dimens.iconSm,
                    tint = AppTheme.colors.onCanvasSubtle,
                    modifier = Modifier
                        .noRippleClickable(onClick = props.onClear)
                        .semantics { contentDescription = clearLabel },
                )
            }
        }
    }
}

private const val SelectionAlpha = 0.3f

@DayNightPreviews
@Composable
private fun AppSearchFieldPreview() = AppTheme {
    AppSearchField(
        AppSearchFieldProps(
            query = "",
            placeholder = TextResource.fromString("Search for plants"),
        ),
    )
}

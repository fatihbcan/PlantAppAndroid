package com.plantappmvi.android.core.presentation.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Renders a component in both schemes at once. A hardcoded colour is caught
 * the moment someone opens the preview pane, which is the cheapest possible
 * dark-mode test.
 */
@Preview(name = "Light", uiMode = Configuration.UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
annotation class DayNightPreviews

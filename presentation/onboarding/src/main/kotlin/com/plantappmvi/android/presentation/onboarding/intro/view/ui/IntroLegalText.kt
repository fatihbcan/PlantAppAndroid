package com.plantappmvi.android.presentation.onboarding.intro.view.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.plantappmvi.android.core.designsystem.theme.AppTheme

/**
 * The consent line under the welcome page's call to action.
 *
 * The sentence stays one translatable string; the policy names are passed in
 * separately and located inside it, so a translator can move them without the
 * underlines coming adrift.
 */
@Composable
internal fun IntroLegalText(
    text: String,
    underlinedPhrases: List<String>,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text.withUnderlined(underlinedPhrases),
        style = AppTheme.typography.caption,
        color = AppTheme.colors.onCanvasMuted,
        textAlign = TextAlign.Center,
        modifier = modifier.semantics { contentDescription = text },
    )
}

/** Walks the sentence once, splitting at whichever marked phrase comes next. */
private fun String.withUnderlined(phrases: List<String>): AnnotatedString = buildAnnotatedString {
    var cursor = 0

    while (cursor < length) {
        val next = phrases
            .filter { it.isNotEmpty() }
            .mapNotNull { phrase ->
                indexOf(phrase, cursor).takeIf { it >= 0 }?.let { it to phrase }
            }
            .minByOrNull { it.first }

        if (next == null) {
            append(substring(cursor))
            break
        }

        val (at, phrase) = next
        if (at > cursor) append(substring(cursor, at))
        withStyle(SpanStyle(textDecoration = TextDecoration.Underline)) { append(phrase) }
        cursor = at + phrase.length
    }
}

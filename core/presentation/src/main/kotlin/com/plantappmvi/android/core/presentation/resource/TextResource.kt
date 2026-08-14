package com.plantappmvi.android.core.presentation.resource

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

/**
 * Deferred user-facing text.
 *
 * Props are built by `mapStateToProps`, a plain non-`@Composable` function
 * that cannot call `stringResource()`. This defers resolution to composition,
 * so presentation logic stays testable on the JVM and previews still render
 * real copy.
 */
@Immutable
sealed interface TextResource {

    @Immutable
    data class Text(val text: String) : TextResource

    @Immutable
    data class Id(@StringRes val id: Int, val formatArgs: List<Any>) : TextResource

    @Immutable
    data class Plural(
        @PluralsRes val pluralId: Int,
        val count: Int,
        val formatArgs: List<Any>,
    ) : TextResource

    companion object {
        fun fromString(text: String): TextResource = Text(text)

        fun fromId(@StringRes id: Int, vararg args: Any): TextResource = Id(id, args.toList())

        fun fromPlural(@PluralsRes id: Int, count: Int, vararg args: Any): TextResource =
            Plural(id, count, args.toList())
    }
}

// The spread is required: `stringResource` takes `vararg Any`, and there is no
// list-taking overload to call instead.
@Suppress("SpreadOperator")
@Composable
fun TextResource.asString(): String = when (this) {
    is TextResource.Text -> text
    is TextResource.Id -> stringResource(id, *formatArgs.toTypedArray())
    is TextResource.Plural -> pluralStringResource(pluralId, count, *formatArgs.toTypedArray())
}

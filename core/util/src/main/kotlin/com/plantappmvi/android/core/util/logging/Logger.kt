package com.plantappmvi.android.core.util.logging

/**
 * Logging as an interface so pure-Kotlin layers can report without importing
 * `android.util.Log`. The real implementation is bound in the composition root.
 */
interface Logger {
    fun debug(message: String)
    fun warn(message: String, cause: Throwable? = null)
    fun error(message: String, cause: Throwable? = null)
}

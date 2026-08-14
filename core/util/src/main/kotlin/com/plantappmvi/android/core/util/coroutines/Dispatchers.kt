package com.plantappmvi.android.core.util.coroutines

import javax.inject.Qualifier

/**
 * Dispatcher qualifiers.
 *
 * A class that needs a dispatcher injects one of these rather than naming
 * `Dispatchers.IO` inline, so a test can substitute a deterministic one.
 * The bindings live in the composition root.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

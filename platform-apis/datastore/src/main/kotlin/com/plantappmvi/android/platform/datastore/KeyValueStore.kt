package com.plantappmvi.android.platform.datastore

import java.io.IOException

/**
 * The persistence surface the data layer sees. Deliberately narrow: this app
 * stores one flag, and a general-purpose store would be an abstraction with
 * one caller.
 */
interface KeyValueStore {
    /** @return the stored value, or null when the key was never written. */
    suspend fun readBoolean(key: String): Boolean?

    suspend fun writeBoolean(key: String, value: Boolean)
}

/** Thrown when the underlying store cannot be read or written. */
class StorageException(message: String, cause: Throwable? = null) : IOException(message, cause)

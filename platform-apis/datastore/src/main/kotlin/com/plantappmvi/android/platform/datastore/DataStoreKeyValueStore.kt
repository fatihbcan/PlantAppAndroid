package com.plantappmvi.android.platform.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val PREFERENCES_NAME = "plantapp_preferences"

private val Context.preferencesDataStore: DataStore<Preferences> by
    preferencesDataStore(name = PREFERENCES_NAME)

@Singleton
internal class DataStoreKeyValueStore @Inject constructor(
    @ApplicationContext private val context: Context,
) : KeyValueStore {

    override suspend fun readBoolean(key: String): Boolean? = try {
        context.preferencesDataStore.data.first()[booleanPreferencesKey(key)]
    } catch (cause: IOException) {
        throw StorageException("Could not read '$key'", cause)
    }

    override suspend fun writeBoolean(key: String, value: Boolean) {
        try {
            context.preferencesDataStore.edit { it[booleanPreferencesKey(key)] = value }
        } catch (cause: IOException) {
            throw StorageException("Could not write '$key'", cause)
        }
    }
}

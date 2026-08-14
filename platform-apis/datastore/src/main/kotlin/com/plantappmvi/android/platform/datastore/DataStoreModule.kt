package com.plantappmvi.android.platform.datastore

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Like [com.plantappmvi.android.platform.network.NetworkModule], this binds
 * infrastructure to itself rather than domain to data, so it stays with the
 * implementation it hides — which lets that implementation stay `internal`.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface DataStoreModule {

    @Binds
    @Singleton
    fun bindKeyValueStore(impl: DataStoreKeyValueStore): KeyValueStore
}

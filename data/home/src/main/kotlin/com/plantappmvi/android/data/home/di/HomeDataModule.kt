package com.plantappmvi.android.data.home.di

import com.plantappmvi.android.data.home.datasource.HomeRemoteDataSource
import com.plantappmvi.android.data.home.datasource.HomeRemoteDataSourceImpl
import com.plantappmvi.android.data.home.repository.HomeRepositoryImpl
import com.plantappmvi.android.domain.home.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** See `OnboardingDataModule` for why these bindings live here. */
@Module
@InstallIn(SingletonComponent::class)
internal interface HomeDataModule {

    @Binds
    @Singleton
    fun bindRemoteDataSource(impl: HomeRemoteDataSourceImpl): HomeRemoteDataSource

    @Binds
    @Singleton
    fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}

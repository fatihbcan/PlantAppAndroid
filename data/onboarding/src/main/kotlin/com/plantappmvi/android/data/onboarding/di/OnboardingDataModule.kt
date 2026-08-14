package com.plantappmvi.android.data.onboarding.di

import com.plantappmvi.android.data.onboarding.datasource.OnboardingLocalDataSource
import com.plantappmvi.android.data.onboarding.datasource.OnboardingLocalDataSourceImpl
import com.plantappmvi.android.data.onboarding.repository.OnboardingRepositoryImpl
import com.plantappmvi.android.domain.onboarding.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * A deliberate deviation from the blueprint, which puts every domain→data
 * `@Binds` in the composition root.
 *
 * Binding here is what lets `OnboardingRepositoryImpl`, the data sources and
 * the DTOs all stay `internal`: a `@Binds` in `framework:app` would force
 * every one of them public, and Kotlin would then require the whole chain of
 * types in their signatures to be public too.
 *
 * The property the blueprint's rule protects — nothing outside this module
 * knows an implementation exists — comes out stronger, not weaker, because
 * the compiler now enforces it. The composition root still owns everything
 * that genuinely crosses features: the routes, the NavHost and every
 * Navigator implementation.
 */
@Module
@InstallIn(SingletonComponent::class)
internal interface OnboardingDataModule {

    @Binds
    @Singleton
    fun bindLocalDataSource(impl: OnboardingLocalDataSourceImpl): OnboardingLocalDataSource

    @Binds
    @Singleton
    fun bindOnboardingRepository(impl: OnboardingRepositoryImpl): OnboardingRepository
}

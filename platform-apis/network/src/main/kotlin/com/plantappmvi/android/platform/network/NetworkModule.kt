package com.plantappmvi.android.platform.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Endpoints and timeouts for the case API.
 */
object ApiConfig {
    const val BASE_URL = "https://dummy-api-jtg6bessta-ey.a.run.app/"

    const val CONNECT_TIMEOUT_SECONDS = 15L
    const val READ_TIMEOUT_SECONDS = 20L
    const val WRITE_TIMEOUT_SECONDS = 15L
}

/**
 * Infrastructure wiring lives with the infrastructure. This module binds
 * nothing across an architectural layer — it hands out a configured client —
 * so it does not belong in the composition root, which owns the domain→data
 * bindings and the navigators.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(errorInterceptor: ErrorInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .connectTimeout(ApiConfig.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(ApiConfig.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(ApiConfig.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .addInterceptor(errorInterceptor)
            .build()

    /**
     * Note what is *not* here: the Flutter build of this app needs a
     * `JsonDecodeInterceptor`, because both endpoints answer with
     * `content-type: text/plain` and Dio dispatches its decoder on that
     * header. Retrofit picks a converter by the declared return type instead,
     * so kotlinx-serialization parses these bodies regardless of what the
     * server claims they are. The quirk is real; the workaround is not needed.
     */
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}

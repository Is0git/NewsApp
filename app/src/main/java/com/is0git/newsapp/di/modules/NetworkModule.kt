package com.is0git.newsapp.di.modules

import com.is0git.newsapp.network.constants.API_KEY
import com.is0git.newsapp.network.constants.BASE_URL
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.network.services.NewsSourceService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object NetworkModule {

    @Provides
    @Singleton
    fun getLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    }

    @Provides
    @Singleton
    fun getOkHttpClient(interceptor: HttpLoggingInterceptor) = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .connectTimeout(100, TimeUnit.SECONDS)
        .writeTimeout(100, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .addInterceptor {
            val request = it.request().newBuilder()
                .addHeader("X-Api-Key", API_KEY)
                .build()
            it.proceed(request)
        }
        .build()

    @Provides
    @Singleton
    fun getRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Provides
    fun getNewsSourceService(retrofit: Retrofit): NewsSourceService {
        return retrofit.create(NewsSourceService::class.java)
    }

    @Provides
    fun getNewsHeadlinesService(retrofit: Retrofit): NewsHeadlinesService {
        return retrofit.create(NewsHeadlinesService::class.java)
    }
}
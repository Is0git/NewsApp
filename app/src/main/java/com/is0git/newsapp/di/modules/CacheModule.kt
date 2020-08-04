package com.is0git.newsapp.di.modules

import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.di.qualifiers.cache.HeadlineCacheQualifier
import com.is0git.newsapp.models.common.ArticlesItem
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
abstract class CacheModule {
    @Binds
    @HeadlineCacheQualifier
    abstract fun getHeadlineCache(cache: HeadlineCache) : DataCache<ArticlesItem>
}
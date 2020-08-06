package com.is0git.newsapp.vm.sources

import android.content.Context
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.source_cache.SourceCache
import com.is0git.newsapp.network.models.sources.SourcesItem
import com.is0git.newsapp.network.services.NewsSourceService
import com.is0git.newsapp.utils.executeNetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

open class SourcesRepository @Inject constructor(
    private val newsApiService: NewsSourceService,
    private val sourceCache: SourceCache,
    @ApplicationContext var appContext: Context
) : DataCache<SourcesItem> by sourceCache {

    suspend fun getNewsSources() {
        val result = executeNetworkRequest(appContext) {
            newsApiService.getNewsSources()
        }
        if (result != null) cacheData(result.sources)
    }
}
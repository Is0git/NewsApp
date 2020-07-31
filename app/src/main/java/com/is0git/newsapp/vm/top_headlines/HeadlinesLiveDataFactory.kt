package com.is0git.newsapp.vm.top_headlines

import androidx.lifecycle.LiveData
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.network.models.common.ArticlesItem

object HeadlinesLiveDataFactory {
    fun create(headlineCache: HeadlineCache, limit: Int, category: String): LiveData<List<ArticlesItem>> {
        return headlineCache.getHeadlinesByCategory(limit, category)
    }
}
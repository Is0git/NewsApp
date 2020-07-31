package com.is0git.newsapp.vm.top_headlines

import android.content.Context
import androidx.lifecycle.LiveData
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.di.qualifiers.cache.HeadlineCacheQualifier
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.network.services.NewsSourceService
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_BUSINESS
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_ENTERTAINMENT
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_GENERAL
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_HEALTH
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_SCIENCE
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_SPORTS
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment.Companion.CATEGORY_TECHNOLOGY
import com.is0git.newsapp.utils.executeNetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class TopHeadlinesRepository @Inject constructor(
    private val newsHeadlinesService: NewsHeadlinesService,
    @ApplicationContext val applicationContext: Context,
    @HeadlineCacheQualifier val dataCache: DataCache<ArticlesItem>
) : DataCache<ArticlesItem> by dataCache {

    val categoriesLiveData = mutableListOf<LiveData<List<ArticlesItem>>>()
    private val categories: Array<out String> = arrayOf(
        CATEGORY_BUSINESS,
        CATEGORY_ENTERTAINMENT,
        CATEGORY_GENERAL,
        CATEGORY_HEALTH,
        CATEGORY_SCIENCE,
        CATEGORY_SPORTS,
        CATEGORY_TECHNOLOGY
    )

    suspend fun init() {
        addCategories(*categories, pageSize = DEFAULT_PAGE_SIZE)
        getCategories(*categories, pageSize = DEFAULT_PAGE_SIZE)
    }

    private suspend fun addCategories(vararg categories: String, pageSize: Int) {
        coroutineScope {
            val cache = (dataCache as HeadlineCache)
            val receiverChannel = produce(capacity = Channel.BUFFERED) {
                for (c in categories) {
                    send(c)
                }
            }
            for (s in receiverChannel) {
                launch {
                    val liveData = HeadlinesLiveDataFactory.create(cache, pageSize, s)
                    categoriesLiveData.add(liveData)
                }
            }
        }
    }

    private suspend fun getCategories(vararg categories: String, pageSize: Int) {
        coroutineScope {
            val receiveChannel = produce(capacity = Channel.BUFFERED) {
                for (c in categories) {
                    send(c)
                }
            }
            for (c in receiveChannel) {
                launch(Dispatchers.IO) {
                    val networkResult = executeNetworkRequest(applicationContext) {
                        newsHeadlinesService.getTopHeadLines(
                            0,
                            pageSize,
                            c
                        )
                    }
                    if (networkResult != null) {
                        networkResult.articles?.forEach {
                            it.category = c
                        }
                        cacheData(networkResult.articles)
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 10
    }
}
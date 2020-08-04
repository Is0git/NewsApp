package com.is0git.newsapp.vm.category_view_all

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.data.db.dao.PagedHeadlineDao
import com.is0git.newsapp.mediators.HeadlineMediator
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.paging_sources.ViewAllPagingSource
import com.is0git.newsapp.vm.top_headlines.TopHeadlinesRepository.Companion.DEFAULT_PAGE_SIZE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HeadlineViewAllRepository @Inject constructor(
    @ApplicationContext val context: Context,
    val dao: PagedHeadlineDao,
    dataCache: HeadlineCache,
    private val service: NewsHeadlinesService
) : DataCache<ArticlesItem> by dataCache {

    fun getAllArticlesStream(category: String, country: String): Flow<PagingData<ArticlesItem>> {
        Log.d("HeadlinebRepository", "New query: $country")
        // appending '%' so we can allow other characters to be before and after the query string
        val mediator = HeadlineMediator(service, context, dao).apply {
            this.category = category
            this.country = country
        }
        val pagingSource = { dao.getPagingSource("business", "lt") }
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = mediator,
            pagingSourceFactory = pagingSource
        ).flow
    }

    fun getAllArticlesOnlyNetworkStream(
        category: String,
        country: String
    ): Flow<PagingData<ArticlesItem>> {
        val dataSource = ViewAllPagingSource(context, service, category, country)
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE)
        ) {
            dataSource
        }.flow
    }
}
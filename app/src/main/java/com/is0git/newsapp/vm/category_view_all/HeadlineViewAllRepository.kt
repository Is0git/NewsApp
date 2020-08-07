package com.is0git.newsapp.vm.category_view_all

import android.content.Context
import android.util.Log
import androidx.paging.*
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.data.db.dao.PagedHeadlineDao
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.paging.HeadlineMediator
import com.is0git.newsapp.paging.ViewAllPagingSource
import com.is0git.newsapp.paging.seperators.UiModel
import com.is0git.newsapp.vm.top_headlines.TopHeadlinesRepository.Companion.DEFAULT_PAGE_SIZE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
        val mediator = HeadlineMediator(
            service,
            context,
            dao
        ).apply {
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
    ): Flow<PagingData<UiModel>> {
        return Pager(
            config = PagingConfig(DEFAULT_PAGE_SIZE, enablePlaceholders = true)
        ) {
            ViewAllPagingSource(context, service, category, country)
        }.flow.map { pagingData -> pagingData.map { UiModel.Data(it) } }
            .map {
                it.insertSeparators<UiModel.Data, UiModel> { before: UiModel.Data?, after: UiModel.Data? ->
                    if (after == null) {
                        return@insertSeparators null
                    }

                    if (before == null) return@insertSeparators null
                    val comparison = before.compare(before.articlesItem, after.articlesItem)
                    if (comparison > 0) {
                        UiModel.Separator(after.articlesItem.publishedAt)
                    } else {
                        // no separator
                        null
                    }
                }
            }
    }
}
package com.is0git.newsapp.mediators

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.is0git.newsapp.data.db.dao.PagedHeadlineDao
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.utils.executeNetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@ExperimentalPagingApi
class HeadlineMediator(val category: String, val country: String) : RemoteMediator<Int, ArticlesItem>() {

    @Inject lateinit var service: NewsHeadlinesService
    @Inject @ApplicationContext lateinit var appContext: Context
    @Inject lateinit var pagedHeadlineDao: PagedHeadlineDao

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticlesItem>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.APPEND -> {
                state.lastItemOrNull() ?: return MediatorResult.Success(true)
                state.pages.size + 1
            }
            LoadType.PREPEND -> return MediatorResult.Success(true)
        }
        val response = executeNetworkRequest(appContext) {
            service.getTopHeadLines(loadKey, state.pages.size, category, country)
        }
        if (loadType == LoadType.REFRESH) {
            val lastNetworkQuery = state.pages.last()
            pagedHeadlineDao.deleteByQueryResult(lastNetworkQuery.data)
        }
//        if (response == null) MediatorResult.Success(true)\
        if (response != null) {
            pagedHeadlineDao.insertHeadlines(response.articles)
        }
        return MediatorResult.Success(response == null)
    }
}
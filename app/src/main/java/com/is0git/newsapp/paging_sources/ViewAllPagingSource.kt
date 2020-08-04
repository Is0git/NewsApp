package com.is0git.newsapp.paging_sources

import android.content.Context
import androidx.paging.PagingSource
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.utils.executeNetworkRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ViewAllPagingSource @Inject constructor(
    @ApplicationContext val context: Context,
    private val service: NewsHeadlinesService,
    val category: String,
    val country: String
) : PagingSource<Int, ArticlesItem>() {

    private val pageStart = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        val pageNumber = params.key ?: pageStart
        val response = executeNetworkRequest(context) {service.getTopHeadLines(pageNumber, params.loadSize, category, country)}
            ?: return LoadResult.Error(Throwable("null network response"))
        return LoadResult.Page(response.articles!!, null, pageNumber + 1)
    }

}
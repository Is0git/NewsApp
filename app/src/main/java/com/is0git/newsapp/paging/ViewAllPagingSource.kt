package com.is0git.newsapp.paging

import android.accounts.NetworkErrorException
import android.content.Context
import androidx.paging.PagingSource
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

class ViewAllPagingSource @Inject constructor(
    @ApplicationContext val context: Context,
    private val service: NewsHeadlinesService,
    val category: String,
    val country: String
) : PagingSource<Int, ArticlesItem>() {

    private val pageStart = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        try {
            val pageNumber = params.key ?: pageStart
            val response = service.getTopHeadLines(pageNumber, params.loadSize, category, country)
            if (response.body() == null) {
                return LoadResult.Error(Throwable("List has reached the end ;( ${response.message()}"))
            }
            return LoadResult.Page(response.body()!!.articles!!, null, pageNumber + 1)
        } catch (ex: IOException) {
            return LoadResult.Error(Throwable("no internet? Pagination is not cached ;) $ex"))
        }
        catch (ex: NetworkErrorException) {
            return LoadResult.Error(Throwable("network error $ex"))
        }
    }
}
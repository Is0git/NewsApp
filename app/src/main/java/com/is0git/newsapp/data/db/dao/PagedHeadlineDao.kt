package com.is0git.newsapp.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import com.is0git.newsapp.network.models.common.ArticlesItem

@Dao
abstract class PagedHeadlineDao : HeadlineDao() {
    @Query("SELECT * FROM articles_table WHERE category == :category and country == :country")
    abstract fun getPagingSource(
        category: String?,
        country: String?
    ): PagingSource<Int, ArticlesItem>

    @Delete
    abstract suspend fun deleteByQueryResult(query: List<ArticlesItem>)
}
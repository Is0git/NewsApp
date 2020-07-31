package com.is0git.newsapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.is0git.newsapp.network.models.common.ArticlesItem

@Dao
abstract class HeadlineDao {
    @Query("SELECT * from articles_table WHERE articles_table.category == :category limit :limit")
    abstract fun getHeadlineArticles(limit: Int, category: String? = null): LiveData<List<ArticlesItem>>

    @Query("DELETE FROM articles_table WHERE articles_table.category == :category")
    abstract suspend fun deleteArticlesByCategory(category: String)

    @Insert
    abstract suspend fun insertHeadlines(headlines: List<ArticlesItem>?)
}
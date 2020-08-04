package com.is0git.newsapp.data.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.is0git.newsapp.network.models.common.ArticlesItem

@Dao
abstract class HeadlineDao {
    @Query("SELECT * from articles_table WHERE articles_table.category == :category AND country == :country LIMIT :limit")
    abstract fun getHeadlineArticles(limit: Int, category: String? = null, country: String? = null): LiveData<List<ArticlesItem>>

    @Query("DELETE FROM articles_table WHERE articles_table.category == :category AND country == :country")
    abstract suspend fun deleteArticlesByCategory(category: String, country: String? = null)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertHeadlines(headlines: List<ArticlesItem>?)

    @Query("SELECT * FROM articles_table WHERE country == :country")
    abstract fun getAllArticles(country: String?): LiveData<List<ArticlesItem>>

    @Query("DELETE FROM articles_table")
    abstract suspend fun clearAll()
}
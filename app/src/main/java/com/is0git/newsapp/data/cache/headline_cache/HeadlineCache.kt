package com.is0git.newsapp.data.cache.headline_cache

import androidx.lifecycle.LiveData
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.db.dao.HeadlineDao
import com.is0git.newsapp.models.common.ArticlesItem
import javax.inject.Inject

class HeadlineCache @Inject constructor(private val headlineDao: HeadlineDao) :
    DataCache<ArticlesItem> {
    override suspend fun cacheData(data: List<ArticlesItem>?) {
        headlineDao.insertHeadlines(data)
    }

    override fun getCachedLiveData(): LiveData<List<ArticlesItem>> {
        return headlineDao.getHeadlineArticles(10)
    }

    fun getHeadlinesByCategory(
        limit: Int,
        category: String,
        country: String?
    ): LiveData<List<ArticlesItem>> {
        return headlineDao.getHeadlineArticles(limit, category, country)
    }

    suspend fun deleteHeadlinesByCategory(category: String, country: String? = null) {
        headlineDao.deleteArticlesByCategory(category, country)
    }

    override suspend fun deleteAll() {
        headlineDao.clearAll()
    }
}
package com.is0git.newsapp.data.cache.source_cache

import androidx.lifecycle.LiveData
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.db.dao.SourceDao
import com.is0git.newsapp.network.models.sources.SourcesItem
import javax.inject.Inject

class SourceCache @Inject constructor(var sourceDao: SourceDao) : DataCache<SourcesItem> {
    override suspend fun cacheData(data: List<SourcesItem>?) {
        sourceDao.deleteAll()
        sourceDao.insertSourceItems(data)
    }

    override fun getCachedLiveData(): LiveData<List<SourcesItem>> {
        return sourceDao.getSourcesLiveData(10)
    }

    override suspend fun deleteAll() {

    }
}
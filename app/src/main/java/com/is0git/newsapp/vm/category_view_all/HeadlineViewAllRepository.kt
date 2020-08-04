package com.is0git.newsapp.vm.category_view_all

import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.data.db.dao.PagedHeadlineDao
import com.is0git.newsapp.mediators.HeadlineMediator
import com.is0git.newsapp.network.models.common.ArticlesItem
import javax.inject.Inject

class HeadlineViewAllRepository @Inject constructor(dao: PagedHeadlineDao, dataCache: HeadlineCache) : DataCache<ArticlesItem> by dataCache{
        val mediator = HeadlineMediator("business", "lt")
        val pagingSource = dao.getPagingSource("business", "lt")
}
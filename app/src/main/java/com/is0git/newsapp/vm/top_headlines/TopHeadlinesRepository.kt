package com.is0git.newsapp.vm.top_headlines

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.is0git.newsapp.data.cache.DataCache
import com.is0git.newsapp.data.cache.headline_cache.HeadlineCache
import com.is0git.newsapp.data.db.dao.HeadlineDao
import com.is0git.newsapp.di.qualifiers.cache.HeadlineCacheQualifier
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.network.services.NewsHeadlinesService
import com.is0git.newsapp.ui.fragments.top_headlines_fragment.HeadlinesFragment
import com.is0git.newsapp.utils.LocaleUtils.getCurrentCountryCode
import com.is0git.newsapp.utils.executeNetworkRequest
import com.is0git.newsapp.vm.single_job.DefaultSingleJobRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@ExperimentalCoroutinesApi
class TopHeadlinesRepository @Inject constructor(
    private val newsHeadlinesService: NewsHeadlinesService,
    @ApplicationContext val applicationContext: Context,
    @HeadlineCacheQualifier val dataCache: DataCache<ArticlesItem>,
    private val dao: HeadlineDao
) : DefaultSingleJobRepository(),
    DataCache<ArticlesItem> by dataCache {

    val countryCode = applicationContext.getCurrentCountryCode().toLowerCase(Locale.ROOT)
    val country: MutableLiveData<String> = MutableLiveData(countryCode)
    val categoriesLiveData: List<LiveData<List<ArticlesItem>>> =
        HeadlinesFragment.categories.map { categoryString ->
            Transformations.distinctUntilChanged(Transformations.switchMap(country) {
                HeadlinesLiveDataFactory.create(
                    dataCache as HeadlineCache,
                    DEFAULT_PAGE_SIZE,
                    categoryString,
                    it
                )
            })
        }
    val allArticles = Transformations.distinctUntilChanged(Transformations.switchMap(country) {
        dao.getAllArticles(it)
    })

    suspend fun getCategories(
        vararg categories: String,
        pageSize: Int,
        country: String? = null
    ) {
        coroutineScope {
            onJobStart()
            val receiveChannel = produce(capacity = Channel.BUFFERED) {
                for (c in categories) {
                    send(c)
                }
            }
            for (c in receiveChannel) {
                launch(Dispatchers.IO) {
                    val networkResult = executeNetworkRequest(applicationContext) {
                        newsHeadlinesService.getTopHeadLines(
                            0,
                            pageSize,
                            c,
                            country
                        )
                    }
                    if (networkResult != null) {
                        (dataCache as HeadlineCache).deleteHeadlinesByCategory(c, country)
                        networkResult.articles?.forEach {
                            updateArticleForRoom(it, c, country)
                        }
                        cacheData(networkResult.articles)
                    } else {
                        onJobFailed(Throwable("job failed"))
                    }
                }
            }
        }
        onJobCompleted()
    }


    private fun updateArticleForRoom(article: ArticlesItem?, category: String?, country: String?) {
        article?.apply {
            this.category = category
            this.country = country
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE: Int = 10
    }
}
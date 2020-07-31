package com.is0git.newsapp.network.services

import com.is0git.newsapp.network.models.every_headline.EveryHeadline
import com.is0git.newsapp.network.models.sources.NewsSources
import com.is0git.newsapp.network.models.top_headlines.TopHeadlines
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsSourceService {
    @GET("sources")
    suspend fun getNewsSources(@Query("category") category: String? = null): Response<NewsSources>
}


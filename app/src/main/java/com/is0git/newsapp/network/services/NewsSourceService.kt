package com.is0git.newsapp.network.services

import com.is0git.newsapp.models.sources.NewsSources
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsSourceService {
    @GET("sources")
    suspend fun getNewsSources(@Query("category") category: String? = null): Response<NewsSources>
}


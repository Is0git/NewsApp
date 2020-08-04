package com.is0git.newsapp.network.services

import com.is0git.newsapp.models.every_headline.EveryHeadline
import com.is0git.newsapp.models.top_headlines.TopHeadlines
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsHeadlinesService {
    @GET("top-headlines")
    suspend fun getTopHeadLines(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: String? = null,
        @Query("country") country: String? = null
    ): Response<TopHeadlines>

    @GET("everything")
    suspend fun getEveryHeadline(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int,
        @Query("category") category: String? = null,
        @Query("sortBy") sortBy: String
    ): Response<EveryHeadline>
}

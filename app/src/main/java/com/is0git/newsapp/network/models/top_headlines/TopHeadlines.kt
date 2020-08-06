package com.is0git.newsapp.network.models.top_headlines

import com.is0git.newsapp.network.models.common.ArticlesItem
import com.squareup.moshi.Json

data class TopHeadlines(

    @Json(name = "totalResults")
    val totalResults: Int? = null,

    @Json(name = "articles")
    val articles: List<ArticlesItem>? = null,

    @Json(name = "status")
    val status: String? = null
)
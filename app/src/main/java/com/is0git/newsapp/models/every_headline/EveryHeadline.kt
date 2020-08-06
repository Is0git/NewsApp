package com.is0git.newsapp.models.every_headline

import com.is0git.newsapp.models.common.ArticlesItem

data class EveryHeadline(
    val totalResults: Int? = null,
    val articles: List<ArticlesItem?>? = null,
    val status: String? = null
)

package com.is0git.newsapp.models.sources

import com.squareup.moshi.Json

data class NewsSources(

    @Json(name = "sources")
    val sources: List<SourcesItem>? = null,

    @Json(name = "status")
    val status: String? = null
)
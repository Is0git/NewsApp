package com.is0git.newsapp.network.models.common

import com.squareup.moshi.Json

data class Source(

    @Json(name="name")
    val name: String? = null,

    @Json(name="id")
    val id: String? = null
)
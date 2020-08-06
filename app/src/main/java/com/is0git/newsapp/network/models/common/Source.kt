package com.is0git.newsapp.network.models.common

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Source(

    @Json(name = "name")
    val name: String? = null,

    @Json(name = "id")
    val id: String? = null
) : Parcelable
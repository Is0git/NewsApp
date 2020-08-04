package com.is0git.newsapp.data.db.dao

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ssd")
data class TestD(
    @PrimaryKey  val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val url: String,
    val stars: Int,
    val forks: Int,
    val language: String?

) {
}
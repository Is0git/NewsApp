package com.is0git.newsapp.network.models.common

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "articles_table")
data class ArticlesItem(

	@PrimaryKey(autoGenerate = true)
	val tableId: Int,

	@Json(name="publishedAt")
	val publishedAt: String? = null,

	@Json(name="author")
	val author: String? = null,

	@Json(name="urlToImage")
	val urlToImage: String? = null,

	@Json(name="description")
	val description: String? = null,

	@Json(name="source")
	@Embedded
	val source: Source? = null,

	@Json(name="title")
	val title: String? = null,

	@Json(name="url")
	val url: String? = null,

	@Json(name="content")
	val content: String? = null,

	var category: String? = null
) {
	companion object {
		val comparator = object : DiffUtil.ItemCallback<ArticlesItem>() {
			override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
				return oldItem.tableId == newItem.tableId
			}

			override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
				return oldItem == newItem
			}

		}
	}
}
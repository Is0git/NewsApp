package com.is0git.newsapp.models.sources

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "source_table")
data class SourcesItem(

	@Json(name="country")
	val country: String? = null,

	@Json(name="name")
	val name: String? = null,

	@Json(name="description")
	val description: String? = null,

	@Json(name="language")
	val language: String? = null,

	@Json(name="id")
	@PrimaryKey
	val id: String,

	@Json(name="category")
	val category: String? = null,

	@Json(name="url")
	val url: String? = null
) {
	companion object {
		val diffCallback = object : DiffUtil.ItemCallback<SourcesItem>() {
			override fun areItemsTheSame(oldItem: SourcesItem, newItem: SourcesItem): Boolean {
				return oldItem.id == newItem.id
			}

			override fun areContentsTheSame(oldItem: SourcesItem, newItem: SourcesItem): Boolean {
				return oldItem == newItem
			}

		}
	}
}
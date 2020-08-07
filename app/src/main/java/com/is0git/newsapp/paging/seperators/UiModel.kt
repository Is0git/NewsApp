package com.is0git.newsapp.paging.seperators

import androidx.recyclerview.widget.DiffUtil
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.utils.DateUtils.pattern
import java.text.SimpleDateFormat
import java.util.*

sealed class UiModel {
    class Data(val articlesItem: ArticlesItem) : UiModel() {
        fun compare(o1: ArticlesItem?, o2: ArticlesItem?, diffTime: Int = ONE_DAY): Int {
            val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
            val date = simpleDateFormat.getDate(o1)
            val date2 = simpleDateFormat.getDate(o2)
            if (date == null || date2 == null) return -1
            return if (date.time < date2.time) -1
            else {
                val diff = date.time - date2.time
                if (diff > ONE_DAY) return 1
                else -1
            }
        }

        private fun SimpleDateFormat.getDate(articlesItem: ArticlesItem?): Date? {
            if (articlesItem == null && articlesItem?.publishedAt == null) return null
            return parse(articlesItem.publishedAt!!)
        }

        companion object {
            const val ONE_DAY = 86400000

            val UI_MODEL_SEPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
                override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                    return (oldItem is Data && newItem is Data &&
                            oldItem.articlesItem.url == newItem.articlesItem.url) ||
                            (oldItem is Separator && newItem is Separator &&
                                    oldItem.description == newItem.description)
                }

                override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }

    class Separator(val description: String?) : UiModel()
}
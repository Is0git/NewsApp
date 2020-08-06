package com.is0git.newsapp.paging.seperators

import androidx.recyclerview.widget.DiffUtil
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.utils.DateUtils.pattern
import java.text.SimpleDateFormat
import java.util.*

sealed class UiModel {
    class Data(val articlesItem: ArticlesItem) : UiModel(), Comparator<ArticlesItem> {
        override fun compare(o1: ArticlesItem?, o2: ArticlesItem?): Int {
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
            const val ONE_DAY = 8640000

            val UI_MODEL_SEPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
                override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                    return when (oldItem) {
                        is Data -> oldItem.articlesItem.url == (newItem as Data).articlesItem.url
                        is Separator -> oldItem.description == (newItem as Separator).description
                    }
                }

                override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                    return when (oldItem) {
                        is Data -> oldItem.articlesItem == (newItem as Data).articlesItem
                        is Separator -> oldItem.description == (newItem as Separator).description
                    }
                }

            }
        }
    }

    class Separator(val description: String?) : UiModel()

}
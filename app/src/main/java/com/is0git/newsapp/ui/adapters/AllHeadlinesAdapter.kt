package com.is0git.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.is0git.multicategorylayout.databinding.ListItemThreeBinding
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.ArticleSeparatorViewBinding
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.paging.seperators.UiModel
import com.is0git.newsapp.paging.seperators.UiModel.Data.Companion.UI_MODEL_SEPARATOR
import com.is0git.newsapp.utils.DateUtils
import com.is0git.newsapp.utils.loadImageWith
import javax.inject.Inject

class AllHeadlinesAdapter @Inject constructor() :
    PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UI_MODEL_SEPARATOR) {

    var onClickListener: ((item: ArticlesItem) -> Unit)? = null
    var onLinkButtonClick: ((item: ArticlesItem) -> Unit)? = null

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            if (holder is ViewAllItemViewHolder) {
                holder.binding.apply {
                    (item as UiModel.Data).apply {
                        val stringNull = root.context.getString(android.R.string.untitled)
                        descriptionText.text = articlesItem.description ?: stringNull
                        headerText.text = articlesItem.author ?: stringNull
                        headImage.loadImageWith(articlesItem.urlToImage)
                        sourceText.text = articlesItem.source?.name
                        dateText.text = DateUtils.formatDate(articlesItem.publishedAt)
                        root.setOnClickListener {
                            onClickListener?.invoke(articlesItem)
                        }
                        linkButton.setOnClickListener {
                            onLinkButtonClick?.invoke(articlesItem)
                        }
                    }
                }
            }
            if (holder is SeparatorViewHolder) {
                (item as UiModel.Separator).apply {
                    holder.binding.separatorDescription.text =
                        DateUtils.formatDate(this.description)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.Data -> R.layout.list_item_three
            is UiModel.Separator -> R.layout.article_separator_view
            else -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.list_item_three) {
            val binding =
                ListItemThreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewAllItemViewHolder(binding)
        } else {
            val binding = ArticleSeparatorViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            SeparatorViewHolder(binding)
        }
    }

    class ViewAllItemViewHolder(
        val binding: ListItemThreeBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class SeparatorViewHolder(
        val binding: ArticleSeparatorViewBinding
    ) : RecyclerView.ViewHolder(binding.root)
}
package com.is0git.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.is0git.multicategorylayout.databinding.ListItemOneBinding
import com.is0git.multicategorylayout.ui.category_layout.view_holders.VerticalListViewHolder
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.utils.loadImageWith
import javax.inject.Inject

class AllHeadlinesAdapter @Inject constructor() :
    PagingDataAdapter<ArticlesItem, VerticalListViewHolder>(ArticlesItem.comparator) {
    override fun onBindViewHolder(holder: VerticalListViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.descriptionText.text = item.description
            holder.binding.headerText.text = item.author
            holder.binding.headImage.loadImageWith(item.urlToImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalListViewHolder {
        val binding = ListItemOneBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VerticalListViewHolder(binding)
    }
}
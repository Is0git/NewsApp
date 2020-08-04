package com.is0git.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.is0git.multicategorylayout.databinding.ListItemThreeBinding
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.utils.loadImageWith
import javax.inject.Inject

class AllHeadlinesAdapter @Inject constructor() :
    PagingDataAdapter<ArticlesItem, AllHeadlinesAdapter.ViewAllItemViewHolder>(ArticlesItem.comparator) {
    override fun onBindViewHolder(holder: ViewAllItemViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.binding.descriptionText.text = item.description
            holder.binding.headerText.text = item.author
            holder.binding.headImage.loadImageWith(item.urlToImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewAllItemViewHolder {
        val binding = ListItemThreeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewAllItemViewHolder(binding)
    }

    class ViewAllItemViewHolder(val binding: ListItemThreeBinding) : RecyclerView.ViewHolder(binding.root)
}
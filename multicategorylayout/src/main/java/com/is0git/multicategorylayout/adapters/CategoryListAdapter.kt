package com.is0git.multicategorylayout.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class CategoryListAdapter<T, VH : RecyclerView.ViewHolder>(
    diffCallback: DiffUtil.ItemCallback<T>,
    val bind: (holder: VH, item: T, position: Int) -> Unit,
    val createVH: (parent: ViewGroup, viewType: Int) -> VH
) : ListAdapter<T, VH>(diffCallback) {

    override fun onBindViewHolder(holder: VH, position: Int) {
        bind(holder, getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return createVH(parent, viewType)
    }
}
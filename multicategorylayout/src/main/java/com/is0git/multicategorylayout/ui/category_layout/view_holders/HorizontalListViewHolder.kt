package com.is0git.multicategorylayout.ui.category_layout.view_holders

import androidx.recyclerview.widget.RecyclerView
import com.is0git.multicategorylayout.databinding.ListItemTwoBinding

class HorizontalListViewHolder(val binding: ListItemTwoBinding) :
    RecyclerView.ViewHolder(binding.root) {

    var onLinkClick: ((position: Int) -> Unit)? = null
}
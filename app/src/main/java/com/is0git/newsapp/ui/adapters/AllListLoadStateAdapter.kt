package com.is0git.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.LoadStateAdapterLayoutBinding
import javax.inject.Inject

class AllListLoadStateAdapter @Inject constructor() : LoadStateAdapter<LoadStateViewHolder>() {
    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder.create(parent)
    }
}

class LoadStateViewHolder(
    private val binding: LoadStateAdapterLayoutBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        if (loadState.endOfPaginationReached) {
            Toast.makeText(binding.root.context, "END", Toast.LENGTH_SHORT).show()
        }
        if (loadState is LoadState.Error) {
            binding.errorMsg.text = loadState.error.localizedMessage
        }
        binding.loadProgressBar.isVisible = loadState is LoadState.Loading
        binding.errorMsg.isVisible = loadState !is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup): LoadStateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.load_state_adapter_layout, parent, false)
            val binding = LoadStateAdapterLayoutBinding.bind(view)
            return LoadStateViewHolder(binding)
        }
    }
}
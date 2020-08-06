package com.is0git.newsapp.vm.category_view_all

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.paging.seperators.UiModel
import kotlinx.coroutines.flow.Flow

class HeadlineViewAllViewModel @ViewModelInject constructor(
    val repo: HeadlineViewAllRepository,
    @Assisted savedStateHandle: SavedStateHandle
) : ViewModel() {

    fun getAllArticlesStream(category: String, country: String): Flow<PagingData<ArticlesItem>> {
        return repo.getAllArticlesStream(category, country)
            .cachedIn(viewModelScope)
    }

    fun getAllArticlesOnlyNetworkStream(
        category: String,
        country: String
    ): Flow<PagingData<UiModel>> {
        return repo.getAllArticlesOnlyNetworkStream(category, country).cachedIn(viewModelScope)
    }
}
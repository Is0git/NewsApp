package com.is0git.newsapp.vm.top_headlines

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.is0git.newsapp.vm.single_job_viewmodel.DefaultSingleJobViewModel
import kotlinx.coroutines.launch

class TopHeadLinesViewModel @ViewModelInject constructor(
    val topHeadlinesRepository: TopHeadlinesRepository,
    @Assisted savedStateHandle: SavedStateHandle
) : DefaultSingleJobViewModel(topHeadlinesRepository) {

    val categoriesLiveData = topHeadlinesRepository.categoriesLiveData
    val allArticlesLiveData = topHeadlinesRepository.allArticles

    fun getCategories(vararg categories: String, pageSize: Int, country: String? = null) {
        viewModelScope.launch {
            topHeadlinesRepository.getCategories(
                categories = *categories,
                pageSize = pageSize,
                country = country
            )
        }
    }
}
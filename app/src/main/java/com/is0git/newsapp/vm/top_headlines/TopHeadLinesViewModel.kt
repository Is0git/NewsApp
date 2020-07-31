package com.is0git.newsapp.vm.top_headlines

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class TopHeadLinesViewModel @ViewModelInject constructor(topHeadlinesRepository: TopHeadlinesRepository) : ViewModel() {

    val categoriesLiveData = topHeadlinesRepository.categoriesLiveData

    init {
        viewModelScope.launch {
            topHeadlinesRepository.init()
        }
    }
}
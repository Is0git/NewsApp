package com.is0git.newsapp.vm.sources

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

open class SourcesViewModel @ViewModelInject constructor(private val sourcesRepository: SourcesRepository) :
    ViewModel() {

    init {
        getNewsSources()
    }

    @Inject
    lateinit var savedStateHandle: SavedStateHandle
    val newSourcesLiveData = sourcesRepository.getCachedLiveData()

    private fun getNewsSources() {
        viewModelScope.launch(Dispatchers.IO) {
            sourcesRepository.getNewsSources()
        }
    }
}
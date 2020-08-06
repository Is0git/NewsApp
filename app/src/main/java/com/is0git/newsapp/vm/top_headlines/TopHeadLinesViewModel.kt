package com.is0git.newsapp.vm.top_headlines

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.is0git.newsapp.ui.fragments.top_headlines_fragment.HeadlinesFragment
import com.is0git.newsapp.vm.single_job.DefaultSingleJobViewModel
import com.is0git.newsapp.vm.top_headlines.TopHeadlinesRepository.Companion.DEFAULT_PAGE_SIZE
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TopHeadLinesViewModel @ViewModelInject constructor(
    val topHeadlinesRepository: TopHeadlinesRepository,
    @Assisted val savedStateHandle: SavedStateHandle
) : DefaultSingleJobViewModel(topHeadlinesRepository) {

    val categoriesLiveData = topHeadlinesRepository.categoriesLiveData
    val allArticlesLiveData = topHeadlinesRepository.allArticles
    val countryLiveData = topHeadlinesRepository.country
    val bottomSheetStateLiveData = savedStateHandle.getLiveData<Int>(BOTTOM_SHEET_STATE)

    init {
        getCategories(
            *HeadlinesFragment.categories,
            pageSize = DEFAULT_PAGE_SIZE,
            country = topHeadlinesRepository.countryCode
        )
    }

    fun getCategories(vararg categories: String, pageSize: Int, country: String? = null) {
        viewModelScope.launch {
            topHeadlinesRepository.getCategories(
                categories = *categories,
                pageSize = pageSize,
                country = country
            )
        }
    }

    // data keys for restoring after process death or config changes
    companion object {
        const val TAB_POSITION = "TabPosition"
        const val COUNTRY_CHIP_POSITION = "CountryChipPosition"
        const val BOTTOM_SHEET_STATE = "BottomSheetState"
    }
}
package com.is0git.newsapp.ui.fragments.sources

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.SourcesFragmentLayoutBinding
import com.is0git.newsapp.network.models.sources.SourcesItem
import com.is0git.newsapp.ui.fragments.BaseFragment
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SourcesFragment :
    BaseFragment<SourcesFragmentLayoutBinding>(R.layout.sources_fragment_layout) {

    private val sourceViewModel: TopHeadLinesViewModel by viewModels()

    override fun observeData() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildCategories()
    }

    private fun buildCategories() {
        binding.categoryLayout.apply {
            val businessCategory = Category.Builder<SourcesItem>()
                .enabledViewAll()
                .setId(CATEGORY_BUSINESS)
                .setTitle(CATEGORY_BUSINESS)
                .build()
//            val entertainmentCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_ENTERTAINMENT)
//                .setTitle(CATEGORY_ENTERTAINMENT)
//                .build()
//            val generalCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_GENERAL)
//                .setTitle(CATEGORY_GENERAL)
//                .build()
//            val healthCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_HEALTH)
//                .setTitle(CATEGORY_HEALTH)
//                .build()
//            val scienceCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_SCIENCE)
//                .setTitle(CATEGORY_SCIENCE)
//                .build()
//            val sportsCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_SPORTS)
//                .setTitle(CATEGORY_SPORTS)
//                .build()
//            val technologyCategory = Category.Builder<SourcesItem>()
//                .enabledViewAll()
//                .setId(CATEGORY_TECHNOLOGY)
//                .setTitle(CATEGORY_TECHNOLOGY)
//                .build()
            val listOfCategories = mutableListOf(
                businessCategory
//                entertainmentCategory,
//                generalCategory,
//                healthCategory,
//                scienceCategory,
//                sportsCategory,
//                technologyCategory
            )
            addCategories(listOfCategories)
        }
    }

    companion object {
        const val CATEGORY_BUSINESS = "Business"
        const val CATEGORY_ENTERTAINMENT = "Entertainment"
        const val CATEGORY_GENERAL = "General"
        const val CATEGORY_HEALTH = "Health"
        const val CATEGORY_SCIENCE = "Science"
        const val CATEGORY_SPORTS = "Sports"
        const val CATEGORY_TECHNOLOGY = "Technology"
    }
}
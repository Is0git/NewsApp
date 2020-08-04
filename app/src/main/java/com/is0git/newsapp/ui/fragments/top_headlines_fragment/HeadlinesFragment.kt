package com.is0git.newsapp.ui.fragments.top_headlines_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.adapters.CategoryListAdapter
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_data.Category.Companion.FLAG_HORIZONTAL
import com.is0git.multicategorylayout.databinding.ListItemOneBinding
import com.is0git.multicategorylayout.databinding.ListItemTwoBinding
import com.is0git.multicategorylayout.listeners.OnCategoryListener
import com.is0git.multicategorylayout.ui.category_layout.view_holders.HorizontalListViewHolder
import com.is0git.multicategorylayout.ui.category_layout.view_holders.VerticalListViewHolder
import com.is0git.multicategorylayout.ui.tab_layout_integration.OnCategoryTabListener
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.HeadlinesFragmentLayoutBinding
import com.is0git.newsapp.network.models.common.ArticlesItem
import com.is0git.newsapp.ui.fragments.BaseFragment
import com.is0git.newsapp.ui.fragments.sources.SourcesFragment
import com.is0git.newsapp.ui.views.filter_card.Filter
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener
import com.is0git.newsapp.utils.JobState
import com.is0git.newsapp.utils.loadImageWith
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel
import com.is0git.newsapp.vm.top_headlines.TopHeadlinesRepository.Companion.DEFAULT_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HeadlinesFragment :
    BaseFragment<HeadlinesFragmentLayoutBinding>(R.layout.headlines_fragment_layout),
    OnFilterCheckedListener {

    private val topHeadLinesViewModel: TopHeadLinesViewModel by this.navGraphViewModels(R.id.main_nav) {defaultViewModelProviderFactory}
    lateinit var allListAdapter: CategoryListAdapter<ArticlesItem, VerticalListViewHolder>
    lateinit var behavior: BottomSheetBehavior<View>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        doNetworkWork()
        super.onViewCreated(view, savedInstanceState)
        buildCategories()
        setupBottomSheet()
        addHeadlinesFilters()
    }

    override fun observeData() {
        topHeadLinesViewModel.categoriesLiveData.forEach {
            it.observe(viewLifecycleOwner) { articles ->
                if (articles.count() > 0)
                    binding.categoryLayout.updateAdapter(articles.first().category!!, articles)
            }
        }
        topHeadLinesViewModel.allArticlesLiveData.observe(viewLifecycleOwner) {
            Log.d("TEST", "${it.count()}")
            allListAdapter.submitList(it)
        }
        topHeadLinesViewModel.jobStatesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is JobState.JobStarted -> {
                    Log.d("jobState", "started")
                    progressDialog.show()
                }
                is JobState.JobFailed -> Log.d("jobState", "failed: ${it.throwable}")
                is JobState.JobCompleted -> {
                    view?.postDelayed({
                        progressDialog.dismiss()
                    }, 1000)
                }
            }
        }
    }

    private fun doNetworkWork() {
        topHeadLinesViewModel.getCategories(
            *categories,
            pageSize = DEFAULT_PAGE_SIZE,
            country = "fr"
        )
    }

    private fun setupBottomSheet() {
        behavior = BottomSheetBehavior.from(binding.filterCard)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

        })
        binding.filtersButton.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun addHeadlinesFilters() {
        val countries = resources.getStringArray(R.array.countries)
        val filter = Filter.create("Country", countries)
        binding.filterCard.apply {
            setOnFilterCheckedListener(this@HeadlinesFragment)
            addFilter(filter)
        }
    }

    private fun buildCategories() {
        val businessAdapter = createVerticalPositionAdapter()
        binding.categoryLayout.apply {
            val businessCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_BUSINESS)
                .setTitle(CATEGORY_BUSINESS)
                .setAdapter(businessAdapter)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .build()
            val entertainmentCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_ENTERTAINMENT)
                .setTitle(CATEGORY_ENTERTAINMENT)
                .setAdapter(createHorizontalPositionAdapter())
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .build()
            val generalCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_GENERAL)
                .setTitle(CATEGORY_GENERAL)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .setAdapter(createVerticalPositionAdapter())
                .build()
            val healthCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_HEALTH)
                .setAdapter(createHorizontalPositionAdapter())
                .setTitle(CATEGORY_HEALTH)
                .addDivider(R.color.dividerColor)
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .build()
            val scienceCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_SCIENCE)
                .setTitle(CATEGORY_SCIENCE)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .setAdapter(createVerticalPositionAdapter())
                .build()
            val sportsCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_SPORTS)
                .setTitle(CATEGORY_SPORTS)
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .setAdapter(createHorizontalPositionAdapter())
                .build()
            val technologyCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll(::onViewAllClick)
                .setId(CATEGORY_TECHNOLOGY)
                .setTitle(CATEGORY_TECHNOLOGY)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .setAdapter(createVerticalPositionAdapter())
                .build()
            val listOfCategories = mutableListOf(
                businessCategory,
                entertainmentCategory,
                generalCategory,
                healthCategory,
                sportsCategory

            )
            allListAdapter = createVerticalPositionAdapter()
            setupWithTabLayout(binding.categoryTabLayout, allListAdapter)
            addCategories(listOfCategories, viewLifecycleOwner)
            setOnCategoryTabListener(object : OnCategoryTabListener {
                override fun onTabAdded(
                    tab: TabLayout.Tab,
                    category: Category<*>,
                    position: Int
                ) {
                    Log.d("TESTY", "Added: $position")
                }

                override fun onTabRemoved(category: Category<*>, position: Int) {
                    Log.d("TESTY", "Removed: $position")
                }

                override fun onTabUpdated(
                    tab: TabLayout.Tab,
                    category: Category<*>,
                    position: Int
                ) {
                    Log.d("TESTY", "UPDATED: $position")
                }


            })
            setOnCategoryListener(
                object : OnCategoryListener {
                    override fun onCategoryChanged(category: Category<*>, position: Int) {
                        Log.d("TESTY", "Added2: $position")
                    }

                    override fun onCategoryRemoved(category: Category<*>, position: Int) {
                        Log.d("TESTY", "Removed2: $position")
                    }

                    override fun onCategoryAdded(category: Category<*>, position: Int) {
                        Log.d("TESTY", "UPDATED2: $position")
                    }

                }
            )
            onTabSelectedListener {
                binding.appBar.setExpanded(false)
            }
        }

    }

    private fun createVerticalPositionAdapter(): CategoryListAdapter<ArticlesItem, VerticalListViewHolder> {
        return CategoryListAdapter(
            ArticlesItem.comparator,
            { holder, item, position ->
                holder.binding.descriptionText.text = item.description
                holder.binding.headerText.text = item.author
                holder.binding.headImage.loadImageWith(item.urlToImage)
            },
            { parent, _ ->
                val binding =
                    ListItemOneBinding.inflate(LayoutInflater.from(context), parent, false)
                VerticalListViewHolder(binding)
            }
        )
    }

    private fun createHorizontalPositionAdapter(): CategoryListAdapter<ArticlesItem, HorizontalListViewHolder> {
        return CategoryListAdapter(
            ArticlesItem.comparator,
            { holder, item, position ->
                holder.binding.descriptionText.text = item.description
                holder.binding.headerText.text = item.author
                holder.binding.headImage.loadImageWith(item.urlToImage)
            },
            { parent, _ ->
                val binding =
                    ListItemTwoBinding.inflate(LayoutInflater.from(context), parent, false)
                HorizontalListViewHolder(binding)
            }
        )
    }

    override fun onFilterSelectionCheck(chip: Chip?, filter: Filter) {
        if (chip != null) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            topHeadLinesViewModel.topHeadlinesRepository.country.value = chip.text.toString()
            topHeadLinesViewModel.getCategories(
                *categories,
                pageSize = DEFAULT_PAGE_SIZE,
                country = chip.text.toString()
            )
        }
    }

    private fun onViewAllClick(category: Category<*>) {
        val directions = HeadlinesFragmentDirections.actionTopHeadlinesFragmentToCategoryViewAllFragment(category.id)
        findNavController().navigate(directions)
    }

    companion object {
        val categories: Array<out String> = arrayOf(
            SourcesFragment.CATEGORY_BUSINESS,
            SourcesFragment.CATEGORY_ENTERTAINMENT,
            SourcesFragment.CATEGORY_GENERAL,
            SourcesFragment.CATEGORY_HEALTH,
            SourcesFragment.CATEGORY_SCIENCE,
            SourcesFragment.CATEGORY_SPORTS,
            SourcesFragment.CATEGORY_TECHNOLOGY
        )
        const val CATEGORY_BUSINESS = "Business"
        const val CATEGORY_ENTERTAINMENT = "Entertainment"
        const val CATEGORY_GENERAL = "General"
        const val CATEGORY_HEALTH = "Health"
        const val CATEGORY_SCIENCE = "Science"
        const val CATEGORY_SPORTS = "Sports"
        const val CATEGORY_TECHNOLOGY = "Technology"
    }
}
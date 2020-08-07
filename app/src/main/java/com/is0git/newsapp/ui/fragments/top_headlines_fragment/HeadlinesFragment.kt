package com.is0git.newsapp.ui.fragments.top_headlines_fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.is0git.multicategorylayout.adapters.CategoryListAdapter
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_data.Category.Companion.FLAG_GRID
import com.is0git.multicategorylayout.category_data.Category.Companion.FLAG_HORIZONTAL
import com.is0git.multicategorylayout.databinding.ListItemOneBinding
import com.is0git.multicategorylayout.databinding.ListItemTwoBinding
import com.is0git.multicategorylayout.ui.category_layout.view_holders.HorizontalListViewHolder
import com.is0git.multicategorylayout.ui.category_layout.view_holders.VerticalListViewHolder
import com.is0git.newsapp.R
import com.is0git.newsapp.databinding.HeadlinesFragmentLayoutBinding
import com.is0git.newsapp.di.modules.SharedPreferencesModule.DARK_MODE_KEY
import com.is0git.newsapp.models.common.ArticlesItem
import com.is0git.newsapp.ui.fragments.BaseFragment
import com.is0git.newsapp.ui.views.filter_card.Filter
import com.is0git.newsapp.ui.views.filter_card.FilterMaterialCard
import com.is0git.newsapp.ui.views.filter_card.listeners.OnFilterCheckedListener
import com.is0git.newsapp.utils.*
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel.Companion.COUNTRY_CHIP_POSITION
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel.Companion.TAB_POSITION
import com.is0git.newsapp.vm.top_headlines.TopHeadlinesRepository.Companion.DEFAULT_PAGE_SIZE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class HeadlinesFragment :
    BaseFragment<HeadlinesFragmentLayoutBinding>(R.layout.headlines_fragment_layout),
    OnFilterCheckedListener {

    private val topHeadLinesViewModel: TopHeadLinesViewModel by this.navGraphViewModels(R.id.main_nav) { defaultViewModelProviderFactory }
    lateinit var allListAdapter: CategoryListAdapter<ArticlesItem, VerticalListViewHolder>
    lateinit var behavior: BottomSheetBehavior<View>

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @Inject
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        restoreFilter()
        setupBottomSheet()
        super.onViewCreated(view, savedInstanceState)
        buildCategoryLayout()
        addHeadlinesFilters()
        setActions()
    }

    override fun observeData() {
        topHeadLinesViewModel.categoriesLiveData.forEach {
            it.observe(viewLifecycleOwner) { articles ->
                if (articles.count() > 0)
                    binding.categoryLayout.updateAdapter(articles.first().category!!, articles)
            }
        }
        topHeadLinesViewModel.allArticlesLiveData.observe(viewLifecycleOwner) {
            allListAdapter.submitList(it)
            requireView().postDelayed({
                binding.categoryLayout.getAllList()?.smoothScrollToPosition(0)
            }, 1000)
        }
        topHeadLinesViewModel.jobStatesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is JobState.JobStarted -> {
                    showProgress()
                }
                is JobState.JobFailed -> Log.d(HEADLINE_FRAGMENT_TAG, "job failed: ${it.throwable}")
                is JobState.JobCompleted -> {
                    hideProgress()
                    topHeadLinesViewModel.repo.onJobIdle()
                }
                else -> Log.i(HEADLINE_FRAGMENT_TAG, "job state is idle")
            }
        }
        topHeadLinesViewModel.bottomSheetStateLiveData.observe(viewLifecycleOwner) {
            behavior.state = it
        }
    }

    private fun setupBottomSheet() {
        behavior = BottomSheetBehavior.from(binding.filterCard)
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                topHeadLinesViewModel.bottomSheetStateLiveData.value = newState
            }

        })
        binding.filtersButton.setOnClickListener {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun addHeadlinesFilters() {
        val countries = resources.getStringArray(R.array.countries)
        val filter = Filter.create(getString(R.string.country_filter_title), countries)
        binding.filterCard.apply {
            setOnFilterCheckedListener(this@HeadlinesFragment)
            addFilter(filter)
        }
    }

    private fun restoreFilter() {
        val selectedCountryChipKey =
            topHeadLinesViewModel.savedStateHandle.get<Int>(COUNTRY_CHIP_POSITION)
        if (selectedCountryChipKey != null) {
            binding.filterCard.selectChip(
                getString(R.string.country_filter_title),
                selectedCountryChipKey
            )
        }
    }

    private fun showProgress() {
        binding.progressBar.isVisible = true
        binding.hideView.isVisible = true
    }

    private fun hideProgress() {
        binding.progressBar.isVisible = false
        binding.hideView.isVisible = false
    }

    private fun buildCategoryLayout() {
        binding.categoryLayout.apply {
            val businessCategory = createHeadlineFragmentCategory(
                CATEGORY_BUSINESS,
                createVerticalPositionAdapter(),
                FLAG_HORIZONTAL
            )
            val entertainmentCategory = createHeadlineFragmentCategory(
                CATEGORY_ENTERTAINMENT,
                createHorizontalPositionAdapter(),
                FLAG_HORIZONTAL or FLAG_GRID
            )
            val generalCategory = createHeadlineFragmentCategory(
                CATEGORY_GENERAL,
                createVerticalPositionAdapter(),
                FLAG_HORIZONTAL
            )
            val healthCategory = createHeadlineFragmentCategory(
                CATEGORY_HEALTH,
                createHorizontalPositionAdapter(),
                FLAG_HORIZONTAL or FLAG_GRID
            )
            val scienceCategory = createHeadlineFragmentCategory(
                CATEGORY_SCIENCE,
                createVerticalPositionAdapter(),
                FLAG_HORIZONTAL
            )
            val sportsCategory = createHeadlineFragmentCategory(
                CATEGORY_SPORTS,
                createHorizontalPositionAdapter(),
                FLAG_HORIZONTAL or FLAG_GRID
            )
            val technologyCategory = createHeadlineFragmentCategory(
                CATEGORY_TECHNOLOGY,
                createVerticalPositionAdapter(),
                FLAG_HORIZONTAL
            )
            val listOfCategories = mutableListOf(
                businessCategory,
                entertainmentCategory,
                generalCategory,
                healthCategory,
                scienceCategory,
                sportsCategory,
                technologyCategory

            )
            allListAdapter = createVerticalPositionAdapter()
            setupWithTabLayout(binding.categoryTabLayout, allListAdapter)
            addCategories(listOfCategories, viewLifecycleOwner)
            onTabSelectedListener { _, position ->
                binding.appBar.setExpanded(false)
                topHeadLinesViewModel.savedStateHandle.set(TAB_POSITION, position)
            }
        }
        hideProgress()
    }

    private fun setActions() {
        binding.nightModeButton.setOnClickListener {
            resolveThemeMode()
        }
        binding.refreshButton
            .clicks { playRefreshAnimation(it) }
            .buffer(Channel.RENDEZVOUS)
            .onEach {
                val country = topHeadLinesViewModel.countryLiveData.value
                if (country != null) initNewCategoriesData(country)
            }
            .launchIn(lifecycleScope)
    }

    private fun playRefreshAnimation(view: View) {
        val rotation = if (view.rotation == 360f) 0f else 360f
        view.animate().rotation(rotation).start()
    }

    private fun createHeadlineFragmentCategory(
        id: String,
        adapter: CategoryListAdapter<ArticlesItem, out RecyclerView.ViewHolder>,
        flags: Int
    ): Category<ArticlesItem> {
        return Category.Builder<ArticlesItem>()
            .enabledViewAll(::onViewAllClick)
            .setId(id)
            .setTitle(id)
            .setAdapter(adapter)
            .addFlags(flags)
            .addDivider(R.color.dividerColor)
            .build()
    }

    private fun navigateToViewAll(articlesItem: ArticlesItem) {
        val directions =
            HeadlinesFragmentDirections.actionTopHeadlinesFragmentToFullNewsFragment(articlesItem)
        findNavController().navigate(directions)
    }

    private fun resolveThemeMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                sharedPreferencesEditor.putBoolean(DARK_MODE_KEY, true)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                sharedPreferencesEditor.putBoolean(DARK_MODE_KEY, false)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            } // Night mode is active, we're using dark theme
        }
        sharedPreferencesEditor.apply()
    }

    override fun onFilterSelectionCheck(
        chip: Chip?,
        filterView: FilterMaterialCard.FilterView,
        position: Int?
    ) {
        if (chip != null) {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            if (filterView.filter.displayName == getString(R.string.country_filter_title)) {
                topHeadLinesViewModel.savedStateHandle.set(
                    COUNTRY_CHIP_POSITION,
                    position
                )
                initNewCategoriesData(chip.text.toString())
            }
        }
    }

    private fun initNewCategoriesData(country: String) {
        topHeadLinesViewModel.topHeadlinesRepository.country.value = country
        topHeadLinesViewModel.getCategories(
            *categories,
            pageSize = DEFAULT_PAGE_SIZE,
            country = country
        )
    }

    private fun startBrowseIntent(articlesItem: ArticlesItem) {
        requireActivity().startBrowserCheckedConnection(articlesItem)
    }

    private fun restoreViews() {
        topHeadLinesViewModel.savedStateHandle.apply {
            val position = get<Int>(TAB_POSITION)
            if (position != null) {
                binding.categoryLayout.post {
                    binding.categoryLayout.selectTabByPosition(position)
                }
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        restoreViews()
    }

    private fun onViewAllClick(category: Category<*>, transitionView: View?) {
        val extras = FragmentNavigatorExtras(transitionView!! to "categoryNameTransition")
        val directions =
            HeadlinesFragmentDirections.actionTopHeadlinesFragmentToCategoryViewAllFragment(category.id)
        findNavController().navigate(directions, extras)
    }

    private fun createVerticalPositionAdapter(): CategoryListAdapter<ArticlesItem, VerticalListViewHolder> {
        return CategoryListAdapter(
            ArticlesItem.comparator,
            { holder, item, _ ->
                val stringNull = getString(android.R.string.untitled)
                holder.binding.apply {
                    descriptionText.text = item.description ?: stringNull
                    headerText.text = item.author ?: stringNull
                    headImage.loadImageWith(item.urlToImage)
                    sourceText.text = item.source?.name
                    dateText.text = DateUtils.formatDate(item.publishedAt)
                }
                holder.binding.linkButton.setOnClickListener {
                    startBrowseIntent(item)
                }
                holder.itemView.setOnClickListener {
                    navigateToViewAll(item)
                }
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
            { holder, item, _ ->
                val stringNull = getString(android.R.string.untitled)
                holder.binding.apply {
                    descriptionText.text = item.description ?: stringNull
                    headerText.text = item.author ?: stringNull
                    headImage.loadImageWith(item.urlToImage)
                    sourceText.text = item.source?.name
                    dateText.text = DateUtils.formatDate(item.publishedAt)
                }
                holder.binding.linkButton.setOnClickListener {
                    startBrowseIntent(item)
                }
                holder.itemView.setOnClickListener {
                    navigateToViewAll(item)
                }
            },
            { parent, _ ->
                val binding =
                    ListItemTwoBinding.inflate(LayoutInflater.from(context), parent, false)
                HorizontalListViewHolder(binding)
            }
        )
    }

    companion object {
        const val CATEGORY_BUSINESS = "Business"
        const val CATEGORY_ENTERTAINMENT = "Entertainment"
        const val CATEGORY_GENERAL = "General"
        const val CATEGORY_HEALTH = "Health"
        const val CATEGORY_SCIENCE = "Science"
        const val CATEGORY_SPORTS = "Sports"
        const val CATEGORY_TECHNOLOGY = "Technology"

        val categories: Array<out String> = arrayOf(
            CATEGORY_BUSINESS,
            CATEGORY_ENTERTAINMENT,
            CATEGORY_GENERAL,
            CATEGORY_HEALTH,
            CATEGORY_SCIENCE,
            CATEGORY_SPORTS,
            CATEGORY_TECHNOLOGY
        )

        fun Activity.startBrowserCheckedConnection(articlesItem: ArticlesItem) {
            val connection = showConnectionSnackBar()
            if (!connection) return
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articlesItem.url))
            if (intent.resolveActivity(this.packageManager) != null) startActivity(intent)
            else Toast.makeText(this, this.getString(R.string.no_browser), Toast.LENGTH_SHORT)
                .show()
        }
        const val HEADLINE_FRAGMENT_TAG = "HeadlineFragmentTag"
    }
}
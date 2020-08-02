package com.is0git.newsapp.ui.fragments.top_headlines_fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
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
import com.is0git.newsapp.utils.loadImageWith
import com.is0git.newsapp.vm.top_headlines.TopHeadLinesViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HeadlinesFragment : BaseFragment<HeadlinesFragmentLayoutBinding>(R.layout.headlines_fragment_layout) {

    private val topHeadLinesViewModel: TopHeadLinesViewModel by viewModels()

    override fun observeData() {
        topHeadLinesViewModel.categoriesLiveData.forEach {
            it.observe(viewLifecycleOwner) {
                Log.d("TEST", "${it.count()}")
                if (it.count() > 0)
                binding.categoryLayout.updateAdapter<ArticlesItem>(it.first().category!!, it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buildCategories()
    }

    private fun buildCategories() {
        val businessAdapter =  createVerticalPositionAdapter()
        binding.categoryLayout.apply {
            val businessCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_BUSINESS)
                .setTitle(CATEGORY_BUSINESS)
                .setAdapter(businessAdapter)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .build()
            val entertainmentCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_ENTERTAINMENT)
                .setTitle(CATEGORY_ENTERTAINMENT)
                .setAdapter(createHorizontalPositionAdapter())
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .build()
            val generalCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_GENERAL)
                .setTitle(CATEGORY_GENERAL)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .setAdapter(createVerticalPositionAdapter())
                .build()
            val healthCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_HEALTH)
                .setAdapter(createHorizontalPositionAdapter())
                .setTitle(CATEGORY_HEALTH)
                .addDivider(R.color.dividerColor)
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .build()
            val scienceCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_SCIENCE)
                .setTitle(CATEGORY_SCIENCE)
                .addFlags(FLAG_HORIZONTAL)
                .addDivider(R.color.dividerColor)
                .setAdapter(createVerticalPositionAdapter())
                .build()
            val sportsCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
                .setId(CATEGORY_SPORTS)
                .setTitle(CATEGORY_SPORTS)
                .addFlags(Category.FLAG_GRID or FLAG_HORIZONTAL)
                .setAdapter(createHorizontalPositionAdapter())
                .build()
            val technologyCategory = Category.Builder<ArticlesItem>()
                .enabledViewAll()
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
                sportsCategory,
                technologyCategory
            )
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
            setupWithTabLayout(binding.categoryTabLayout, createVerticalPositionAdapter())
            onTabSelectedListener {
                binding.appBar.setExpanded(false)
            }
            lifecycleScope.launch {
                delay(1000)
                removeCategoryAt(2)
            }
        }

    }

    private fun createVerticalPositionAdapter() : CategoryListAdapter<ArticlesItem, VerticalListViewHolder> {
        return CategoryListAdapter(
            ArticlesItem.comparator,
            { holder, item,  position ->
                holder.binding.descriptionText.text = item.description
                holder.binding.headerText.text = item.author
                holder.binding.headImage.loadImageWith(item.urlToImage)
            },
            { parent, _ ->
                val binding = ListItemOneBinding.inflate(LayoutInflater.from(context), parent, false)
                VerticalListViewHolder(binding)
            }
        )
    }

    private fun createHorizontalPositionAdapter() : CategoryListAdapter<ArticlesItem, HorizontalListViewHolder> {
        return CategoryListAdapter(
            ArticlesItem.comparator,
            { holder, item,  position ->
                holder.binding.descriptionText.text = item.description
                holder.binding.headerText.text = item.author
                holder.binding.headImage.loadImageWith(item.urlToImage)
            },
            { parent, _ ->
                val binding = ListItemTwoBinding.inflate(LayoutInflater.from(context), parent, false)
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
    }
}
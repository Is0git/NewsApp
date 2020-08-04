package com.is0git.multicategorylayout.ui.tab_layout_integration

import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.R
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.CategoryTabFactory

class CategoryTabLayoutManager(tabLayout: TabLayout, categoryTabFactory: CategoryTabFactory) :
    TabLayoutManager(tabLayout, categoryTabFactory) {

    var allTabListener: AllTabListener? = null

    init {
        tabLayout.removeAllTabs()
    }

    override fun setupWithCategoryView(
        tabLayout: TabLayout,
        category: List<Category<*>>,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        super.setupWithCategoryView(tabLayout, category, listAdapter)
        if (isAllEnabled) {
            val title: String
            val id: String
            tabLayout.resources.apply {
                title = getString(R.string.all)
                id = getString(R.string.all_tab)
            }
            val allCategory = Category.Builder<Any>()
                .setTitle(title)
                .setId(id)
                .setAdapter(listAdapter!!)
                .build()

            val allTab = tabFactory.createTab(tabLayout, allCategory, R.drawable.ic_all)
            allTab.view.id = R.id.all_tab
            addTab(allTab, allCategory, 0)
            allTabListener?.onAllTabAdded(allTab)
        }
    }

    interface AllTabListener {
        fun onAllTabAdded(tab: TabLayout.Tab)
    }
}
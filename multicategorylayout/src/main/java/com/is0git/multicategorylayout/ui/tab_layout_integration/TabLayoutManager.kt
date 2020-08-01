package com.is0git.multicategorylayout.ui.tab_layout_integration

import android.view.View
import androidx.core.view.children
import androidx.core.view.get
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.TabFactory
import kotlinx.coroutines.*
import java.lang.IllegalStateException

abstract class TabLayoutManager(
    var tabLayout: TabLayout,
    protected val tabFactory: TabFactory<Category<*>>,
    protected var tabManagerListener: TabManagerListener? = null
) {

    var isAllEnabled = false
    private var tabUpdateListener: ((TabLayout.Tab, Category<*>) -> Boolean)? = null
    lateinit var updateTabLayoutJob: Job

    open suspend fun setupWithCategoryView(
        tabLayout: TabLayout,
        category: Map<String, Category<*>>,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        if (this.tabLayout != tabLayout) throw IllegalStateException("you have to pass initial TabLayout")
        this.isAllEnabled = listAdapter != null
        withContext(Dispatchers.Main) {
            for ((_, c) in category) {
                val tab = tabFactory.createTab(tabLayout, c, null)
                addTab(tab, c)
            }
        }
    }

    fun addTab(tab: TabLayout.Tab, category: Category<*>, position: Int? = null) {
        if (position != null) tabLayout.addTab(tab, position) else tabLayout.addTab(tab)
        tabManagerListener?.onTabAdded(tab, category)
    }

    fun setOnTabUpdateListener(action: (tab: TabLayout.Tab, category: Category<*>) -> Boolean) {
        tabUpdateListener = action
    }

    suspend fun updateTabs(category: Collection<Category<*>>) {
        updateTabLayoutJob = CoroutineScope(Dispatchers.Default).launch {
            if (category.count() > 0) {
                val categoryHashMap = HashMap<String, Category<*>>(category.size)
                for (c in category) {
                    categoryHashMap[c.id] = c
                }
                for (i in 0 until tabLayout.children.count()) {
                    val tabKey = (tabLayout.getTabAt(i) as TabLayout.Tab).text
                    if (categoryHashMap.containsKey(tabKey)) {
                        categoryHashMap.remove(tabKey)
                    } else {
                        tabLayout.removeTabAt(i)
                    }
                }
                withContext(Dispatchers.Main) {
                    for ((s, c) in categoryHashMap) {
                        val tab = tabFactory.createTab(tabLayout, c, null)
                        addTab(tab, c)
                    }
                }
            }
        }
    }

    fun clear() {
        updateTabLayoutJob.cancel()
    }
}
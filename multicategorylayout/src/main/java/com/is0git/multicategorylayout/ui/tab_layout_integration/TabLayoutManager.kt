package com.is0git.multicategorylayout.ui.tab_layout_integration

import androidx.core.view.children
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.TabFactory
import kotlinx.coroutines.*
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.set

abstract class TabLayoutManager(
    var tabLayout: TabLayout,
    protected val tabFactory: TabFactory,
    var tabManagerListener: TabManagerListener? = null
) : TabLayout.OnTabSelectedListener,
    TabController {

    var isAllEnabled = false
    private var tabUpdateListener: ((TabLayout.Tab, Category<*>) -> Boolean)? = null
    private var onTabSelectedListener: ((TabLayout.Tab, key: String) -> Unit)? = null
    private var updateTabLayoutJob: Job? = null
    lateinit var onTabSelect: () -> Unit

    open fun setupWithCategoryView(
        tabLayout: TabLayout,
        category: List<Category<*>>,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        if (this.tabLayout != tabLayout) throw IllegalStateException("you have to pass initial TabLayout")
        this.isAllEnabled = listAdapter != null
        tabLayout.addOnTabSelectedListener(this@TabLayoutManager)
    }

    fun addTab(tab: TabLayout.Tab, category: Category<*>, position: Int? = null) {
        tab.view.id = category.categoryViewId
        if (position != null) {
            if (tabLayout.tabCount - 1 > position) tabLayout.addTab(tab, position) else tabLayout.addTab(tab)
            tabManagerListener?.tabAdded(tab, category, position)
        } else {
            tabLayout.addTab(tab)
            tabManagerListener?.tabAdded(tab, category, tabLayout.tabCount - 1)
        }
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
                    for ((_, c) in categoryHashMap) {
                        val tab = tabFactory.createTab(tabLayout, c, null)
                        addTab(tab, c)
                    }
                }
            }
        }
    }

    override fun addTab(category: Category<*>, position: Int) {
        val tab = tabFactory.createTab(tabLayout, category, null)
        addTab(tab, category, position)
    }

    override fun removeTab(category: Category<*>, position: Int) {
        val mPosition = if (isAllEnabled) position + 1 else position
        tabLayout.removeTabAt(mPosition)
        tabManagerListener?.tabRemoved(category, mPosition)
    }

    override fun updateTab(category: Category<*>, position: Int) {
        TODO("yet to implement")
    }

    fun setOnTabSelectedListener(listener: ((TabLayout.Tab, key: String) -> Unit)?) {
        onTabSelectedListener = listener
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
            onTabSelectedListener?.invoke(tab, tab.text.toString())
        }

    }

    fun clear() {
        updateTabLayoutJob?.cancel()
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {}
    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    companion object {
        const val FLAG_SCROLL_TAB = 0
    }
}
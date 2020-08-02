package com.is0git.multicategorylayout.category_manager

import android.annotation.SuppressLint
import android.graphics.Rect
import android.util.Log
import android.view.ViewGroup
import androidx.core.util.forEach
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.R
import com.is0git.multicategorylayout.category_controller.CategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_manager.adapter.AdapterController
import com.is0git.multicategorylayout.category_modifier.CategoryModifier
import com.is0git.multicategorylayout.listeners.CategoryListener
import com.is0git.multicategorylayout.listeners.OnCategoryListener
import com.is0git.multicategorylayout.scroll_listener.CategoryScrollView
import com.is0git.multicategorylayout.ui.tab_layout_integration.CategoryTabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.OnCategoryTabListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabManagerListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.CategoryTabFactory
import com.is0git.multicategorylayout.ui.ui_manager.CategoryUIManager
import com.is0git.multicategorylayout.ui.ui_manager.CategoryView
import com.is0git.multicategorylayout.ui.ui_manager.UIManagerListener
import kotlinx.coroutines.launch
import kotlin.math.abs

class CategoryManager(
    var viewGroup: ViewGroup,
    private val categoryModifier: CategoryModifier,
    private val categoryController: CategoryController
) : CategoryListener,
    AdapterController,
    UIManagerListener,
    TabManagerListener{
    val categories: MutableList<Category<*>> = mutableListOf()
    var uiManager = CategoryUIManager(viewGroup)
    var onCategoryListener: OnCategoryListener? = null
    var tabLayoutManager: TabLayoutManager? = null
    var onCategoryTabListener: OnCategoryTabListener? = null
    var lifecycleOwner: LifecycleOwner? = null

    init {
        uiManager.uiManagerListener = this
    }

    fun addCategories(categories: List<Category<*>>) {
        lifecycleOwner?.lifecycleScope?.launch {
            categories.forEachIndexed { pos, category ->
                this@CategoryManager.categories.add(category)
                categoryAdded(category, pos)
            }
        }
    }

    fun addCategory(category: Category<*>, position: Int) {
        categoryModifier.modifyCategory(category)
        if (position >= 0 && position < categories.size) {
            categories.add(position, category)
            categoryAdded(category, position)
            onCategoryListener?.onCategoryAdded(category, position)
        }
    }

    fun removeCategory(position: Int) {
        val category = categories[position]
        categories.remove(category)
        categoryRemoved(category, position)
        onCategoryListener?.onCategoryRemoved(category, position)
    }

    fun removeAll() {
        for (i in categories.indices) {
            removeCategory(0)
        }
    }

    fun updateCategory(position: Int) {
        val category = categories[position]
        if (categories.contains(category)) throw IllegalStateException("category not found")
        categoryChanged(category, position)
        onCategoryListener?.onCategoryChanged(category, position)
    }

    //not implemented yet
    fun resetCategories() {
        categoryController.resetCategories(categories)
    }

    //not implemented yet
    fun updateCategories() {
        categoryController.updateCategories(categories)
    }

    @Suppress("unchecked_cast")
    override fun updateCategoryAdapter(categoryId: String, list: List<*>) {
        val category = categories.find { it.id == categoryId }
        if (category == null) return
        else {
            category.adapter.submitList(list as List<Nothing>)
        }
    }

    fun getCount(): Int {
        return categories.size
    }

    override fun categoryViewCreated(categoryView: CategoryView, position: Int) {
        tabLayoutManager?.addTab(categoryView.category, position)
    }

    override fun categoryViewRemoved(categoryView: CategoryView, position: Int) {
        tabLayoutManager?.removeTab(categoryView.category, position)
    }

    override fun categoryViewUpdated(categoryView: CategoryView, position: Int) {
        tabLayoutManager?.removeTab(categoryView.category, position)
    }

    override fun categoryChanged(category: Category<*>, position: Int) {
        uiManager.updateCategoryView(category, position)
        onCategoryListener?.onCategoryChanged(category, position)
    }

    override fun categoryRemoved(category: Category<*>, position: Int) {
        uiManager.removeCategoryView(category, position)
        onCategoryListener?.onCategoryRemoved(category, position)
    }

    override fun categoryAdded(category: Category<*>, position: Int) {
        uiManager.createCategoryView(category, position)
        onCategoryListener?.onCategoryAdded(category, position)
    }

    override fun tabAdded(
        tab: TabLayout.Tab,
        category: Category<*>,
        position: Int
    ) {
        onCategoryTabListener?.onTabAdded(tab, category, position)
    }

    override fun tabRemoved(category: Category<*>, position: Int) {
        onCategoryTabListener?.onTabRemoved(category, position)
    }

    override fun tabUpdated(
        tab: TabLayout.Tab,
        category: Category<*>,
        position: Int
    ) {
        onCategoryTabListener?.onTabUpdated(tab, category, position)
    }
    // sorry for ugly code for now,  don't have time
    @SuppressLint("ClickableViewAccessibility")
    fun setupWithTabLayout(
        tabLayout: TabLayout,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        if (viewGroup.parent !is NestedScrollView) throw IllegalStateException("category layout has to be wrapped in CategoryScrollView to setup it with tabLayout")
        val scrollView = viewGroup.parent as CategoryScrollView
            if (tabLayoutManager == null) {
                tabLayoutManager = CategoryTabLayoutManager(tabLayout, CategoryTabFactory())
                tabLayoutManager!!.setOnTabUpdateListener { tab, category ->
                    true
                }
                tabLayoutManager!!.tabManagerListener = this@CategoryManager
                tabLayoutManager!!.setupWithCategoryView(
                    tabLayout,
                    categories,
                    listAdapter
                )
                var inTabClickScroll = false
                var isScrolling = false
                tabLayoutManager!!.setOnTabSelectedListener { tab, key ->
                    val pos = uiManager.findPositionInView(tab.view.id, 1)
                        ?: return@setOnTabSelectedListener
                    val view = viewGroup[pos]
                    if (!isScrolling) {
                        tabLayoutManager?.onTabSelect?.invoke()
                        inTabClickScroll = true
                        scrollView.smoothScrollTo(
                            0,
                            view.y.toInt(),
                            getContext().resources.getInteger(R.integer.category_layout_scroll_anim)
                        )
                    }
                }
                scrollView.setOnScrollStoppedListener(object : CategoryScrollView.OnScrollStopListener {
                    override fun onScrollStopped(y: Int) {
                        isScrolling = false
                    }

                })
                scrollView.setOnScrollChangeListener(object  : NestedScrollView.OnScrollChangeListener {
                    var lastPost: Int = -1
                    val rect = Rect()
                    override fun onScrollChange(
                        v: NestedScrollView?,
                        scrollX: Int,
                        scrollY: Int,
                        oldScrollX: Int,
                        oldScrollY: Int
                    ) {
                        if (inTabClickScroll) {
                            if (abs(scrollY - oldScrollY) == 1) {
                                inTabClickScroll = false
                                isScrolling = false
                            }
                            return
                        }
                            isScrolling = true
                            uiManager.categoryViews.forEach { key, value ->
                                val currentPos = value.id
                                rect.left = 0
                                rect.right = viewGroup.width
                                rect.top = value.views[1].y.toInt()
                                rect.bottom =  value.views.last().y.toInt()
                                if (rect.contains(5, scrollY)) {
                                    Log.d("RECTY", "WE ARE NOWIN $: ${value.category.id}")
                                    if (currentPos !=  lastPost && abs(scrollY - oldScrollY) > 10) {
                                        var tab: TabLayout.Tab? = null
                                        for (a in 0 until tabLayout.tabCount) {
                                            tab = tabLayout.getTabAt(a)
                                            if (tab?.view?.id == currentPos) break
                                        }
                                        tabLayout.selectTab(tab)
                                    }
                                }
                                lastPost = currentPos
                            }
                        }
                })
            } else {
                throw InstantiationException("tab layout is already integrated into category layout")
            }
    }

    fun clear() {
        tabLayoutManager?.clear()
    }

    private fun getContext() = uiManager.getContext()
}

infix fun List<Category<*>>.getCategoryPosition(category: Category<*>): Int? {
    this.forEachIndexed { index, c ->
        if (c.id == category.id) return index
    }
    return null
}

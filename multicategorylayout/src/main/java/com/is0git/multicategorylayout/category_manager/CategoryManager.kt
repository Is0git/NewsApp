package com.is0git.multicategorylayout.category_manager

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
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
    TabManagerListener,
    CategoryTabLayoutManager.AllTabListener {
    val categories: MutableList<Category<*>> = mutableListOf()
    var uiManager = CategoryUIManager(viewGroup)
    var onCategoryListener: OnCategoryListener? = null
    var tabLayoutManager: TabLayoutManager? = null
    var onCategoryTabListener: OnCategoryTabListener? = null
    var lifecycleOwner: LifecycleOwner? = null
    var allListAdapter: ListAdapter<out Any?, out RecyclerView.ViewHolder>? = null

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
        val position = categories.getCategoryPositionById(categoryId)
        if (position == null) return
        else {
            val category = categories[position]
            val categoryView = uiManager.categoryViews[position]
            val visibility = if (list.isEmpty()) {
                View.INVISIBLE
            } else {
                category.adapter.submitList(list as List<Nothing>)
                View.VISIBLE
            }
            if (tabLayoutManager != null && !tabLayoutManager!!.isAllEnabled) {
                for (v in categoryView.views) {
                    v.visibility = visibility
                }
            }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uiManager.removeCategoryView(category, position)
        }
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
        if (allListAdapter == null) allListAdapter = listAdapter
        if (viewGroup.parent !is NestedScrollView) throw IllegalStateException("category layout has to be wrapped in CategoryScrollView to setup it with tabLayout")
        val scrollView = viewGroup.parent as CategoryScrollView
        if (tabLayoutManager == null) {
            tabLayoutManager = CategoryTabLayoutManager(tabLayout, CategoryTabFactory()).also {
                it.allTabListener = this
            }
            tabLayoutManager!!.tabManagerListener = this@CategoryManager
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {}

                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    tabLayoutManager?.isAllEnabled = tab?.text == getContext().getString(R.string.all)
                    tabLayoutManager!!.onTabSelect?.invoke(tab, tabLayout.selectedTabPosition)
                }

            })
            tabLayoutManager!!.setupWithCategoryView(
                tabLayout,
                categories,
                listAdapter
            )
            var inTabClickScroll = false
            var isScrolling = false
            val listener = { tab: TabLayout.Tab, key: String ->
                if (tab.text == getContext().getString(R.string.all)) {
                    uiManager.categoryTransitionManager.show()
                    uiManager.allList!!.postDelayed(
                        {
                            scrollView.smoothScrollTo(0, 0)
                        },
                        400
                    )
                } else {
                    uiManager.categoryTransitionManager.hide()
                }
                val posY = uiManager.findCategoryViewById(tab.view.id)?.views?.get(1)?.y
                if (posY != null) {
                    if (!isScrolling) {
                        tabLayoutManager?.onTabSelect?.invoke(tab, tabLayout.selectedTabPosition)
                        inTabClickScroll = true
                        scrollView.postDelayed({
                            inTabClickScroll = false
                        }, 300)
                        scrollView.smoothScrollTo(
                            0,
                            posY.toInt(),
                            getContext().resources.getInteger(R.integer.category_layout_scroll_anim)
                        )
                    }
                }
            }
            tabLayoutManager!!.setOnTabSelectedListener(listener)
            scrollView.setScrollListener(object : CategoryScrollView.ScrollListener {
                override fun onScrollStart() {
                    Log.d("CategoryManager", "scroll start")
                }

                override fun onScrollEnd() {
                    isScrolling = false
                }

            })
            scrollView.setOnScrollChangeListener(object : NestedScrollView.OnScrollChangeListener {
                var lastPost: Int = -1
                val rect = Rect()
                override fun onScrollChange(
                    v: NestedScrollView?,
                    scrollX: Int,
                    scrollY: Int,
                    oldScrollX: Int,
                    oldScrollY: Int
                ) {
                    isScrolling = true
                    if (inTabClickScroll) {
                        return
                    }
                    if (uiManager.allList != null && tabLayout.selectedTabPosition == 0) return
                    uiManager.categoryViews.forEach { value ->
                        rect.left = 0
                        rect.right = viewGroup.width
                        rect.top = value.views[1].y.toInt()
                        rect.bottom = value.views.last().y.toInt()
                        if (rect.contains(0, scrollY)) {
                            val currentPos = value.id
                            if (currentPos != lastPost && abs(scrollY - oldScrollY) > 10) {
                                var tab: TabLayout.Tab? = null
                                for (a in 0 until tabLayout.tabCount) {
                                    tab = tabLayout.getTabAt(a)
                                    if (tab?.view?.id == currentPos) break
                                }
                                Log.d(
                                    "RECTY",
                                    "WE ARE NOWIN $: ${value.category.id}: istabclick: ${inTabClickScroll}"
                                )
                                tabLayout.selectTab(tab)
                            }
                            lastPost = currentPos
                        }
                    }
                }
            })
        } else {
            throw InstantiationException("tab layout is already integrated into category layout")
        }
    }

    fun selectTab(tab: TabLayout.Tab?) {
        tabLayoutManager?.also {
            if (it.isAllEnabled && tab?.text == getContext().getString(R.string.all)) {
                uiManager.categoryTransitionManager.showWithoutAnim()
            } else {
                uiManager.categoryTransitionManager.hideWithoutAnim()
            }
        }
        tabLayoutManager?.tabLayout?.selectTab(tab)
    }

    fun clear() {
        tabLayoutManager?.clear()
    }

    private fun getContext() = uiManager.getContext()

    override fun onAllTabAdded(tab: TabLayout.Tab) {
        if (allListAdapter == null) throw InstantiationException("all list adapter was not provided")
        uiManager.createAllCategoryList(tab.view.id, allListAdapter!!)
        if (tabLayoutManager!!.isAllEnabled) {
            if (tabLayoutManager!!.tabLayout.selectedTabPosition == 0) {
                viewGroup.post {
                    uiManager.categoryTransitionManager.showWithoutAnim()
                }
            } else {
                viewGroup.post {
                    uiManager.hideAllList()
                }
            }
        }
    }
}

infix fun List<Category<*>>.getCategoryPosition(category: Category<*>): Int? {
    this.forEachIndexed { index, c ->
        if (c.id == category.id) return index
    }
    return null
}

infix fun List<Category<*>>.getCategoryPositionById(categoryId: String): Int? {
    this.forEachIndexed { index, c ->
        if (c.id == categoryId) return index
    }
    return null
}


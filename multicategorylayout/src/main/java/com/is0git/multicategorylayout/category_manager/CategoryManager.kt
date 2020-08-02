package com.is0git.multicategorylayout.category_manager

import android.view.ViewGroup
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
import com.is0git.multicategorylayout.ui.tab_layout_integration.CategoryTabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.OnCategoryTabListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabManagerListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.CategoryTabFactory
import com.is0git.multicategorylayout.ui.ui_manager.CategoryUIManager
import com.is0git.multicategorylayout.ui.ui_manager.CategoryView
import com.is0git.multicategorylayout.ui.ui_manager.UIManagerListener
import kotlinx.coroutines.launch

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
    lateinit var tabSelectedListener: () -> Unit

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

    fun setupWithTabLayout(
        tabLayout: TabLayout,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        if (viewGroup.parent !is NestedScrollView) throw IllegalStateException("category layout has to be wrapped in NestedScrollView to setup it with tabLayout")
        val scrollView = viewGroup.parent as NestedScrollView
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

                tabLayoutManager!!.setOnTabSelectedListener { tab, key ->
                    val pos = uiManager.findPositionInView(tab.view.id, 1)
                        ?: return@setOnTabSelectedListener
                    val view = viewGroup[pos]
                    tabSelectedListener()
                    scrollView.smoothScrollTo(
                        0,
                        view.y.toInt(),
                        getContext().resources.getInteger(R.integer.category_layout_scroll_anim)
                    )
                }
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

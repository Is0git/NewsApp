package com.is0git.multicategorylayout.ui.category_layout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_controller.DefaultCategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_manager.CategoryManager
import com.is0git.multicategorylayout.category_manager.getCategoryPosition
import com.is0git.multicategorylayout.category_modifier.DefaultCategoryModifier
import com.is0git.multicategorylayout.listeners.OnCategoryListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.OnCategoryTabListener
import com.is0git.multicategorylayout.ui.ui_manager.CategoryView
import kotlinx.coroutines.launch


class CategoryLayout : ConstraintLayout,
    LifecycleObserver {

    private var categoryManager: CategoryManager =
        CategoryManager(this, DefaultCategoryModifier(), DefaultCategoryController())
    val count: Int
        get() {
            return categoryManager.getCount()
        }
    private var lifecycleOwner: LifecycleOwner? = null
        set(value) {
            field = value
            categoryManager.lifecycleOwner = field
        }


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    fun addCategories(categories: List<Category<*>>, lifeCycleOwner: LifecycleOwner) {
        addObserver(lifeCycleOwner)
            categoryManager.addCategories(categories)

    }

    private fun addObserver(lifeCycleOwner: LifecycleOwner) {
        if (categoryManager.lifecycleOwner == null) {
            this.lifecycleOwner = lifeCycleOwner
            lifeCycleOwner.lifecycle.addObserver(this)
        }
    }

    fun getCategoriesCount(): Int {
        return categoryManager.categories.count()
    }

    fun getCategoryGroupSize(): Int {
        return categoryManager.uiManager.categoryGroupSize
    }

    fun addCategory(category: Category<*>) {
        addCategory(category, (count - 1).coerceAtLeast(0))
    }

    private fun addCategory(category: Category<*>, position: Int) {
        lifecycleOwner?.lifecycleScope?.launch {
            categoryManager.addCategory(category, position)
        }
    }

    fun removeCategory(category: Category<*>) {
        val position = categoryManager.categories getCategoryPosition category
        if (position != null) categoryManager.removeCategory(position)
    }

    fun removeCategoryAt(position: Int) {
        categoryManager.removeCategory(position)
    }

    fun updateCategory(category: Category<*>) {
        val position = categoryManager.categories getCategoryPosition category
        if (position != null) categoryManager.updateCategory(position)
    }

    fun updateCategoryAt(position: Int) {
        categoryManager.updateCategory(position)
    }

    fun getCategoryById(id: String): Category<*>? {
        return categoryManager.categories[id]
    }

    fun getCategoryAt(position: Int): Category<*> {
        return categoryManager.categories.values.elementAt(position)
    }

    /**
     * make sure [items] collection type matches with type in adapter, else exception will be thrown
     */
    @Throws(IllegalStateException::class, TypeCastException::class)
    fun <T> updateAdapter(categoryId: String, items: List<T>) {
        categoryManager.updateCategoryAdapter(categoryId, items)
    }

    fun setOnCategoryTabListener(categoryTabListener: OnCategoryTabListener) {
        this.categoryManager.onCategoryTabListener = categoryTabListener
    }

    fun onTabSelectedListener(listener: () -> Unit) {
        categoryManager.tabSelectedListener = listener
    }

    fun setOnCategoryListener(onCategoryListener: OnCategoryListener) {
        this.categoryManager.onCategoryListener = onCategoryListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clear() {
        categoryManager.clear()
    }

    /**
     * if [listAdapter] is null, 'all item' tab won't be included]
     * [CategoryView] must be wrapped in [NestedScrollView] to use it with [TabLayout]
     */
    fun setupWithTabLayout(
        tabLayout: TabLayout,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        categoryManager.setupWithTabLayout(tabLayout, listAdapter)
    }

}
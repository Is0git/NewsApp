package com.is0git.multicategorylayout.ui.category_layout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.*
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.R
import com.is0git.multicategorylayout.category_controller.DefaultCategoryController
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.category_manager.CategoryManager
import com.is0git.multicategorylayout.category_modifier.DefaultCategoryModifier
import com.is0git.multicategorylayout.listeners.CategoryListener
import com.is0git.multicategorylayout.listeners.OnCategoryListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.CategoryTabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.OnCategoryTabListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabManagerListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.CategoryTabFactory
import com.is0git.multicategorylayout.ui.ui_manager.CategoryUIManager
import com.is0git.multicategorylayout.ui.ui_manager.CategoryView
import com.is0git.multicategorylayout.ui.ui_manager.UIManager
import com.is0git.multicategorylayout.ui.ui_manager.UIManagerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CategoryLayout : ConstraintLayout,
    LifecycleObserver {

    private var categoryManager: CategoryManager
    private var uiManager: UIManager
    private var tabLayoutManager: TabLayoutManager? = null
    private lateinit var categoryJob: Job
    private lateinit var lifecycleOwner: LifecycleOwner
    private lateinit var tabSelectedListener: () -> Unit
    private var onCategoryTabListener: OnCategoryTabListener? = null
    private var onCategoryListener: OnCategoryListener? = null
    val count: Int
        get() {
            return categoryManager.getCount()
        }

    init {
        val categoryModifier = DefaultCategoryModifier()
        val categoryController = DefaultCategoryController()
        uiManager = CategoryUIManager(this)
        // anonymous objects for now to make interface not to be accessible for the public, will clean this later...
        uiManager.uiManagerListener = object : UIManagerListener {
            override fun categoryViewCreated(categoryView: CategoryView, position: Int) {
                tabLayoutManager?.addTab(categoryView.category, position)
            }

            override fun categoryViewRemoved(categoryView: CategoryView, position: Int) {
                tabLayoutManager?.removeTab(categoryView.category, position)
            }

            override fun categoryViewUpdated(categoryView: CategoryView, position: Int) {
                tabLayoutManager?.removeTab(categoryView.category, position)
            }
        }
        val categoryListener: CategoryListener = object : CategoryListener {
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
        }
        categoryManager = CategoryManager(categoryModifier, categoryController, categoryListener)
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
        categoryJob = CoroutineScope(Dispatchers.Default).launch {
            categoryManager.addCategories(categories)
        }
    }

    private fun addObserver(lifeCycleOwner: LifecycleOwner) {
        if (!this::lifecycleOwner.isInitialized) {
            this.lifecycleOwner = lifeCycleOwner
            lifeCycleOwner.lifecycle.addObserver(this)
        }
    }

    fun getCategoriesCount(): Int {
        return categoryManager.categories.count()
    }

    fun getCategoryGroupSize(): Int {
        return uiManager.categoryGroupSize
    }

    fun addCategory(category: Category<*>) {
        addCategory(category, (count - 1).coerceAtLeast(0))
    }

    private fun addCategory(category: Category<*>, position: Int) {
        lifecycleOwner.lifecycleScope.launch {
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
        this.onCategoryTabListener = categoryTabListener
    }

    fun onTabSelectedListener(listener: () -> Unit) {
        tabSelectedListener = listener
    }

    fun setOnCategoryListener(onCategoryListener: OnCategoryListener) {
        this.onCategoryListener = onCategoryListener
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clear() {
        tabLayoutManager?.clear()
    }

    /**
     * if [listAdapter] is null, 'all item' tab won't be included]
     * [CategoryView] must be wrapped in [NestedScrollView] to use it with [TabLayout]
     */
    fun setupWithTabLayout(
        tabLayout: TabLayout,
        listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?
    ) {
        if (this.parent !is NestedScrollView) throw IllegalStateException("category layout has to be wrapped in NestedScrollView to setup it with tabLayout")
        val scrollView = this.parent as NestedScrollView
        lifecycleOwner.lifecycleScope.launch {
            if (tabLayoutManager == null) {
                tabLayoutManager = CategoryTabLayoutManager(tabLayout, CategoryTabFactory())
                tabLayoutManager!!.setOnTabUpdateListener { tab, category ->
                    true
                }
                tabLayoutManager!!.tabManagerListener = object : TabManagerListener {
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
                }
                tabLayoutManager!!.setupWithCategoryView(
                    tabLayout,
                    categoryManager.categories,
                    listAdapter
                )

                tabLayoutManager!!.setOnTabSelectedListener { tab, key ->
                    val pos = uiManager.findPositionInView(tab.view.id, 1)
                        ?: return@setOnTabSelectedListener
                    val view = get(pos)
                    tabSelectedListener()
                    scrollView.smoothScrollTo(
                        0,
                        view.y.toInt(),
                        resources.getInteger(R.integer.category_layout_scroll_anim)
                    )
                }
            } else {
                throw InstantiationException("tab layout is already integrated into category layout")
            }
        }
    }

    private infix fun MutableMap<String, out Category<*>>.getCategoryPosition(category: Category<*>): Int? {
        this.values.forEachIndexed { index, c ->
            if (c.id == category.id) return index
        }
        return null
    }
}
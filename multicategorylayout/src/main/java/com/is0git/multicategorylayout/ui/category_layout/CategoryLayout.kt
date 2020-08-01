package com.is0git.multicategorylayout.ui.category_layout

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.is0git.multicategorylayout.ui.CategoryUIManager
import com.is0git.multicategorylayout.ui.UIManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.CategoryTabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabLayoutManager
import com.is0git.multicategorylayout.ui.tab_layout_integration.TabManagerListener
import com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory.CategoryTabFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class CategoryLayout : ConstraintLayout,
    LifecycleObserver,
    TabManagerListener{

    private lateinit var categoryManager: CategoryManager<*>
    lateinit var uiCreator: UIManager<Category<*>>
    lateinit var tabLayoutManager: TabLayoutManager
    private var isInitialized = false
    lateinit var categoryJob: Job
    private lateinit var lifecycleOwner: LifecycleOwner

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun <T> init(lifeCycleOwner: LifecycleOwner) {
        if (!this::lifecycleOwner.isInitialized) {
            this.lifecycleOwner = lifeCycleOwner
            lifeCycleOwner.lifecycle.addObserver(this)
        }

        val categoryModifier = DefaultCategoryModifier<T>()
        val categoryController = DefaultCategoryController<T>()
        val categoryListener: CategoryListener<T> = object : CategoryListener<T> {
            override fun onCategoryChange(category: Category<T>) {

            }

            override fun onCategoryRemove(categoryId: String) {

            }

            override fun onCategoryAdded(category: Category<T>) {
                createCategoryGroup(category)
            }

        }
        uiCreator = CategoryUIManager<T>(this)
        categoryManager = CategoryManager(categoryModifier, categoryController, categoryListener)
        isInitialized = true
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> addCategories(categories: List<Category<T>>, lifeCycleOwner: LifecycleOwner) {
        init<T>(lifeCycleOwner)
       categoryJob = CoroutineScope(Dispatchers.Default).launch {
            (categoryManager as CategoryManager<T>).addCategories(categories)
        }
    }

    fun getCategoriesCount(): Int {
        return categoryManager.categories.count()
    }

    fun getCategoryGroupSize(): Int {
        return uiCreator.categoryGroupSize
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> updateAdapter(categoryId: String?, items: List<*>) {
        categoryId ?: return
        categoryManager.categories[categoryId]?.adapter?.submitList(items as List<Nothing>)
    }

    private fun createCategoryGroup(category: Category<*>) {
        uiCreator.createUI(category)
    }

    /**
     * if [listAdapter] is null, 'all item' tab won't included]
     */
    fun setupWithTabLayout(tabLayout: TabLayout, listAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>?) {
        lifecycleOwner.lifecycleScope.launch {
            if (!::tabLayoutManager.isInitialized) {
                tabLayoutManager = CategoryTabLayoutManager(tabLayout, CategoryTabFactory())
                tabLayoutManager.setOnTabUpdateListener { tab, category ->
                    true
                }
            }
            categoryJob.join()
            tabLayoutManager.setupWithCategoryView(
                tabLayout,
                categoryManager.categories,
                listAdapter
            )
        }
    }

    private  fun updateTabs() {
        lifecycleOwner.lifecycleScope.launch {
            tabLayoutManager.updateTabs(categoryManager.categories.values)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun clear() {
        if (!::tabLayoutManager.isInitialized) tabLayoutManager.clear()
    }

    override fun onTabAdded(tab: TabLayout.Tab, category: Category<*>) {
        if (tab.text == resources.getString(R.string.all_tab)) {

        }
    }
}
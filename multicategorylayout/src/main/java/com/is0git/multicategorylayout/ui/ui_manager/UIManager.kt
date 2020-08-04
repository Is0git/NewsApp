package com.is0git.multicategorylayout.ui.ui_manager

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.animation.CategoryTransitionManager
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.DefaultViewCreatorLoader
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.ViewCreatorLoader

// type is checked but compiler doesn't know it
@Suppress("unchecked_cast")
abstract class UIManager(protected val viewGroup: ViewGroup) {

    var viewCreatorLoader: ViewCreatorLoader = DefaultViewCreatorLoader()
    var categoryGroupSize: Int = 0
    var categoryViews = mutableListOf<CategoryView>()
    var uiManagerListener: UIManagerListener? = null
    var allList: RecyclerView? = null
    var isAllListHidden = false
    lateinit var categoryTransitionManager: CategoryTransitionManager

    fun createCategoryView(dataItem: Category<*>, position: Int) {
        defineViewCreators(viewCreatorLoader, dataItem)
        val views = createViews(dataItem)
        categoryGroupSize = views.count()
        views.forEach {
            it?.id = ViewCompat.generateViewId()
        }
        var realViewsCount = 0
        viewGroup.apply {
            for (v in views) {
                if (v != null) {
                    addView(v)
                    realViewsCount++
                } else {
                    views.remove(v)
                }
            }
        }
        if (realViewsCount > 0) {
            val categoryView =
                CategoryView(dataItem.categoryViewId, views as MutableList<View>, dataItem)
            categoryViews.add(position, categoryView)
            uiManagerListener?.categoryViewCreated(categoryView, position)
        }
        positionViews(views, position)
    }

    fun getContext(): Context {
        return viewGroup.context
    }

    fun getViewCreators() = viewCreatorLoader.viewCreators

    @RequiresApi(Build.VERSION_CODES.N)
    fun removeCategoryView(category: Category<*>, position: Int) {
        var categoryPositionInViewGroup: Int = 0
        val categoryView = categoryViews[position]
        for (i in 0 until viewGroup.childCount) {
            val categoryViewId = categoryView.views.first().id
            if (viewGroup.getChildAt(i).id == categoryViewId) {
                categoryPositionInViewGroup = i
                break
            }
        }
        viewGroup.removeViews(
            categoryPositionInViewGroup,
            categoryView.count
        )
        uiManagerListener?.categoryViewRemoved(categoryView, position)
        categoryViews.removeIf { it.id == category.categoryViewId }
        if (categoryViews.isEmpty()) return
        val mPosition = if (position > categoryViews.count() - 1) position - 1 else position
        val mCategory = categoryViews[mPosition]
        positionViews(mCategory.views, mPosition)

    }

    fun updateCategoryView(category: Category<*>, position: Int) {
        val categoryView = categoryViews.find { it.id == category.categoryViewId }
        if (categoryView == null) return
        else {
            // TODO: `update
            uiManagerListener?.categoryViewUpdated(categoryView, position)
        }
    }

    /**
     * returns null if position not found.
     * [basedItemPosition] is [CategoryView.views] position.
     */
    fun findCategoryViewById(id: Int): CategoryView? {
        return categoryViews.find { it.id == id}
    }

    fun findPositionById(id: Int): Int? {
       return findById(id)
    }

    private fun findById(id: Int): Int? {
        return run {
            for (v in 0 until viewGroup.childCount) {
                if (viewGroup.getChildAt(v).id == id) {
                    return@run v
                }
            }
            null
        }
    }

    fun hideAllList() {
        categoryTransitionManager.hide()
        isAllListHidden = true
    }

    fun showAllList() {
        categoryTransitionManager.show()
        isAllListHidden = false
    }

    abstract fun createViews(dataItem: Category<*>): MutableList<View?>
    abstract fun positionViews(views: List<View?>, position: Int)
    abstract fun defineViewCreators(creatorLoader: ViewCreatorLoader, item: Category<*>)
    abstract fun createAllCategoryList(id: Int,  mAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>)
}

/**
 * [category] is basically [CategoryView] metadata
 * [id] id is unique and same for all components(different views)
 * [views] reference to views specific category as views are added into constraint layout which has flat hierarchy and we need to contain views by ourselves
 */
data class CategoryView(val id: Int, val views: List<View>, val category: Category<*>) {
    val count: Int
        get() {
            return views.count()
        }
}
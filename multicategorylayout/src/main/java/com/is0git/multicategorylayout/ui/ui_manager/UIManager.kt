package com.is0git.multicategorylayout.ui.ui_manager

import android.content.Context
import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import androidx.core.util.contains
import androidx.core.view.ViewCompat
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.DefaultViewCreatorLoader
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.ViewCreatorLoader

// type is checked but compiler doesn't know it
@Suppress("unchecked_cast")
abstract class UIManager(protected val viewGroup: ViewGroup) {

    var viewCreatorLoader: ViewCreatorLoader = DefaultViewCreatorLoader()
    var categoryGroupSize: Int = 0
    var categoryViews = SparseArray<CategoryView>()
    var uiManagerListener: UIManagerListener? = null

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
            categoryViews.put(dataItem.categoryViewId, categoryView)
            uiManagerListener?.categoryViewCreated(categoryView, position)
        }
        positionViews(views, position)
    }

    fun getContext(): Context {
        return viewGroup.context
    }

    fun getViewCreators() = viewCreatorLoader.viewCreators

    fun removeCategoryView(category: Category<*>, position: Int) {
        var categoryPositionInViewGroup: Int = 0
        val categoryView = categoryViews.get(category.categoryViewId)
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
        categoryViews.remove(category.categoryViewId)
        val mCategory = categoryViews.valueAt(position)
        positionViews(mCategory.views, position)
        uiManagerListener?.categoryViewRemoved(categoryView, position)
    }

    fun updateCategoryView(category: Category<*>, position: Int) {
        if (!categoryViews.contains(category.categoryViewId)) return
        else {
            val categoryView = categoryViews.get(category.categoryViewId)
            // TODO: `update
            uiManagerListener?.categoryViewUpdated(categoryView, position)
        }
    }

    /**
     * returns null if position not found.
     * [basedItemPosition] is [CategoryView.views] position.
     */
    fun findPositionInView(position: Int, basedItemPosition: Int): Int? {
        val id = categoryViews[position].views[basedItemPosition].id
        return findById(id)
    }

    fun findPositionById(id: Int, basedItemPosition: Int): Int? {
        val viewId = categoryViews[id].views[basedItemPosition].id
       return findById(viewId)
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

    abstract fun createViews(dataItem: Category<*>): MutableList<View?>
    abstract fun positionViews(views: List<View?>, position: Int)
    abstract fun defineViewCreators(creatorLoader: ViewCreatorLoader, item: Category<*>)
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
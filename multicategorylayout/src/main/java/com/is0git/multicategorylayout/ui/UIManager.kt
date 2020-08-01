package com.is0git.multicategorylayout.ui

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import com.is0git.multicategorylayout.ui.view_creators.ViewCreator
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.DefaultViewCreatorLoader
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.ViewCreatorLoader

abstract class UIManager<T>(protected val viewGroup: ViewGroup) {

    var viewCreatorLoader: ViewCreatorLoader = DefaultViewCreatorLoader()
    var categoryGroupSize: Int = 0
    var categoryViews = mutableListOf<CategoryView>()

    fun createUI(dataItem: T) {
        defineViewCreators(viewCreatorLoader, dataItem)
        val views = createViews(dataItem)
        categoryGroupSize = views.count()
        views.forEach {
            it?.id = ViewCompat.generateViewId()
        }
        if (views.count() == 0) return
        positionViews(views)
        var realViewsCount = 0
        viewGroup.apply {
            for (v in views) {
                if (v != null) {
                    addView(v)
                    realViewsCount++
                }
            }
        }
        val firstNotNull = views.find { it != null }
        if (firstNotNull != null) {
            val categoryView = CategoryView(firstNotNull, realViewsCount)
            categoryViews.add(categoryView)
        }
    }

    fun getContext(): Context {
        return viewGroup.context
    }

    fun getViewCreators() = viewCreatorLoader.viewCreators

    abstract fun createViews(dataItem: T): List<View?>
    abstract fun positionViews(views: List<View?>)
    abstract fun defineViewCreators(creatorLoader: ViewCreatorLoader, item: T)
}

class CategoryView(val view: View, var count: Int)
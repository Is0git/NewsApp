package com.is0git.multicategorylayout.ui.ui_manager

import android.os.Build
import android.transition.Fade
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams.*
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.is0git.commonlibs.ScreenUnitUtils
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.animation.AllListAnimator
import com.is0git.multicategorylayout.ui.animation.CategoriesAnimator
import com.is0git.multicategorylayout.ui.animation.CategoryTransitionManager
import com.is0git.multicategorylayout.ui.view_creators.*
import com.is0git.multicategorylayout.ui.view_creators.view_creator_loader.ViewCreatorLoader

class CategoryUIManager(viewGroup: ViewGroup) : UIManager(viewGroup) {

    override fun defineViewCreators(creatorLoader: ViewCreatorLoader, item: Category<*>) {
        val headlineCreator = HeadLineCreator(getContext(), item)
        val viewAllButtonCreator = ViewAllButtonCreator(getContext(), item)
        val listViewCreator = ListViewCreator(getContext(), item.flags, item.adapter)
        var divider: ViewCreator? = null
        if (item.hasDivider) divider = DividerCreator(getContext(), item.dividerColor)
        creatorLoader.loadViewCreators(
            headlineCreator,
            viewAllButtonCreator,
            listViewCreator,
            divider
        )
    }

    override fun createAllCategoryList(
        id: Int,
        mAdapter: ListAdapter<out Any, out RecyclerView.ViewHolder>
    ) {
        val recyclerView = RecyclerView(getContext()).apply {
            this.id = id
            layoutParams =
                ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, MATCH_CONSTRAINT)
                    .apply {
                        topToTop = PARENT_ID
                        bottomToBottom = PARENT_ID
                    }
            layoutManager = GridLayoutManager(context, 3)
            adapter = mAdapter
        }
        allList = recyclerView
        createAnimators(allList!!)
        viewGroup.addView(recyclerView)
    }

    private fun createAnimators(list: RecyclerView) {
            categoryTransitionManager = CategoryTransitionManager(viewGroup, Fade())
            val allListAnimator = AllListAnimator(list)
            val categoriesAnimator = CategoriesAnimator(viewGroup, list.id)
            categoryTransitionManager.addAnimator(allListAnimator)
            categoryTransitionManager.addAnimator(categoriesAnimator)
    }

    override fun createViews(dataItem: Category<*>): MutableList<View?> {
        val views = mutableListOf<View?>()
        for (v in getViewCreators()) {
            val view = v?.createView()
            views.add(view)
        }
        return views
    }

    override fun positionViews(views: List<View?>, position: Int) {
        views[0]?.layoutParams = ConstraintLayout.LayoutParams(
            MATCH_CONSTRAINT,
            WRAP_CONTENT
        ).apply {
            when (position) {
                0 -> {
                    topToTop = PARENT_ID
                }
                else -> {
                    val lastChild = categoryViews[position - 1].views.last()
                    topToBottom = lastChild.id
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                marginStart = ScreenUnitUtils.convertDpToPixel(30f, getContext()).toInt()
            } else {
                leftMargin = ScreenUnitUtils.convertDpToPixel(30f, getContext()).toInt()
            }
            topMargin = ScreenUnitUtils.convertDpToPixel(35f, getContext()).toInt()
            startToStart = PARENT_ID
            endToStart = views[1]!!.id
        }
        views[1]?.layoutParams = ConstraintLayout.LayoutParams(
            WRAP_CONTENT,
            WRAP_CONTENT
        ).apply {
            topToTop = views[0]!!.id
            bottomToBottom = views[0]!!.id
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                marginEnd = ScreenUnitUtils.convertDpToPixel(30f, getContext()).toInt()
            } else {
                rightMargin = ScreenUnitUtils.convertDpToPixel(30f, getContext()).toInt()
            }
            endToEnd = PARENT_ID
        }
        views[2]?.layoutParams = ConstraintLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
            topToBottom = views[1]!!.id
            topMargin = ScreenUnitUtils.convertDpToPixel(10f, getContext()).toInt()
            bottomMargin = ScreenUnitUtils.convertDpToPixel(50f, getContext()).toInt()
        }
        val lastView: View = if (categoryViews[position].category.hasDivider) {
            views[3]!!.layoutParams = ConstraintLayout.LayoutParams(
                MATCH_PARENT,
                ScreenUnitUtils.convertDpToPixel(1f, getContext()).toInt()
            ).apply {
                topToBottom = views[2]!!.id
                topMargin = ScreenUnitUtils.convertDpToPixel(30f, getContext()).toInt()
            }
            views[3]!!
        } else {
            views[2]!!
        }
        if (categoryViews.count() - 1 > position) {
            categoryViews[position + 1].views.first()
                .updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = lastView.id
                }
        }
    }
}
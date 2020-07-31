package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.view.View
import com.is0git.multicategorylayout.R
import com.is0git.multicategorylayout.category_data.Category
import com.is0git.multicategorylayout.ui.CategoryUIFactory

class ViewAllButtonCreator(context: Context, var item: Category<*>) : ViewCreator(context) {
    override fun createView(): View {
        return CategoryUIFactory.createSeeAllButton(context, R.string.see_all)
    }
}
package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.view.View
import com.is0git.multicategorylayout.category_data.Category

class HeadLineCreator(context: Context, var item: Category<*>) : ViewCreator(context) {
    override fun createView(): View {
        return CategoryUIFactory.createTitleTextView(context, item.categoryName)
    }
}
package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.view.View
import androidx.annotation.ColorRes

class DividerCreator(context: Context, @ColorRes val dividerColorRes: Int) : ViewCreator(context) {
    override fun createView(): View {
        return CategoryUIFactory.createDivider(context, dividerColorRes)
    }
}
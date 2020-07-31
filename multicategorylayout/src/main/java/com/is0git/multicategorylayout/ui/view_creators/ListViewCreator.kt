package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.ListAdapter
import com.is0git.multicategorylayout.ui.CategoryUIFactory

class ListViewCreator(context: Context, val flags: Int, var adapter: ListAdapter<*, *>) : ViewCreator(context) {
    override fun createView(): View {
        return CategoryUIFactory.createRecyclerView(context, flags, adapter)
    }
}
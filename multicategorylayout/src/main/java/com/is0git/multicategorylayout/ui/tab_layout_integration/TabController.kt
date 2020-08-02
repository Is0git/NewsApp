package com.is0git.multicategorylayout.ui.tab_layout_integration

import com.is0git.multicategorylayout.category_data.Category

interface TabController {
    fun addTab(category: Category<*>, position: Int)
    fun removeTab(category: Category<*>, position: Int)
    fun updateTab(category: Category<*>, position: Int)
}
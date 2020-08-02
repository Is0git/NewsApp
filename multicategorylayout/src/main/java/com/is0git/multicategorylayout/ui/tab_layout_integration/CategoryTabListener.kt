package com.is0git.multicategorylayout.ui.tab_layout_integration

import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category

interface OnCategoryTabListener {
    fun onTabAdded(tab: TabLayout.Tab, category: Category<*>, position: Int)
    fun onTabRemoved(category: Category<*>, position: Int)
    fun onTabUpdated(tab: TabLayout.Tab, category: Category<*>, position: Int)
}
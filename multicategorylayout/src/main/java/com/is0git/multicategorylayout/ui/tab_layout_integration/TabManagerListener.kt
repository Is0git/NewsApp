package com.is0git.multicategorylayout.ui.tab_layout_integration

import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category

interface TabManagerListener {
    fun tabAdded(tab: TabLayout.Tab, category: Category<*>, position: Int)
    fun tabRemoved(category: Category<*>, position: Int)
    fun tabUpdated(tab: TabLayout.Tab, category: Category<*>, position: Int)
}
package com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory

import androidx.annotation.DrawableRes
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category

interface TabFactory {
    /**
     * tabLayout is need in order to create tabs since they are bound to tabLayout object
     */
    fun createTab(
        tabLayout: TabLayout,
        category: Category<*>,
        @DrawableRes iconId: Int? = null
    ): TabLayout.Tab
}
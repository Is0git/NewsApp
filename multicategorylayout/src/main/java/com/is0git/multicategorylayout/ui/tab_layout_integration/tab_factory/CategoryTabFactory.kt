package com.is0git.multicategorylayout.ui.tab_layout_integration.tab_factory

import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.tabs.TabLayout
import com.is0git.multicategorylayout.category_data.Category

class CategoryTabFactory : TabFactory<Category<*>> {
    override fun createTab(
        tabLayout: TabLayout,
        category: Category<*>,
        iconId: Int?
    ): TabLayout.Tab {
       val tab =  tabLayout.newTab()
            .setText(category.categoryName)
        if (iconId != null) {
            val drawable = ResourcesCompat.getDrawable(tabLayout.resources, iconId, tabLayout.context.theme)
            tab.icon = drawable
        }
        return tab
    }


}
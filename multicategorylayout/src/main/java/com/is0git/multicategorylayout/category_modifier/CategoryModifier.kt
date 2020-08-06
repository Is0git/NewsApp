package com.is0git.multicategorylayout.category_modifier

import com.is0git.multicategorylayout.category_data.Category

interface CategoryModifier {
    fun modifyCategory(categories: Category<*>)
    fun modifyCategories(categories: List<*>)
}
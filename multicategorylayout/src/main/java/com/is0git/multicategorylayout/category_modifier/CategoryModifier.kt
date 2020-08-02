package com.is0git.multicategorylayout.category_modifier

import com.is0git.multicategorylayout.category_data.Category

interface CategoryModifier {
    suspend fun modifyCategory(categories: Category<*>)
    suspend fun modifyCategories(categories: List<*>)
}
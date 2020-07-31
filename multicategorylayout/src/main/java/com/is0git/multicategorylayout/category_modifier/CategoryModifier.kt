package com.is0git.multicategorylayout.category_modifier

import com.is0git.multicategorylayout.category_data.Category

interface CategoryModifier<T> {
    suspend fun modifyCategory(categories: Category<T>)
    suspend fun modifyCategories(categories: List<T>)
}
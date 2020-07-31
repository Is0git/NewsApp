package com.is0git.multicategorylayout.category_modifier

import com.is0git.multicategorylayout.category_data.Category

internal class DefaultCategoryModifier<T> : CategoryModifier<T> {
    override suspend fun modifyCategories(categories: List<T>) {

    }

    override suspend fun modifyCategory(categories: Category<T>) {

    }
}
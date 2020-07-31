package com.is0git.multicategorylayout.category_controller

import com.is0git.multicategorylayout.category_data.Category


interface CategoryController<T> {
    /**
     *
     */
    suspend fun resetCategories(categories: MutableMap<String, Category<T>>)
    suspend fun updateCategories(categories: MutableMap<String, Category<T>>)
    suspend fun addCategory(category: Category<T>)
}
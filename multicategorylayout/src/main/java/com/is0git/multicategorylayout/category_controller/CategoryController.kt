package com.is0git.multicategorylayout.category_controller

import com.is0git.multicategorylayout.category_data.Category


interface CategoryController {
    /**
     *
     */
    suspend fun resetCategories(categories: MutableMap<String, Category<*>>)
    suspend fun updateCategories(categories: MutableMap<String, Category<*>>)
    suspend fun addCategory(category: Category<*>, position: Int)
}
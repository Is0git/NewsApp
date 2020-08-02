package com.is0git.multicategorylayout.category_controller

import com.is0git.multicategorylayout.category_data.Category


interface CategoryController {
    /**
     *
     */
    fun resetCategories(categories: MutableList<Category<*>>)
    fun updateCategories(categories: MutableList<Category<*>>)
    fun addCategory(categories: MutableList<Category<*>>)
}
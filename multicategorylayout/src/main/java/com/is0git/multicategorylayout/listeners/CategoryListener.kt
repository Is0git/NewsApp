package com.is0git.multicategorylayout.listeners

import com.is0git.multicategorylayout.category_data.Category


interface CategoryListener<T> {
    fun onCategoryChange(category: Category<T>)
    fun onCategoryRemove(categoryId: String)
    fun onCategoryAdded(category: Category<T>)
}
package com.is0git.multicategorylayout.listeners

import com.is0git.multicategorylayout.category_data.Category


interface CategoryListener {
    fun categoryChanged(category: Category<*>, position: Int)
    fun categoryRemoved(category: Category<*>, position: Int)
    fun categoryAdded(category: Category<*>, position: Int)
}

interface OnCategoryListener {
    fun onCategoryChanged(category: Category<*>, position: Int)
    fun onCategoryRemoved(category: Category<*>, position: Int)
    fun onCategoryAdded(category: Category<*>, position: Int)
}
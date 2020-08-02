package com.is0git.multicategorylayout.ui.ui_manager

interface OnCategoryListener {
    fun onCategoryCreated(id: Int, categoryView: CategoryView)
    fun onCategoryRemoved(id: Int, categoryView: CategoryView)
    fun onCategoryUpdated(id: Int, categoryView: CategoryView)
}
package com.is0git.multicategorylayout.ui.ui_manager

interface UIManagerListener {
    fun categoryViewCreated(categoryView: CategoryView, position: Int)
    fun categoryViewRemoved(categoryView: CategoryView, position: Int)
    fun categoryViewUpdated(categoryView: CategoryView, position: Int)
}
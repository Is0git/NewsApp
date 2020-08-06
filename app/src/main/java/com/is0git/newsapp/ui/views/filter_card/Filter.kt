package com.is0git.newsapp.ui.views.filter_card

import androidx.core.view.ViewCompat

class Filter(val id: Int, val displayName: String, val selection: Array<String>) {
    companion object {
        fun create(title: String, selection: Array<String>) =
            Filter(ViewCompat.generateViewId(), title, selection)
    }
}
package com.is0git.multicategorylayout.ui.view_creators

import android.content.Context
import android.view.View

abstract class ViewCreator(val context: Context) {
    abstract fun createView(): View
}
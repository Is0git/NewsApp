package com.is0git.multicategorylayout.ui.view_creators.view_creator_loader

import com.is0git.multicategorylayout.ui.view_creators.ViewCreator

abstract class ViewCreatorLoader {
    lateinit var viewCreators: Array<out ViewCreator?>

    abstract fun loadViewCreators(vararg items: ViewCreator?)
}